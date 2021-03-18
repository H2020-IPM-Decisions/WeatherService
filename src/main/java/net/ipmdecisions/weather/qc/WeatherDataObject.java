package net.ipmdecisions.weather.qc;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Weather data JSON -> Object
 * @author 03080928
 */
public class WeatherDataObject {
    
    private String      timeStart;
    private String      timeEnd;
    private int         interval;
    private JSONArray   weatherParameters;
    private JSONArray   locationWeatherData;
    
    public WeatherDataObject() {
        
    }
    
    public WeatherDataObject(JSONObject inboundWeatherDataAsJsonObject) {
        this.timeStart = inboundWeatherDataAsJsonObject.getString("timeStart");
        this.timeEnd = inboundWeatherDataAsJsonObject.getString("timeEnd");
        this.interval = inboundWeatherDataAsJsonObject.getInt("interval");
        this.weatherParameters = inboundWeatherDataAsJsonObject.getJSONArray("weatherParameters");
        this.locationWeatherData = inboundWeatherDataAsJsonObject.getJSONArray("locationWeatherData");
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public JSONArray getWeatherParameters() {
        return weatherParameters;
    }

    public void setWeatherParameters(JSONArray weatherParameters) {
        this.weatherParameters = weatherParameters;
    }

    public JSONArray getLocationWeatherData() {
        return locationWeatherData;
    }

    public void setLocationWeatherData(JSONArray locationWeatherData) {
        this.locationWeatherData = locationWeatherData;
    }
    
    
    
}
