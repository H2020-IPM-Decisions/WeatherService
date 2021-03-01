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
 *
 * @author 03080928
 */
public class ThresholdData {
    
    public ThresholdData() {}
    
    public JSONObject getLowerAndUpperLimits(String parameterID) {
        
        return findFromJSONArray(getThresholdData(), parameterID);
          
    }
    
    private JSONObject findFromJSONArray(JSONArray thresholdData, String searchString) {
        
        JSONObject thresholdDataItem;
        JSONObject returnObject = new JSONObject("{}");
        JSONArray parameterIdArray;
        String compareValue;
        
        for (int index=0; index < thresholdData.length(); index++) {
            thresholdDataItem = thresholdData.getJSONObject(index);
            parameterIdArray = thresholdDataItem.getJSONArray("id_array");
            for (int arrInd=0; arrInd < parameterIdArray.length(); arrInd++) {
                compareValue = parameterIdArray.getString(arrInd);
                if (compareValue.equals(searchString)) {
                    returnObject = thresholdDataItem;
                }
            }
        }
        
        return returnObject;
        
    }
    
    private JSONArray getThresholdData() {
        
        String thd = "";
        JSONArray jsonObject;
        
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("/thresholddata.json");
            //thd = getClass().getResource("/thresholddata.json").toString();
            InputStreamReader sr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(sr);
            String line;
            while ((line = br.readLine()) != null) {
                thd += line;
            }
            
        } catch (Exception e) {
            System.out.println(e.toString());
            return new JSONArray("[]");
        }
        return new JSONArray(thd);
        
    }
    
}
