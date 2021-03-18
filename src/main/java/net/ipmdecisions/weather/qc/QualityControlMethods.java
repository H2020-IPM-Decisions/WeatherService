package net.ipmdecisions.weather.qc;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 *
 * @author 03080928
 */
public class QualityControlMethods {
    
    public QualityControlMethods() {
        
    }
    
    /**
     * Access point for quality control methods being the only public method in the class
     * @param inboundWeatherData Weather data as JSON Object as String
     * @param qcType Quality control type 
     * @return Inbound weather data added with quality control JSON array in each as String
     * locationWeatherData object
     */
    public String getQC(String inboundWeatherData, String qcType) {
        
        //Convert inbound weather data into JSON Object
        JSONObject inboundWeatherDataAsJsonObject = new JSONObject(inboundWeatherData);
        
        //Init WeatherDataObject Object
        WeatherDataObject weatherDataObject = new WeatherDataObject(inboundWeatherDataAsJsonObject);
        
        //Location weather data array from inbound weather data
        JSONArray locationWeatherData = weatherDataObject.getLocationWeatherData();
        //Location weather data object def
        JSONObject locationWeatherDataObject;
        //Data (weather parameter values) from location weather data object def
        JSONArray data;
       
        //Weather parameters as JSON array from inpound weather data
        JSONArray weatherParameters = weatherDataObject.getWeatherParameters();
        
        //Weather data interval in seconds from inbound weather data
        int interval = weatherDataObject.getInterval();
        
        //Inbound weather data temporal limits
        String timeStart = weatherDataObject.getTimeStart();
        String timeEnd = weatherDataObject.getTimeEnd();
        
        //Location weather data geolocation specific parameters
        double  longitude;
        double  latitude;
        int     altitude;
        
        //Quality control result as JSONArray. This Array receives the QC results
        //from relevant methods and puts it into locationWeatherData object.
        JSONArray qcResult;
        
        //Iterate through location weather data array
        for (int i=0; i<locationWeatherData.length(); i++) {
            //Location weather data object into variable
            locationWeatherDataObject = locationWeatherData.getJSONObject(i);
            //Geolocation specific parameters into variables
            longitude = locationWeatherDataObject.getDouble("longitude");
            latitude = locationWeatherDataObject.getDouble("latitude");
            altitude = locationWeatherDataObject.getInt("altitude");
            //Weather data parameter array into variable
            data = locationWeatherDataObject.getJSONArray("data");
        
            //Decision tree based on data type (either real time or non-real time)
            switch(qcType) {
                //Real time weather data QC
                case "RT":
                    //Real time QC result to qcResult JSONArray
                    qcResult = getRtQC(data, interval, timeStart, timeEnd, weatherParameters, longitude, latitude, altitude);
                    //QC result into location weather data object and locationWeatherDataObject into inbound weather data
                    locationWeatherDataObject.put("qc", qcResult);
                    inboundWeatherDataAsJsonObject.getJSONArray("locationWeatherData").put(i, locationWeatherDataObject);
                break;
                //Non-real time weather data QC
                case "NONRT":
                     //Non-real time QC result to qcResult JSONArray
                    qcResult = getNonRtQC(data, interval, timeStart, timeEnd, weatherParameters, longitude, latitude, altitude);
                    locationWeatherDataObject.put("qc", qcResult);
                    //QC result into location weather data object and locationWeatherDataObject into inbound weather data
                    inboundWeatherDataAsJsonObject.getJSONArray("locationWeatherData").put(i, locationWeatherDataObject);
                break;
                
            }
        
        }
        
        return inboundWeatherDataAsJsonObject.toString();
        
    }
    
    
    /**
     * Real time quality control main method. The real time QC includes prequalification test
     * and interval test. The test result values are combined bitmapped integer values.
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
    private JSONArray getRtQC(JSONArray data, int interval, String timeStart, String timeEnd, JSONArray weatherParameters, double longitude, double latitude, int altitude) {
        
        //Weather parameter values in specific index as List
        //Basically jsonArray[index] -> List
        List weatherParameterValuesAsList;
        
        //Quality control result as JSONArray
        JSONArray qcResult = new JSONArray();
        
        //Quality control test result
        int testResult;
        
        //Weather parameter (from weatherParameters[index])
        int weatherParameter;
        
        //Iterate the weather parameter key array and couple weather data parameter keys and values
        for (int index=0; index<weatherParameters.length(); index++) {
            //Weather parameter from weather parameter array
            weatherParameter = weatherParameters.getInt(index);
            //Index pointed weather data parameter values into List
            weatherParameterValuesAsList = getValuesAsList(data, index);
            //Prequalification test to check the non-numerical values
            testResult = getPrequalificationTestResult(weatherParameterValuesAsList, weatherParameter);
            //Prequalification test passes returning 2
            if (testResult == 2) {
                //Interval test result
                testResult += getIntervalTestResult(weatherParameterValuesAsList, weatherParameter);
            }
            //Put the final test result into qcResult
            //getFinalRestResult(int) returns 2 if the final result remains 0
            qcResult.put(getFinalTestResult(testResult));
        }
        
        return qcResult;
    }
    
    
    /**
     * Prequalification test for weather data parameter values. The test fails if any of the
     * values in List is non-numeric
     * @param weatherParameterValuesAsList Weather data parameter values
     * @param weatherParameter Weathe data parameter key
     * @return QC result as integer.
     * Returns
     * - 4 if fails
     * - 2 if success
     */
    private int getPrequalificationTestResult(List weatherParameterValuesAsList, int weatherParameter) {
        
        //QC result return variable. Default is set as fail
        int qcResult = 2;
        
        //Weather parameter value List Iterator
        Iterator iterator = weatherParameterValuesAsList.iterator();
        
        //Parameter value as Object for object type test
        Object parameterValue;
        
        //Iterate through parameter value List
        //Abort immediately if non-numeric value is found (return 4)
        while (iterator.hasNext()) {
            //Parameter value from iterator
            parameterValue = iterator.next();
            //Parameter value object type. Only String is tested
            if (parameterValue instanceof String) {
                return 4;
            }
        }
        
        return qcResult;
        
    }
    
    
    /**
     * Interval test for weather data parameter values. Interval test is performed agains predefined
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
    private int getIntervalTestResult(List weatherParameterValuesAsList, int weatherParameter) {
        
        //QC result return variable. Default is set as fail
        int qcResult = 2;
        
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
        
        //Weather data parameter value iterator
        Iterator iterator = weatherParameterValuesAsList.iterator();
        
        //Weather parameter value iterator loop
        while (iterator.hasNext()) {
            //Weather data parameter value as double
            parameterValue = ((Number)iterator.next()).doubleValue();
            //Interval test. Weather data parameter values higher than upper limit
            //or lower than lower limit causes abort with exit code 8 (QC pass false)
            if (parameterValue > upperLimit || parameterValue < lowerLimit) {
                return 8;
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
    private JSONArray getNonRtQC(JSONArray data, int interval, String timeStart, String timeEnd, JSONArray weatherParameters, double longitude, double latitude, int altitude) {
        
        //Weather parameter values in specific index as List
        //Basically jsonArray[index] -> List
        List weatherParameterValuesAsList;
        
        //Quality control result as JSONArray
        JSONArray qcResult = new JSONArray();
        
        //Quality control test result
        int testResult;
        
        //Weather parameter (from weatherParameters[index])
        int weatherParameter;
        
        //Iterate the weather parameter key array and couple weather data parameter keys and values
        for (int index=0; index<weatherParameters.length(); index++) {
            //Weather parameter from weather parameter array
            weatherParameter = weatherParameters.getInt(index);
            //Index pointed weather data parameter values into List
            weatherParameterValuesAsList = getValuesAsList(data, index);
            //Freeze test
            testResult = getFreezeTestResult(weatherParameterValuesAsList, index);
            //Step test
            testResult += getStepTestResult(weatherParameterValuesAsList,weatherParameter);
            //Put the final test result into qcResult
            //getFinalRestResult(int) returns 2 if the final result remains 0
            qcResult.put(getFinalTestResult(testResult));
        }
        
        return qcResult;
    }
    
    
    /**
     * Freeze test for weather data parameter values. Chacks the dublicates in
     * specific weather data parameter values List. Note, that this algorithm
     * checks the whole data array and does not enable temporal filtering!
     * @param weatherParameterValuesAsList Weather data parameter values as List
     * @param index Index of weather parameter key to be tested as int
     * @return QC result as integer
     * Returns
     * - 64 if fails
     * - 2 if success
     */
    private int getFreezeTestResult(List weatherParameterValuesAsList, int index) {
        //Weather data parameter values List into HashSet for dublicate check
        HashSet unique = new HashSet(weatherParameterValuesAsList);
        if (unique.size() == 1 && weatherParameterValuesAsList.size() > 1) {
            return 64;
        } else {
            return 2;
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
        return 0;
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
    private int getStepTestResult(List weatherParameterValuesAsList, int weatherParameter) {
        
        //Default QC response is set to pass with return value 2
        int returnValue = 2;
        
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
            returnValue =  getStepTestRun(weatherParameterValuesAsList, thresholdValue, thresholdDataObject.getString("step_test_threshold_type"));
        } else {
            returnValue = 0;
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
    private int getStepTestRun(List weatherParameterValuesAsList, double thresholdValue, String thresholdType) {
        
        //Weather data parameter value iterator
        Iterator iterator = weatherParameterValuesAsList.iterator();
        
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
        if (weatherParameterValuesAsList.size() == 1) {
            return 0;
        //Step test
        } else {
            //Loop for weather data parameter values
            while (iterator.hasNext()) {
                //Weather data parameter value conversion to double using parseDouble(String)
                weatherParameterValueAsString = iterator.next().toString();
                weatherParameterValue = Double.parseDouble(weatherParameterValueAsString);
                counter ++;
                //If counter > 1 the step test can be performed (we now have current value and previous value)
                if (counter > 1) {
                    switch (thresholdType) {
                        case "absolute":
                            //Step test for absolute type where the threshold is + or - n units
                            //Immediate abort if the test fails
                            if (abs(previousWeatherParameterValue-weatherParameterValue) > thresholdValue) {
                                return 32;
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
                                return 32;
                            }
                            break;
                    }
                }
                //Set the previous weather data parameter value
                previousWeatherParameterValue = weatherParameterValue;
            }
        }
        
        return 2;
    }
    
    
    /**
     * Deprecated
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
            return 2;
        } else {
            return testResult;
        }
    }
    
    
    /**
     * Converts array data into List with values pointed by the index
     * @param data Weather data array from locationWeatherData object in incoming
     * weather data
     * @param index Data column to be returned
     * @return Data column pointed by the given index as List
     */
    private List getValuesAsList(JSONArray data, int index) {
        //New ArrayList object for weather data parameter column value
        List valuesAsList = new ArrayList();
        //Data item in JSONArray data
        JSONArray dataItem;
        //Variable value as Object
        Object variableValue;
        //Iterate through data
        for (int i=0; i<data.length(); i++) {
            //Data item
            dataItem = data.getJSONArray(i);
            //Variable value from array
            variableValue = dataItem.get(index);
            //Variable append to List
            valuesAsList.add(variableValue);
        }
        return valuesAsList;
    }
    
}
