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
    
    public String getQC(String inboundWeatherData, String qcType) {
        
        JSONObject inboundWeatherDataAsJsonObject = new JSONObject(inboundWeatherData);
        
        WeatherDataObject weatherDataObject = new WeatherDataObject(inboundWeatherDataAsJsonObject);
        
        JSONArray locationWeatherData = weatherDataObject.getLocationWeatherData();
        JSONObject locationWeatherDataObject;
        
        JSONArray data;
       
        JSONArray weatherParameters = weatherDataObject.getWeatherParameters();
        
        int interval = weatherDataObject.getInterval();
        String timeStart = weatherDataObject.getTimeStart();
        String timeEnd = weatherDataObject.getTimeEnd();
        
        double  longitude;
        double  latitude;
        int     altitude;
        
        JSONArray qcResult;
        
        for (int i=0; i<locationWeatherData.length(); i++) {
            locationWeatherDataObject = locationWeatherData.getJSONObject(i);
            longitude = locationWeatherDataObject.getDouble("longitude");
            latitude = locationWeatherDataObject.getDouble("latitude");
            altitude = locationWeatherDataObject.getInt("altitude");
            data = locationWeatherDataObject.getJSONArray("data");
        
            switch(qcType) {
            
                // Simple prequalification for values -> TODO: Implement
                // Interval test for temperature, soil temperature and wind -> OK
                // Logical test for temperature, soil temperature, humidity and wind
                case "RT":
                    qcResult = getRtQC(data, interval, timeStart, timeEnd, weatherParameters, longitude, latitude, altitude);
                    locationWeatherDataObject.put("qc", qcResult);
                    inboundWeatherDataAsJsonObject.getJSONArray("locationWeatherData").put(i, locationWeatherDataObject);
                break;
            
                // Step test -> Needs input data for parameter specific thresholds
                // Freeze test -> OK
                case "NONRT":
                    qcResult = getNonRtQC(data, interval, timeStart, timeEnd, weatherParameters, longitude, latitude, altitude);
                    locationWeatherDataObject.put("qc", qcResult);
                    inboundWeatherDataAsJsonObject.getJSONArray("locationWeatherData").put(i, locationWeatherDataObject);
                break;
                
            }
        
        }
        
        return inboundWeatherDataAsJsonObject.toString();
        
    }
    
    private JSONArray getRtQC(JSONArray data, int interval, String timeStart, String timeEnd, JSONArray weatherParameters, double longitude, double latitude, int altitude) {
        
        List weatherParameterValuesAsList;
        
        JSONArray qcResult = new JSONArray();
        
        int testResult;
        
        int weatherParameter;
        
        for (int index=0; index<weatherParameters.length(); index++) {
            weatherParameter = weatherParameters.getInt(index);
            weatherParameterValuesAsList = getValuesAsList(data, index);
            //testResult = getIntervalTestResult(weatherParameterValuesAsList, weatherParameter);
            testResult = getPrequalificationTestResult(weatherParameterValuesAsList, weatherParameter);
            qcResult.put(getFinalTestResult(testResult));
        }
        
        return qcResult;
    }
    
    private int getPrequalificationTestResult(List weatherParameterValuesAsList, int weatherParameter) {
        
        int qcResult = 2;
        
        Iterator iterator = weatherParameterValuesAsList.iterator();
        
        Object parameterValue;
        String parameterClassName;
        
        while (iterator.hasNext()) {
            parameterValue = iterator.next();
            parameterClassName = parameterValue.getClass().getName();
            if (parameterClassName.equals("java.lang.String")) {
                return 4;
            }
        }
        
        return qcResult;
        
    }
    
    private int getIntervalTestResult(List weatherParameterValuesAsList, int weatherParameter) {
        
        int qcResult = 2;
        
        ThresholdData thresholdData = new ThresholdData();
        JSONObject lowerAndUpperLimits = thresholdData.getLowerAndUpperLimits(String.valueOf(weatherParameter));
        double lowerLimit = lowerAndUpperLimits.getDouble("lower_limit");
        double upperLimit = lowerAndUpperLimits.getDouble("upper_limit");
        double parameterValue;
        
        Iterator iterator = weatherParameterValuesAsList.iterator();
        
        while (iterator.hasNext()) {
            parameterValue = (double) iterator.next();
            if (parameterValue >= upperLimit || parameterValue <= lowerLimit) {
                return 8;
            }
        }
        
        return qcResult;
        
    }
    
    private JSONArray getNonRtQC(JSONArray data, int interval, String timeStart, String timeEnd, JSONArray weatherParameters, double longitude, double latitude, int altitude) {
        
        List weatherParameterValuesAsList;
        
        JSONArray qcResult = new JSONArray();
        
        int testResult;
        
        for (int index=0; index<weatherParameters.length(); index++) {
            weatherParameterValuesAsList = getValuesAsList(data, index);
            testResult = getFreezeTestResult(weatherParameterValuesAsList, index);
            qcResult.put(getFinalTestResult(testResult));
        }
        
        return qcResult;
    }
    
    private int getFreezeTestResult(List weatherParameterValuesAsList, int index) {
        HashSet unique = new HashSet(weatherParameterValuesAsList);
        if (unique.size() == 1 && weatherParameterValuesAsList.size() > 1) {
            return 64;
        } else {
            return 0;
        }
    }
    
    private int getLogicalTestResult(JSONArray data, int index, int weatherParameter, int interval, String timeStart, String timeEnd, double longitude, double latitude, int altitude) {
        return 0;
    }
    
    
    /*
     * This needs to be redrafted -> The parameter specific step threshold should come from threshold data
     */
    private int getStepTestResult(List weatherParameterValuesAsList, int index, int weatherParameter) {
        
        int returnValue = 0;
        
        return returnValue;
        
    }
    
    private int getParameterSpecificStepTestResult(List valuesAsList, int parameterSpecificThreshold) {
        
        if (valuesAsList.size() == 1) {
            return 0;
        }
        
        boolean testSuccess = true;
        
        Iterator<Double> iterator = valuesAsList.iterator();
        double parameterValue_current;
        double parameterValue_next;
        
        while (iterator.hasNext()) {
            parameterValue_current = iterator.next();
            if (iterator.hasNext()) {
                parameterValue_next = iterator.next();
            } else {
                parameterValue_next = parameterValue_current;
            }
            if (abs(parameterValue_next-parameterValue_current) > parameterSpecificThreshold) {
                testSuccess = false;
            }
        }
        
        if (testSuccess) {
            return 0;
        } else {
            return 32;
        }
        
    }
    
    private int getFinalTestResult(int testResult) {
        if (testResult == 0) {
            return 2;
        } else {
            return testResult;
        }
    }
    
    private List getValuesAsList(JSONArray data, int index) {
        List valuesAsList = new ArrayList();
        JSONArray dataItem;
        Object variableValue;
        for (int i=0; i<data.length(); i++) {
            dataItem = data.getJSONArray(i);
            variableValue = dataItem.get(index);
            valuesAsList.add(variableValue);
        }
        return valuesAsList;
    }
    
}
