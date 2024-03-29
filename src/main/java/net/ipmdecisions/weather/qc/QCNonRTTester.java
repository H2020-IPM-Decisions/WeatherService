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

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import net.ipmdecisions.weather.entity.QCType;
import net.ipmdecisions.weather.qc.util.QCHelpers;
import net.ipmdecisions.weather.qc.util.QCTestType;
import org.json.JSONObject;

/**
 * Collection of static methods for doing quality control checks
 * for non real time weather data.
 */
public class QCNonRTTester {
    
    /**
     * Default value for longest valid freeze (in hours) that a parameter is 
     * allowed to have.
     * 
     * Used in Freeze QC test as a default value, when a weather parameter does 
     * not have `freeze_test_threshold` defined in `threshoddata.json`.
     */
    private static final Double defaultFreezeLengthThresholdValue = 5.0;

    public QCNonRTTester() { 
    }

    /**
     * Step test result for weather data parameter values. 
     * 
     * The step thresholds are defined in the threshold data.
     * 
     * > "Comparison between the present values and the previous one results in 
     *    an error flag when the difference equals or exceeds the parameter
     *    specific threshold. The threshold will be dependent on the parameter
     *    type.
     *    
     *    Cannot fail for precipitation. Huge step is possible." -subtask 2.1.3 documentation.
     * 
     * @param weatherParameterValues Weather parameter values
     * @param weatherParameter Weather parameter
     * @return QC result as int
     * Returns
     * - 32 if fails
     * - 0 if not tested, or no errors found.
     */
    public static int getStepTestResult(Double[] weatherParameterValues, int weatherParameter) {
        //Default QC response is 0.
        int returnValue = QCType.NO_QC;

        //ThresholdData object
        ThresholdData thresholdData = new ThresholdData();
        //Weather data parameter key specific threshold data object
        JSONObject thresholdDataObject = thresholdData.getThresholdDataObject(String.valueOf(weatherParameter));

        //Run the test if the threshold data exists.
        if (thresholdDataObject.has("step_test_threshold") && thresholdDataObject.has("step_test_threshold_type")) {
            //Threshold value from threshold data.
            double thresholdValue = thresholdDataObject.getDouble("step_test_threshold");
            //Variable for step test type. This is weather data parameter specific and
            //is either absolute or relative.
            String thresholdType = thresholdDataObject.getString("step_test_threshold_type");
            returnValue = getStepTestResult(weatherParameterValues, thresholdValue, thresholdType);
        }
        
        return returnValue;
    }
    
    
    /**
     * Runs the actual step test based on variable values (previous and current) and
     * test type as well as the threshold value in question
     * @param weatherParameterValuesAsList Weather data parameter values as List
     * @param thresholdValue Threshold value as double
     * @param thresholdType Threshold type as string being either absolute or relative
     * @return QC result as integer
     * Returns
     * - 0 if success
     * - 32 if fail
     */
    public static int getStepTestResult(Double[] weatherParameterValues, double thresholdValue, String thresholdType) {

        //Weather data parameter value as double
        Double weatherParameterValue;
        //Previous weather data parameter value for comparison
        Double previousWeatherParameterValue = null;
        //Test values for relative model
        Double testValue_max;
        Double testValue_min;
        
        // If there is only one weather data parameter value the step test cannor run
        //and returns 0 (no quality control performed)
        if (weatherParameterValues.length == 1) {
            return QCType.NO_QC;
        //Step test
        } else {
            //Loop for weather data parameter values
            for(int i=0;i<weatherParameterValues.length;i++)
            {
                weatherParameterValue = weatherParameterValues[i];
                if(weatherParameterValue == null)
                {
                	continue;
                }

                //If counter > 1 the step test can be performed (we now have current value and previous value)
                if (previousWeatherParameterValue != null) {
                    switch (thresholdType) {
                        case "absolute":
                            //Step test for absolute type where the threshold is + or - n units
                            //Immediate abort if the test fails
                            if (abs(previousWeatherParameterValue-weatherParameterValue) >= thresholdValue) {
                                return QCType.FAILED_STEP_TEST;
                            }
                            break;
                        case "relative":
                            //Calculate the current MAX value based on threshold and previous weather data parameter value
                            testValue_max = previousWeatherParameterValue + ((previousWeatherParameterValue/100) * thresholdValue);
                            //Calculate the current MIN value based on threshold and previous weather data parameter value
                            testValue_min = previousWeatherParameterValue - ((previousWeatherParameterValue/100) * thresholdValue);
                            //Step test for relative type where the threshold is + or - n presentage
                            //Abort with return value 32 if the test fails
                            if (weatherParameterValue >= testValue_max || weatherParameterValue <= testValue_min) {
                                return QCType.FAILED_STEP_TEST;
                            }
                            break;
                    }
                }
                //Set the previous weather data parameter value
                previousWeatherParameterValue = weatherParameterValue;
            }
        }
        
        //Return OK if test passes and reaches this point
        return QCType.NO_QC;
    }

    /**
     * Freeze test for weather data parameter values. 
     * 
     * > "This test verifies the number of hours with equal values. 
     *   The threshold will vary with type of parameter. For instance, the same
     *   temperature over several hours is not likely compared to the same 
     *   humidity value. The same amount of rain is not likely at all, if we
     *   look at values above 0.0 mm.
     *   
     *   A duplicate check is not sufficient. There could be two or three hours 
     *   with the same value, more than five consecutive hours is unlikely.
     *   
     *   Cannot fail for precipitation = 0mm." - subtask 2.1.3 documentation
     * 
     * 
     * @param weatherParameterValues Weather data parameter values
     * @param weatherParameter weather parameter
     * @return QC result as Integer
     * Returns
     * - 64 if fails
     * - 0 if success
     */
    public static Integer testForFreezeErrors(Double[] weatherParameterValues, Integer weatherParameter) {
        if (weatherParameterValues.length <= 1) {
            return QCType.NO_QC;
        }

        Double value = null;
        Double previousValue = null;
        int count = 0;
        Double thresholdValue = QCHelpers.getThresholdValueForWeatherParameter(weatherParameter, "freeze_test_threshold");

        if (thresholdValue == null) {
            thresholdValue = QCNonRTTester.defaultFreezeLengthThresholdValue;
        }
        

        // loop through and compare current value to its previous value
        for (int i=0; i < weatherParameterValues.length; i++) {
            value = weatherParameterValues[i];
            if(value == null)
            {
            	continue;
            }
            // Treat the first value and the case of differing values the same way: reset comparison.
            if (previousValue == null || Double.compare(previousValue, value) != 0) {
                previousValue = value;
                count = 1;
                continue;
            }

            count++;

            // special case: precipitation with 0.0mm cannot fail
            if (QCHelpers.isWeatherParameterPrecipitation(weatherParameter) && Double.compare(value, 0.0) == 0) {
                continue;
            }

            // When the same value repeats more than `thresholdValue` times 
            // (by default that is 5 times), the data is possibly erroneous,
            // and fails this QC test.
            if (count > thresholdValue) return QCType.FAILED_FREEZE_TEST;
        }

        return QCType.NO_QC;
    }
}
