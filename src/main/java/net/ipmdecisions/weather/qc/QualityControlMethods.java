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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.LocationWeatherDataException;
import net.ipmdecisions.weather.entity.QCType;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.qc.QCRTTester;
import net.ipmdecisions.weather.qc.QCNonRTTester;
import net.ipmdecisions.weather.qc.util.QCHelpers;
import net.ipmdecisions.weather.qc.util.QCTestType;

import org.json.JSONArray;

/**
 *
 * @author Markku Koistinen, LUKE
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class QualityControlMethods {
        
    public QualityControlMethods() {
        
    }
    
    public WeatherData getQC(String inboundWeatherData, String qcType) throws JsonMappingException, JsonProcessingException
    {
    	 WeatherData weatherData = WeatherData.getInstanceFromString(inboundWeatherData);
		 return this.getQC(weatherData, qcType);
    	
    }
    
    /**
     * Access point for quality control methods being the only public method in the class
     * @param inboundWeatherData Weather data as JSON Object as String
     * @param qcType Quality control type 
     * @return Inbound weather data added with quality control JSON array in each as String
     * locationWeatherData object
     */
    public WeatherData getQC(WeatherData weatherData, String qcType) {
        
        //Location weather data array from inbound weather data
        List<LocationWeatherData> locationWeatherData = weatherData.getLocationWeatherData();
        //Location weather data object def
        LocationWeatherData locationWeatherDataObject;
        //Weather parameters as JSON array from inpound weather data
        Integer[] weatherParameters = weatherData.getWeatherParameters();
        
        
        //Iterate through location weather data array
        for (int i=0; i<locationWeatherData.size(); i++) {
            //Location weather data object into variable
            locationWeatherDataObject = locationWeatherData.get(i);
            //Decision tree based on data type (either real time or non-real time)
            switch(qcType) {
                //Real time weather data QC
                case "RT":
                	// TODO: Check that this object is changed in the WeatherData "mother object"
                	locationWeatherDataObject.setQC(this.getRtQC(weatherParameters, locationWeatherDataObject));
                break;
                //Non-real time weather data QC
                case "NONRT":
                     //Non-real time QC result to qcResult JSONArray
                	locationWeatherDataObject.setQC(getNonRtQC(weatherParameters, locationWeatherDataObject));
                break;
                
            }
        
        }
        
        return weatherData;
        
    }
    
    
    /**
     * Real time quality control main method. The real time QC includes interval test. 
     * The test result values are combined bitmapped integer values.
     * @param data USED -> Location weather data object weather parameter value array
     * @param interval Not used but implemented for future use. Sampling interval value
     * @param timeStart Not used but implemented for future use. Sampling start datetime 
     * @param timeEnd Not used but implemented for future use. Sampling end datetime
     * @param weatherParameters USED -> Inbound weather data object weather parameter key array
     * @param longitude Not used but implemented for future use. Geolocation variables
     * @param latitude Not used but implemented for future use. Geolocation variables
     * @param altitude Not used but implemented for future use. Geolocation variables
     * @return QC result as JSONArray
     */
    private Integer[] getRtQC(Integer[] weatherParameters, LocationWeatherData locationWeatherData) {

        //Weather parameter values in specific index as List
        //Basically jsonArray[index] -> List
        Double[] weatherParameterValues;

        //Quality control result as JSONArray
        Integer[] qcResult = new Integer[weatherParameters.length];
        
        //Quality control test result
        int testResult;

        //Weather parameter (from weatherParameters[index])
        int weatherParameter;
        
        //Iterate the weather parameter key array and couple weather data parameter keys and values
        for (int index=0; index<weatherParameters.length; index++) {
            try
            {
                testResult = QCType.NO_QC;
                //Weather parameter from weather parameter array
                weatherParameter = weatherParameters[index];
                //Index pointed weather data parameter values into List
                weatherParameterValues = locationWeatherData.getColumn(index);

                testResult = testResult | QCRTTester.getPrequalificationTestResult(weatherParameterValues, weatherParameter);

                // We can only do other tests if prequalification test didn't fail, as value needs to be a number.
                if (testResult == QCType.NO_QC) {
                    testResult = testResult | QCRTTester.getIntervalTestResult(weatherParameterValues, weatherParameter);

                    // If none of the tests have failed, we can declare the data as valid.
                    if (testResult == QCType.NO_QC) {
                        testResult = QCType.OK_FROM_IPM_DECISIONS;
                    }
                }
                //Put the final test result into qcResult
                //getFinalRestResult(int) returns 2 if the final result remains 0
                qcResult[index] = testResult;
            }
            catch(LocationWeatherDataException ex)
            {
                    // Pass
            }
        }
        
        Integer[] qcResultLogical = QCRTTester.testForLogicalErrors(locationWeatherData.getData(), weatherParameters, qcResult);
        
        for (Integer i=0; i<qcResult.length; i++) {
            qcResult[i] = qcResult[i] | qcResultLogical[i];
        }
        
        return qcResult;
    }
    
    /**
     * Non-real time quality control main method. The non-real time QC includes step test
     * and freeze test. The test result values are combined bitmapped integer values. 
     * @param data USED -> Location weather data object weather parameter value array
     * @param interval Not used but implemented for future use. Sampling interval value
     * @param timeStart Not used but implemented for future use. Sampling start datetime 
     * @param timeEnd Not used but implemented for future use. Sampling end datetime
     * @param weatherParameters USED -> Inbound weather data object weather parameter key array
     * @param longitude Not used but implemented for future use. Geolocation variables
     * @param latitude Not used but implemented for future use. Geolocation variables
     * @param altitude Not used but implemented for future use. Geolocation variables
     * @return QC result as JSONArray
     */
    private Integer[] getNonRtQC(Integer[] weatherParameters, LocationWeatherData locationWeatherData) {
        
        //Weather parameter values in specific index as List
        //Basically jsonArray[index] -> List
        Double[] weatherParameterValues;
        
        //Quality control result as JSONArray
        Integer[] qcResult = new Integer[weatherParameters.length];
        
        //Quality control test result
        int testResult;
        
        //Weather parameter (from weatherParameters[index])
        int weatherParameter;
        
        //Iterate the weather parameter key array and couple weather data parameter keys and values
        for (int index=0; index<weatherParameters.length; index++) {
        	try
        	{
                    testResult = QCType.NO_QC;
	            //Weather parameter from weather parameter array
	            weatherParameter = weatherParameters[index];
	            //Index pointed weather data parameter values into List
	            weatherParameterValues = locationWeatherData.getColumn(index);

	            //Freeze test
                    if (QCHelpers.isWeatherParameterHandledByQCTest(weatherParameter, QCTestType.STEP)) {
                        testResult = testResult | QCNonRTTester.testForFreezeErrors(weatherParameterValues, weatherParameter);
                    }

                    //Step test
                    if (QCHelpers.isWeatherParameterHandledByQCTest(weatherParameter, QCTestType.STEP)) {
                        testResult = testResult | QCNonRTTester.getStepTestResult(weatherParameterValues, weatherParameter);
                    }

	            //Put the final test result into qcResult
	            //getFinalRestResult(int) returns 2 if the final result remains 0
                    
                    if (testResult == 0) testResult = QCType.OK_FROM_IPM_DECISIONS;
                    
	            qcResult[index] = testResult;
        	}
        	catch(LocationWeatherDataException ex)
        	{
        		// Pass
        	}
        }
        
        return qcResult;
    }
    
    
    /**
     * Logical test is not implemented
     * @param data Not used. Location weather data object weather parameter value array
     * @param index Not used. Index of the weather parameter key
     * @param weatherParameter Not used. Weather data parameter key
     * @param interval Not used. Sampling interval value
     * @param timeStart Not used. Sampling start datetime 
     * @param timeEnd Not used. Sampling end datetime
     * @param longitude Not used. Geolocation variables
     * @param latitude Not used. Geolocation variables
     * @param altitude Not used. Geolocation variables
     * @return 0 as int
     */
    private int getLogicalTestResult(JSONArray data, int index, int weatherParameter, int interval, String timeStart, String timeEnd, double longitude, double latitude, int altitude) {
        return QCType.NO_QC;
    }


    /**
     * @deprecated
     * Final test result correction from 0 to 2 in the case the test could be performed but
     * the test returned 0
     * @param testResult QC test result as int
     * @return Corrected QC test result as int
     * Returns
     * - original qc test result if the value is > 0
     * - 2 if the original qc test result is 0
     */
    private int getFinalTestResult(int testResult) {
        if (testResult == 0) {
            return QCType.OK_FROM_IPM_DECISIONS;
        } else {
            return testResult;
        }
    }
    
}
