package net.ipmdecisions.weather.qc;

import java.util.ArrayList;
import java.util.HashSet;
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
    
    public JSONObject getQC(JSONObject inboundWeatherDataAsJsonObject) {
        
        WeatherDataObject weatherDataObject = new WeatherDataObject(inboundWeatherDataAsJsonObject);
        
        JSONArray locationWeatherData = weatherDataObject.getLocationWeatherData();
        JSONObject locationWeatherDataObject;
        
        JSONArray data;
       
        JSONArray weatherParameters = weatherDataObject.getWeatherParameters();
        int interval = weatherDataObject.getInterval();
        
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
            qcResult = getParameterTypeBasedQC(data, interval, weatherParameters, longitude, latitude, altitude);
            locationWeatherDataObject.put("qc", qcResult);
            inboundWeatherDataAsJsonObject.getJSONArray("locationWeatherData").put(i, locationWeatherDataObject);
        }
        
        return inboundWeatherDataAsJsonObject;
        
    }
    
    private JSONArray getParameterTypeBasedQC(JSONArray data, int interval, JSONArray weatherParameters, double longitude, double latitude, int altitude) {
        
        JSONArray qcResult = new JSONArray();
        
        int testResult;
        
        for (int index=0; index<weatherParameters.length(); index++) {
            switch (weatherParameters.getInt(index)) {
                case 1002:
                    testResult = getFreezeTestResult(data, index);
                    qcResult.put(getFinalTestResult(testResult));
                    break;
                case 3002:
                    testResult = getFreezeTestResult(data, index);
                    qcResult.put(getFinalTestResult(testResult));
                    break;
                default:
                    qcResult.put(0);
            }
        }
        
        return qcResult;
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
    
    private int getFreezeTestResult(JSONArray data, int index) {
        List valuesAsList = getValuesAsList(data, index);
        HashSet unique = new HashSet(valuesAsList);
        if (unique.size() == 1) {
            return 64;
        } else {
            return 0;
        }
    }
    
    private void getQC_1002(JSONArray data, int index, int interval, double longitude, double latitude, int altitude) {
        
    }
    
}
