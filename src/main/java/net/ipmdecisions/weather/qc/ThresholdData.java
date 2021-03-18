/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ipmdecisions.weather.qc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ThresholdData class provides the threshold data for quality control methods.
 * This release implements file based threshold data.
 * @author 03080928
 */
public class ThresholdData {
    
    public ThresholdData() {}
    
    /**
     * Returns threshold data object related to weather data parameter key (parameterID)
     * @param parameterID Weather data parameter key
     * @return Threshold data object as JSONObject if found
     */
    public JSONObject getThresholdDataObject(String parameterID) {
        
        return findFromJSONArray(getThresholdData("RESOURCE_READER", new JSONObject("{\"resource_path\":\"/thresholddata.json\"}")), parameterID);
          
    }
    
    
    /**
     * Threshold data search functions
     * @param thresholdData Threshold data as JSONArray
     * @param searchString Weather data parameter key
     * @return Threshold data object as JSONObject if found
     */
    private JSONObject findFromJSONArray(JSONArray thresholdData, String searchString) {
        
        //Threshold data object
        JSONObject thresholdDataItem;
        //Return value as JSONObject. Set to be {} if search has no match
        JSONObject returnObject = new JSONObject("{}");
        
        //Parameter id JSONArray from threshold data
        JSONArray parameterIdArray;
        //Individual parameter id from JSONArray
        String compareValue;
        
        //Iterate threshold data objects
        for (int index=0; index < thresholdData.length(); index++) {
            //Set threshold data object
            thresholdDataItem = thresholdData.getJSONObject(index);
            //Parameter id array from threshold data
            parameterIdArray = thresholdDataItem.getJSONArray("id_array");
            //Iterate through parameter id array
            for (int arrInd=0; arrInd < parameterIdArray.length(); arrInd++) {
                //Compare value from parameter array
                compareValue = parameterIdArray.getString(arrInd);
                //Compare test
                if (compareValue.equals(searchString)) {
                    returnObject = thresholdDataItem;
                }
            }
        }
        
        return returnObject;
        
    }
    
    
    /**
     * Returns threshold raw data as JSONArray from requested source.
     * This release implements resource reader.
     * @param reader Requested source. This release implements RESOURCE_READER
     * @param params Reader specific parameters as JSONObject. This release expects
     * only key value pair resource_path but this might also include DB access
     * directives as well as instructions for REST requests. Readers can be additional
     * methods or external classes.
     * @return Threshold data as JSONArray
     */
    private JSONArray getThresholdData(String reader, JSONObject params) {
        
        //Threshold data as string
        String thresholdData = "";
        
        //Threshold data reader selection
        switch(reader) {
            //Resource reader
            case "RESOURCE_READER":
                //Threshold data using resource reader method
                thresholdData = thresholdDataResourceReader(params);
                break;
            default:
                //REturn empty array if requested method is not implemented
                thresholdData = "[]";
                break;
        }
        
        return new JSONArray(thresholdData);
        
    }
    
    
    /**
     * Resource reader for threshold data
     * @param params Paramters for resource reader. Method expects JSONObject
     * with the resource_path key value pair.
     * @return 
     */
    private String thresholdDataResourceReader(JSONObject params) {
        
        //Threshold data as String
        String thresholdDataAsString = "";
        
        //Resource path from params JSONObject
        String resourcePath = params.getString("resource_path");
        
        //Resource reader with anomaly and exception handling
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
            InputStreamReader sr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(sr);
            String line;
            while ((line = br.readLine()) != null) {
                thresholdDataAsString += line;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            return "[]";
        }
        
        if (thresholdDataAsString.length() == 0) {
            thresholdDataAsString = "[]";
        }
        
        return thresholdDataAsString;
        
    }
    
}
