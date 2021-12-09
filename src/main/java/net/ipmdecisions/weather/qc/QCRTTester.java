/*
 * Copyright (c) 2021 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of IPMDecisionsWeatherService.
 * IPMDecisionsWeatherService is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * IPMDecisionsWeatherService is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with IPMDecisionsWeatherService.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package net.ipmdecisions.weather.qc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import net.ipmdecisions.weather.entity.QCType;
import net.ipmdecisions.weather.qc.util.QCTestType;
import net.ipmdecisions.weather.qc.util.QCHelpers;
import org.json.JSONObject;

/**
 * Collection of static methods for doing quality control checks
 * for real time weather data.
 */
public class QCRTTester {
    
    public QCRTTester() { 
    }

    /**
     * Prequalification test for weather data parameter values.
     * The prequalification of an observation is a verification of the actual value.
     * If the actual value is not a number, the test fails.
     * 
     * @todo
     * 
     * @param weatherParameterValuesAsList Weather data parameter values as List
     * @param weatherParameter Weather data parameter key
     * @return QC result as integer
     * Returns
     * - 4 if fails
     * - 0 if success
     */
    public static int getPrequalificationTestResult(Double[] weatherParameterValues, int weatherParameter) {

        if(!QCHelpers.isWeatherParameterHandledByQCTest(weatherParameter, QCTestType.INTERVAL)) {
            return QCType.NO_QC;
        }

        for (int i=0;i<weatherParameterValues.length;i++) {
            // Given that we get values as Double type, the only non-number value is null.
            if (weatherParameterValues[i] == null) {
                return QCType.FAILED_NAN;                
            }
        }

        return QCType.NO_QC;
    }
    
    /**
     * Interval test for weather data parameter values. Interval test is performed against predefined
     * upper and lower limits. The threshold data is accessed using ThresholdData class.
     * This release implements file based rules but builds foundation for getting the
     * rules form REST or other type external source.
     * @param weatherParameterValuesAsList Weather data parameter values as List
     * @param weatherParameter Weather data parameter key
     * @return QC result as integer
     * Returns
     * - 8 if fails
     * - 0 if success
     */
    public static int getIntervalTestResult(Double[] weatherParameterValues, int weatherParameter) {
        
        //QC result return variable. Default is set as no qc done.
        //This way a single test won't qualify anything, but passing all tests qualifies.
        int qcResult = QCType.NO_QC;

        if(!QCHelpers.isWeatherParameterHandledByQCTest(weatherParameter, QCTestType.INTERVAL)) {
            return qcResult;
        }
        
        //Threshold data Object
        ThresholdData thresholdData = new ThresholdData();
        //Threshold data as JSONObject
        JSONObject lowerAndUpperLimits = thresholdData.getThresholdDataObject(String.valueOf(weatherParameter));

        //Lower limit value
        double lowerLimit = lowerAndUpperLimits.getDouble("lower_limit");
        //Upper limit value
        double upperLimit = lowerAndUpperLimits.getDouble("upper_limit");

        //Weather data parameter value placeholder
        Double parameterValue;

        //Weather parameter value iterator loop
        for(int i=0;i<weatherParameterValues.length;i++) 
        {
            //Weather data parameter value as double
            parameterValue = weatherParameterValues[i];
            
            if (parameterValue == null) continue;
            
            //Interval test. Weather data parameter values higher than upper limit
            //or lower than lower limit causes abort with exit code 8 (QC pass false)
            if (parameterValue > upperLimit || parameterValue < lowerLimit) {
                return QCType.FAILED_INTERVAL_TEST;
            }
        }
        
        return qcResult;
        
    }

    /**
     * Logical test for weather data parameter values.
     * 
     * The logic test is performed on a set of hourly data, where we have min, mean and max values.
     * 
     * The test can be used for:
     * 
     * - Temperature
     * - Soil temperature
     * - Humidity
     * - Wind
     * 
     * When the `minValue =< meanValue =< maxValue` the logical test result is set to passed.
     * 
     * Returns a list of QC results, one value for each weather parameter.
     * 
     * @param weatherParameterValuesAsList Weather data parameter values as List
     * @param weatherParameters List of weather data parameter keys
     * @return list of QC results as integers
     * Returns values in list:
     * - 16 if fails
     * - 0 if success
     */
    public static Integer[] testForLogicalErrors(Double[][] weatherParameterValuesByTime, Integer[] weatherParameters, Integer[] qcResult) {
        
        Integer[] qcResults = new Integer[weatherParameters.length];
        //QC result return variable. Default is set as no qc done.
        //This way a single test won't qualify anything, but passing all tests qualifies.
        Arrays.fill(qcResults, QCType.NO_QC);
        
        Double[][] valuesById = QCHelpers.switchRowsAndColumnsForLocationWeatherData(weatherParameterValuesByTime, weatherParameters.length);

        // Find weather parameters that are checked by the logical QC test.
        Integer[] weatherParametersToHandle = QCHelpers.filterWeatherParametersBasedOnQCType(weatherParameters, QCTestType.LOGICAL);
        
        Integer[][] parameterTuples = QCHelpers.getLogicalTuplesFromWeatherParameters(weatherParametersToHandle);

        List<Integer[]> tuplesList = new ArrayList<Integer[]>(Arrays.asList(parameterTuples));
        tuplesList.stream().forEach((Integer[] tuple) -> {
            Double[] means = null;
            Double[] mins = null;
            Double[] maxs = null;

            if (tuple[0] != null) { 
                means = valuesById[tuple[0]];
            }
            if (tuple[1] != null) { 
                mins = valuesById[tuple[1]];
            }
            if (tuple[2] != null) { 
                maxs = valuesById[tuple[2]];
            }

            for (Integer i=0; i < parameterTuples.length; i++) {
                // check that minValue <= maxValue
                if (mins != null && maxs != null && mins[i] != null && maxs[i] != null  && mins[i] > maxs[i]) {
                    qcResults[tuple[1]] = QCType.FAILED_LOGIC_TEST;
                    qcResults[tuple[2]] = QCType.FAILED_LOGIC_TEST;
                }
                // check that minValue <= mean
                if (means != null && mins != null && mins[i] != null && means[i] != null  && mins[i] > means[i]) {
                    qcResults[tuple[0]] = QCType.FAILED_LOGIC_TEST;
                    qcResults[tuple[1]] = QCType.FAILED_LOGIC_TEST;
                }
                // check that mean <= maxValue
                if (means != null && maxs != null && means[i] != null && maxs[i] != null  && means[i] > maxs[i]) { 
                    qcResults[tuple[0]] = QCType.FAILED_LOGIC_TEST;
                    qcResults[tuple[2]] = QCType.FAILED_LOGIC_TEST;
                }
            }
        });

        return qcResults;
    }

}
