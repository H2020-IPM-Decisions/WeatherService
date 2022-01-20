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
package net.ipmdecisions.weather.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.entity.WeatherDataSource;
import net.ipmdecisions.weather.services.WeatherDataSourceService;

/**
 * 
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 *
 */
@Stateless
public class AmalgamationBean {
	
	@EJB
	WeatherDataSourceBean weatherDataSourceBean;
	
	// Interchangeable parameters (e.g. instantaneous and average temperatures)
	// Temperature: 1001 (inst) - 1002 (avg) 
	// Relative humidity: 3001 (inst) - 3002 (avg)
	// Wind speed: 4002 (inst 2m) - 4003 (avg 2m) - 4012 (inst 10m) - 4013 (avg 10m)
	// Used in  this.addFallbackParameters(WeatherData weatherData, Set<Integer> missingParameters)
	private Map<Integer, List<Integer>> fallbackParams = Map.of(
			1001, List.of(1002),
			1002, List.of(1001),
			3001, List.of(3002),
			4002, List.of(4003,4012,4013),
			4003, List.of(4002,4013,4012),
			4012, List.of(4013,4002,4003),
			4013, List.of(4012,4003,4002)
			);
	
	/**
	 * Adds missing parameters to the data set, using fallback parameters from the same data set 
	 * @param weatherData
	 * @param missingParameters
	 * @return
	 */
	public WeatherData addFallbackParameters(WeatherData weatherData, Set<Integer> missingParameters)
	{
		for(Integer missingParameter: missingParameters)
		{
			List<Integer> fallbacksForParam = this.fallbackParams.get(missingParameter);
			if(fallbacksForParam != null)
			{
				for(Integer fallback: fallbacksForParam)
				{
					//System.out.println("Have we got " + fallback + " in the current data set? " + weatherData.containsWeatherParameter(fallback));
					if(weatherData.containsWeatherParameter(fallback))
					{
						// Add the missing parameter to end of parameter list in weather data
						List<Integer> wpList = new ArrayList<Integer>(Arrays.asList(weatherData.getWeatherParameters())); 
						wpList.add(missingParameter);
						weatherData.setWeatherParameters(wpList.toArray(new Integer[wpList.size()]));
						// For each lwd, add the fallback to end of data list
						Integer fallbackCol = weatherData.getParameterIndex(fallback);
						for(LocationWeatherData lwd: weatherData.getLocationWeatherData()) {
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
									else // Data for new col at end fetched from existing
									{
										newData[row][col] = oldData[row][fallbackCol];
									}
								}
							}
							lwd.setData(newData);
						}
						// Get outta here!
						break;
					}
				}
			}
			
		}
		return weatherData;
	}
	
	/**
	 * Given the available parameters, attempts to calculate LW using 
	 * the most relevant LW calculation algorithm. Status 2022-01-19: Only the 
	 * RH >= 87% method is available
	 * 
	 * @param weatherData
	 * @return
	 */
	public WeatherData calculateLeafWetnessBestEffort(WeatherData weatherData)
	{
		// Currently, we only have the RH >= 87% method available.
		// Look for RH (3001, 3002)
		List<Integer> rhParams = List.of(3001,3002);
		List<Integer> rhParamsInDataset = Arrays.asList(weatherData.getWeatherParameters()).stream()
				.distinct()
				.filter(p->rhParams.contains(p))
				.collect(Collectors.toList());
		if(rhParamsInDataset.size() > 0)
		{
			Integer rhParamIndex = weatherData.getParameterIndex(rhParamsInDataset.get(0));
			for(LocationWeatherData lwd: weatherData.getLocationWeatherData()) {
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
			}
			// Add the missing parameter to end of parameter list in weather data
			List<Integer> wpList = new ArrayList<Integer>(Arrays.asList(weatherData.getWeatherParameters())); 
			wpList.add(3101);
			weatherData.setWeatherParameters(wpList.toArray(new Integer[wpList.size()]));
		}
		return weatherData;
	}

	/**
	 * Based on the provided location: Attempts to pick the best weather data source
	 * As of 2022-01-18 it returns a URL for the Euroweather service () by default
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public URL getURLForWeatherDataSourceBestEffort(Double longitude, Double latitude) throws IOException
	{
		WeatherDataSource wds = this.getWeatherDataSourceBestEffort(longitude, latitude);
		return new URL(wds.getEndpoint());
		
	}
	
	/**
	 * Based on the provided location: Attempts to pick the best weather data source
	 * As of 2022-01-18 it returns a the Euroweather service () by default
	 * @param longitude Decimal degrees
	 * @param latitude Decimal degrees
	 * @return
	 */
	public WeatherDataSource getWeatherDataSourceBestEffort(Double longitude, Double latitude) throws IOException
	{
		return weatherDataSourceBean.getWeatherDataSourceById("net.ipmdecisions.dwd.euroweather");
	}
}
