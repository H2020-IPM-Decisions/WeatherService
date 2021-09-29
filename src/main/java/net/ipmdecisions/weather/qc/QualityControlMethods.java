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
	        	testResult = 0;
	            //Weather parameter from weather parameter array
	            weatherParameter = weatherParameters[index];
	            //Index pointed weather data parameter values into List
	            weatherParameterValues = locationWeatherData.getColumn(index);
	            
	            //Interval test result
	            testResult = testResult | getIntervalTestResult(weatherParameterValues, weatherParameter);
	            
	            //Put the final test result into qcResult
	            //getFinalRestResult(int) returns 2 if the final result remains 0
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
     * Interval test for weather data parameter values. Interval test is performed against predefined
     * upper and lower limits. The threshold data is accessed using ThresholdData class.
     * This release implements file based rules but builds foundation for getting the
     * rules form REST or other type external source.
     * @param weatherParameterValuesAsList Weather data parameter values as List
     * @param weatherParameter Weather data parameter key
     * @return QC result as integer
     * Returns
     * - 8 if fails
     * - 2 if success
     */
    private int getIntervalTestResult(Double[] weatherParameterValues, int weatherParameter) {
        
        //QC result return variable. Default is set as pass
        int qcResult = QCType.OK_FROM_IPM_DECISIONS;
        
        //Threshold data Object
        ThresholdData thresholdData = new ThresholdData();
        //Threshold data as JSONObject
        JSONObject lowerAndUpperLimits = thresholdData.getThresholdDataObject(String.valueOf(weatherParameter));
        //Lower limit value
        double lowerLimit = lowerAndUpperLimits.getDouble("lower_limit");
        //Upper limit value
        double upperLimit = lowerAndUpperLimits.getDouble("upper_limit");
        //Weather data parameter value placeholder
        double parameterValue;
        
        
        //Weather parameter value iterator loop
        for(int i=0;i<weatherParameterValues.length;i++) 
        {
            //Weather data parameter value as double
            parameterValue = weatherParameterValues[i];
            //Interval test. Weather data parameter values higher than upper limit
            //or lower than lower limit causes abort with exit code 8 (QC pass false)
            if (parameterValue > upperLimit || parameterValue < lowerLimit) {
                return QCType.FAILED_INTERVAL_TEST;
            }
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
	        	testResult = 0;
	            //Weather parameter from weather parameter array
	            weatherParameter = weatherParameters[index];
	            //Index pointed weather data parameter values into List
	            weatherParameterValues = locationWeatherData.getColumn(index);
	            //Freeze test
	            testResult = getFreezeTestResult(weatherParameterValues);
	            //Step test
	            testResult = testResult | getStepTestResult(weatherParameterValues,weatherParameter);
	            //Put the final test result into qcResult
	            //getFinalRestResult(int) returns 2 if the final result remains 0
	            qcResult[index] = testResult;
        	}
        	catch(LocationWeatherDataException ex)
        	{
        		// PAss
        	}
        }
        
        return qcResult;
    }
    
    
    /**
     * Freeze test for weather data parameter values. Checks the duplicates in
     * specific weather data parameter values List. Note, that this algorithm
     * checks the whole data array and does not enable temporal filtering!
     * @param weatherParameterValuesAsList Weather data parameter values as List
     * @param index Index of weather parameter key to be tested as int
     * @return QC result as integer
     * Returns
     * - 64 if fails
     * - 2 if success
     */
    private Integer getFreezeTestResult(Double[] weatherParameterValues) {
        //Weather data parameter values List into HashSet for dublicate check
        HashSet<Double> unique = new HashSet(Arrays.asList(weatherParameterValues));
        if (unique.size() == 1 && weatherParameterValues.length > 1) {
            return QCType.FAILED_FREEZE_TEST;
        } else if (weatherParameterValues.length == 1) {
            return QCType.NO_QC;
        } else {
            return QCType.OK_FROM_IPM_DECISIONS;
        }
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
     * Step test result for weather data parameter values. The steps are defined in
     * the threshold data that is accessed using ThresholdData class. This method
     * is host for intGetStepTestRun(params) method.
     * @param weatherParameterValuesAsList Weather data parameter values as List
     * @param weatherParameter Weather data paramteter key as int
     * @return QC result as int
     * Returns
     * - 32 if fails (the teest is performed in the int getStepTestRun(params) method)
     * - 2 if success
     * - 0 if not tested
     */
    private int getStepTestResult(Double[] weatherParameterValues, int weatherParameter) {
        
        //Default QC response is set to pass with return value 2
        int returnValue = QCType.OK_FROM_IPM_DECISIONS;
        
        //ThresholdData object
        ThresholdData thresholdData = new ThresholdData();
        //Weather data parameter key specific threshold data object
        JSONObject thresholdDataObject = thresholdData.getThresholdDataObject(String.valueOf(weatherParameter));
        
        //Variable for step test type. This is weather data prameter specific and
        //is either absolute or relative
        String stepTestType;
        //Threshold value from threshold data
        double thresholdValue;
        
        //Run the test if the threshold data exists
        if (thresholdDataObject.has("step_test_threshold")) {
            //Threshold value as double
            thresholdValue = thresholdDataObject.getDouble("step_test_threshold");
            //Threshold test. Threshold test type is retrieved from threshold data object from the key step_test_threshold_type
            returnValue =  getStepTestRun(weatherParameterValues, thresholdValue, thresholdDataObject.getString("step_test_threshold_type"));
        } else {
            returnValue = QCType.NO_QC;
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
     * - 2 if success
     * - 32 if fail
     */
    private int getStepTestRun(Double[] weatherParameterValues, double thresholdValue, String thresholdType) {
        
   
        //Parameter value from iterator as String
        String weatherParameterValueAsString;
        //Weather data parameter value as double
        double weatherParameterValue;
        //Previous weather data parameter value for comparison
        double previousWeatherParameterValue = 0.0;
        //Test values for relative model
        double testValue_max;
        double testValue_min;
        //Counter for while loop
        int counter = 0;
        
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
                counter ++;
                //If counter > 1 the step test can be performed (we now have current value and previous value)
                if (counter > 1) {
                    switch (thresholdType) {
                        case "absolute":
                            //Step test for absolute type where the threshold is + or - n units
                            //Immediate abort if the test fails
                            if (abs(previousWeatherParameterValue-weatherParameterValue) > thresholdValue) {
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
        return QCType.OK_FROM_IPM_DECISIONS;
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
