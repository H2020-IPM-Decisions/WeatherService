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
    
    private final static Integer STEP_TEST_1002_TRESHOLD = 10;
    private final static Integer STEP_TEST_3002_TRESHOLD = 50;
    
    public QualityControlMethods() {
        
    }
    
    public JSONObject getQC(JSONObject inboundWeatherDataAsJsonObject) {
        
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
            qcResult = getParameterTypeBasedQC(data, interval, timeStart, timeEnd, weatherParameters, longitude, latitude, altitude);
            locationWeatherDataObject.put("qc", qcResult);
            inboundWeatherDataAsJsonObject.getJSONArray("locationWeatherData").put(i, locationWeatherDataObject);
        }
        
        return inboundWeatherDataAsJsonObject;
        
    }
    
    private JSONArray getParameterTypeBasedQC(JSONArray data, int interval, String timeStart, String timeEnd, JSONArray weatherParameters, double longitude, double latitude, int altitude) {
        
        List weatherParameterValuesAsList;
        
        JSONArray qcResult = new JSONArray();
        
        int testResult;
        
        for (int index=0; index<weatherParameters.length(); index++) {
            switch (weatherParameters.getInt(index)) {
                case 1002:
                    weatherParameterValuesAsList = getValuesAsList(data, index);
                    testResult = getFreezeTestResult(weatherParameterValuesAsList, index) + getStepTestResult(weatherParameterValuesAsList, index, 1002);
                    qcResult.put(getFinalTestResult(testResult));
                    break;
                case 3002:
                    weatherParameterValuesAsList = getValuesAsList(data, index);
                    testResult = getFreezeTestResult(weatherParameterValuesAsList, index) + getStepTestResult(weatherParameterValuesAsList, index, 3002);
                    qcResult.put(getFinalTestResult(testResult));
                    break;
                default:
                    qcResult.put(0);
            }
        }
        
        return qcResult;
    }
    
    private int getIntervalTestResult(JSONArray data, int index, int weatherParameter, int interval, String timeStart, String timeEnd, double longitude, double latitude, int altitude) {
        return 0;
    }
    
    private int getLogicalTestResult(JSONArray data, int index, int weatherParameter, int interval, String timeStart, String timeEnd, double longitude, double latitude, int altitude) {
        return 0;
    }
    
    private int getFreezeTestResult(List weatherParameterValuesAsList, int index) {
        HashSet unique = new HashSet(weatherParameterValuesAsList);
        if (unique.size() == 1 && weatherParameterValuesAsList.size() > 1) {
            return 64;
        } else {
            return 0;
        }
    }
    
    private int getStepTestResult(List weatherParameterValuesAsList, int index, int weatherParameter) {
        
        int returnValue = 0;
        
        switch (weatherParameter) {
            case 1002:
                returnValue = getParameterSpecificStepTestResult(weatherParameterValuesAsList, this.STEP_TEST_1002_TRESHOLD);
                break;
            case 3002:
                returnValue = getParameterSpecificStepTestResult(weatherParameterValuesAsList, this.STEP_TEST_3002_TRESHOLD);
                break;
            default:
        }
        
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
        double variableValue;
        for (int i=0; i<data.length(); i++) {
            dataItem = data.getJSONArray(i);
            variableValue = dataItem.getDouble(index);
            valuesAsList.add(variableValue);
        }
        return valuesAsList;
    }
    
}
