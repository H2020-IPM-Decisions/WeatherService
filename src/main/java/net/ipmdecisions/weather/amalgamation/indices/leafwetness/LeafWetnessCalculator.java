/*
 * Copyright (c) 2022 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of IPM Decisions Weather Service.
 * IPM Decisions Weather Service is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * IPM Decisions Weather Service is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with IPM Decisions Weather Service.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.ipmdecisions.weather.amalgamation.indices.leafwetness;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ipmdecisions.weather.amalgamation.indices.IndiceCalculator;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 *
 */
public class LeafWetnessCalculator implements IndiceCalculator{
	private static Logger LOGGER = LoggerFactory.getLogger(LeafWetnessCalculator.class);

	@Override
	public WeatherData calculateIndice(WeatherData weatherData, Integer weatherParameter) {
		// TODO: Check available source parameters in the weather data object
		// Based on this: Select the preferred method.
		// Currently (June 2022) we only have one, then constant RH method
                
                List<Integer> tmParamsInDataset = this.getParamsInDataSet(weatherData,List.of(1001,1002,1021,1022));
                List<Integer> rhParamsInDataset = this.getParamsInDataSet(weatherData,List.of(3001,3002));
                List<Integer> rrParamsInDataset = this.getParamsInDataSet(weatherData,List.of(2001));
                List<Integer> wsParamsInDataset = this.getParamsInDataSet(weatherData,List.of(4002,4003));
                
                
                if (rhParamsInDataset != null && tmParamsInDataset != null && rrParamsInDataset != null && wsParamsInDataset != null){
                    try {
                        weatherData = this.calculateFromLSTM(weatherData);
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(LeafWetnessCalculator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    weatherData = this.calculateFromConstantRH(weatherData);
                }
                 
                return weatherData;
	} 
	
	/**
	 * 
	 * The RH >= 87% method TODO: Add references??
	 * 
	 * @param weatherData
	 * @return
	 */
	public WeatherData calculateFromConstantRH(WeatherData weatherData)
	{
		//LOGGER.debug("Running calculateFromConstantRH");
		// Look for RH (3001, 3002)
		
		List<Integer> rhParamsInDataset = this.getParamsInDataSet(weatherData,List.of(3001,3002));
                        
		if(rhParamsInDataset != null && rhParamsInDataset.size() > 0)
		{
			Integer rhParamIndex = weatherData.getParameterIndex(rhParamsInDataset.get(0));
                        weatherData.getLocationWeatherData().forEach((lwd) -> {
                            Double[][] oldData = lwd.getData();
                            Double[][] newData = new Double[oldData.length][oldData[0].length + 1];
                            for(int row = 0; row < oldData.length; row++)
                            {
                                for(int col=0; col < newData[0].length; col++)
                                {
                                    // Old data are copied
                                    if(col < oldData[0].length)
                                    {
                                        newData[row][col] = oldData[row][col];
                                    }
                                    else // Column for LW based on RH
                                    {
                                        if(oldData[row][rhParamIndex] != null)
                                        {
                                            newData[row][col] = oldData[row][rhParamIndex] >= 87.0 ? 60.0 : 0.0;
                                        }
                                    }
                                }
                            }
                            lwd.setData(newData);
                    });
			// Add the missing parameter to end of parameter list in weather data
			List<Integer> wpList = new ArrayList<>(Arrays.asList(weatherData.getWeatherParameters())); 
			wpList.add(3101);
			weatherData.setWeatherParameters(wpList.toArray(new Integer[wpList.size()]));
		}
		return weatherData;
	}

    public WeatherData calculateFromLSTM(WeatherData weatherData) throws  IOException {
        
        List<Integer> tmParamsInDataset = this.getParamsInDataSet(weatherData,List.of(1001,1002,1021,1022));
        List<Integer> rhParamsInDataset = this.getParamsInDataSet(weatherData,List.of(3001,3002));
        List<Integer> rrParamsInDataset = this.getParamsInDataSet(weatherData,List.of(2001));
        List<Integer> wsParamsInDataset = this.getParamsInDataSet(weatherData,List.of(4002,4003));
        
        Integer tmParamsIndex = weatherData.getParameterIndex(tmParamsInDataset.get(0));
        Integer rhParamsIndex = weatherData.getParameterIndex(rhParamsInDataset.get(0));
        Integer rrParamsIndex = weatherData.getParameterIndex(rrParamsInDataset.get(0));
        Integer wsParamsIndex = weatherData.getParameterIndex(wsParamsInDataset.get(0));
             
        var nOfParams = 6;
        
        for(LocationWeatherData lwd: weatherData.getLocationWeatherData()) {
            var oldData = lwd.getData();
            
            float[][] newData = new float[oldData.length][nOfParams];
            
            JSONArray weatherdata = new JSONArray();
            JSONObject dataobj = new JSONObject();
            
            for(int row = 0; row < oldData.length; row++) {
                
                JSONObject tmpdata = new JSONObject();
                // Create an 3D array that consist of TM, UM, RR, WS, DPD, BT_RH
                newData[row][0]=convertToFloat(oldData[row][tmParamsIndex]);
                newData[row][1]=convertToFloat(oldData[row][rhParamsIndex]);
                newData[row][2]=convertToFloat(oldData[row][rrParamsIndex]);
                newData[row][3]=convertToFloat(oldData[row][wsParamsIndex]);   
                
                // Calcuate dew point temperature and BT_RH needed as input to the LSTM model
                newData[row][4]= convertToFloat(oldData[row][tmParamsIndex] - (oldData[row][tmParamsIndex] - (100 - oldData[row][rhParamsIndex]) / 5));   
                //newData[row][5]= convertToFloat(oldData[row][rhParamsIndex] >= 87.0 ? 1.0 : 0.0); 
                newData[row][5]= (float) (newData[row][1] >= 87.0 ? 1.0 : 0.0); 
                
                //System.out.println(newData[row][1], newData[row][5]);
                
                tmpdata.put("TM", newData[row][0]);
                tmpdata.put("UM", newData[row][1]);
                tmpdata.put("RR", newData[row][2]);
                tmpdata.put("FM2", newData[row][3]);
                tmpdata.put("DPD", newData[row][4]);
                tmpdata.put("BT_RH", newData[row][5]);
                
                weatherdata.put(tmpdata);
            } 
            
            dataobj.put("Data",weatherdata);
            //System.out.println(dataobj.toString(4));
            
            try{
            
                URL url = new URL("http://localhost:5000/runLSTM");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
          
                try (PrintStream ps = new PrintStream(conn.getOutputStream())) {
                    ps.print(dataobj);
                }
            
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                } else {
                }
            
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;
                int[] BT = new int[oldData.length];
                
                while ((output = br.readLine()) != null) {
                        
                    String cleanOutput = output.replaceAll("[\\D+\\.]", "");
                    
                    for (int i = 0; i< cleanOutput.length(); i++){
                            BT[i]=Integer.parseInt(String.valueOf(cleanOutput.charAt(i)));
                    }
                         
                }
                
                conn.disconnect();

                Double[][] addedData = new Double[oldData.length][oldData[0].length + 1];
                            
                    for(int row = 0; row < oldData.length; row++)
                        {
                        for(int col=0; col < addedData[0].length; col++)
                                {
                                    // Old data are copied
                                    if(col < oldData[0].length)
                                    {
                                        addedData[row][col] = oldData[row][col];
                                    }
                                    else // Column for LW based on RH
                                    {
                                       addedData[row][col] = BT[row] >= 1 ? 60.0 : 0.0; 
                                    }
                                }
                            }
                            lwd.setData(addedData);
                
                } catch (MalformedURLException e) {
                } catch (IOException e){
            }
        }
           
        // Add the missing parameter to end of parameter list in weather data
	List<Integer> wpList = new ArrayList<>(Arrays.asList(weatherData.getWeatherParameters())); 
	wpList.add(3101);
	weatherData.setWeatherParameters(wpList.toArray(new Integer[wpList.size()]));
                
        return weatherData;
        
    }

    private List<Integer> getParamsInDataSet(WeatherData weatherData, List<Integer> listOfParams) {
        
        List<Integer> paramsInDataset = Arrays.asList(weatherData.getWeatherParameters()).stream() 
				.distinct()
				.filter(p->listOfParams.contains(p))
				.collect(Collectors.toList());
        return paramsInDataset;
    }

    private static Float convertToFloat(Double doubleValue) {
        return doubleValue == null ? null : doubleValue.floatValue();
    }
    

}
