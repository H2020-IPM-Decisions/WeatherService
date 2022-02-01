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
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.iakovlev.timeshape.TimeZoneEngine;
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
	
	TimeZoneEngine tzEngine;
	
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
	
	/*
	 * Parameters that can be used to calculate the requested parameter
	 */
	private Map<Integer, List<Integer>> calculationParams = Map.of(
			3101, List.of(3001,3002,3003), // Leaf wetness 2m
			3102, List.of(3001,3002,3003), // Leaf wetness in canopy
			3103, List.of(3001,3002,3003)  // Leaf wetness in grass
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
		if(rhParamsInDataset != null && rhParamsInDataset.size() > 0)
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
		// We have a few "best" data sources
		// 
		return weatherDataSourceBean.getWeatherDataSourceById("net.ipmdecisions.dwd.euroweather");
	}
	
	public WeatherData getFusionedWeatherData(
			List<WeatherData> weatherData,
			Instant timeStart,
			Instant timeEnd,
			Integer interval
			)
	{
		ObjectMapper oMapper = new ObjectMapper();
		oMapper.registerModule(new JavaTimeModule());
		Long length = (timeEnd.toEpochMilli() - timeStart.toEpochMilli()) / 1000 / interval;
		WeatherData fusionedWD = new WeatherData();
		fusionedWD.setTimeStart(timeStart);
		fusionedWD.setTimeEnd(timeEnd);
		fusionedWD.setInterval(interval);
		for(WeatherData currentWD:weatherData)
		{
			// Anything to work with here?
			Set<Integer> currentWDParams = currentWD.getWeatherParameters() != null ? 
					Set.copyOf(Arrays.asList(currentWD.getWeatherParameters()))
					:null;
			if(currentWDParams == null)
			{
				continue;
			}
			
			// Which parameters are new?
			List<Integer> fusionedWDParams = fusionedWD.getWeatherParameters() != null ? 
					Arrays.asList(fusionedWD.getWeatherParameters())
					:new ArrayList<>();
			List<Integer> newParams = currentWDParams.stream()
					.filter(param->  ! fusionedWDParams.contains(param))
					.collect(Collectors.toList());
			
			
			
			// From where to start putting data
			// TODO - removed all logical holes in here! :-) 
			Long startRow = (currentWD.getTimeStart().toEpochMilli() - timeStart.toEpochMilli()) / 1000 / interval; 
			//System.out.println("startRow=" + startRow);
			// Currently only one set of data!
			LocationWeatherData currentLWD = currentWD.getLocationWeatherData().get(0);
			if(fusionedWD.getLocationWeatherData() == null)
			{
				fusionedWD.addLocationWeatherData(new LocationWeatherData(
						currentLWD.getLongitude(), currentLWD.getLatitude(),currentLWD.getAltitude(),
						0,0
						));
			}
			LocationWeatherData fusionedLWD = fusionedWD.getLocationWeatherData().get(0);
			
			// 
			
			Double[][] dataMatrix = new Double[length.intValue()][fusionedWDParams.size() + newParams.size()];
			
			// First: Add the values from the current fusionedWD. 
			for(int row = 0; row < fusionedLWD.getData().length ; row++)
			{
				for(int col =0; col < fusionedWDParams.size();col++)
				{
					if(fusionedLWD.getData()[row][col] != null)
					{
						dataMatrix[row][col] = fusionedLWD.getData()[row][col];
					}
				}
			}
			
			/*System.out.println("dataMatrix length=" + length + ", currentLWD.getData().length=" + currentLWD.getData().length);
			System.out.println("dataMatrix width =" + (fusionedWDParams.size() + " + " + newParams.size()) + ", newParams = " 
			+ newParams.stream().map(i->String.valueOf(i)).collect(Collectors.joining(",")));*/
			for(int row = 0; row < currentLWD.getData().length && startRow.intValue() + row < dataMatrix.length; row++)
			{
				int col = 0;
				// Start with the existing
				for(; col < fusionedWDParams.size();col++)
				{
					// Try to fill in holes from the first sets
					if(dataMatrix[startRow.intValue() + row][col] == null)
					{
						//System.out.println("Adding value (" + currentLWD.getData()[row][currentWD.getParameterIndex(fusionedWD.getWeatherParameters()[col])] + ") at [" + (startRow.intValue() + row )+ "]" + Instant.ofEpochMilli(timeStart.toEpochMilli() + (startRow.longValue() + row) * interval * 1000l));
						dataMatrix[startRow.intValue() + row][col] = currentLWD.getData()[row][currentWD.getParameterIndex(fusionedWD.getWeatherParameters()[col])];
					}
				}
				// Add the new at the end of each row
				for(Integer newParam:newParams)
				{
					Integer paramIndex = currentWD.getParameterIndex(newParam);
					dataMatrix[startRow.intValue() + row][col++] = currentLWD.getData()[row][paramIndex];
				}
				
			}
			fusionedLWD.setData(dataMatrix);
			List<Integer> newFusionedWDParams = new ArrayList<>(fusionedWDParams); // To make sure it's mutable
			newFusionedWDParams.addAll(newParams);
			fusionedWD.setWeatherParameters(newFusionedWDParams.toArray(new Integer[newFusionedWDParams.size()]));
			
			// We must update the qc and amalgamation arrays too
			Integer[] newQC = new Integer[fusionedWD.getWeatherParameters().length];
			Integer[] newAmalgamation = new Integer[fusionedWD.getWeatherParameters().length];
			int i=0;
			for(; i < fusionedWDParams.size(); i++)
			{
				newQC[i] = fusionedLWD.getQC()[i];
				newAmalgamation[i] = fusionedLWD.getAmalgamation()[i];
			}
			for(; i < fusionedWDParams.size() + newParams.size(); i++)
			{
				newQC[i] = currentLWD.getQC()[currentWD.getParameterIndex(fusionedWD.getWeatherParameters()[i])];
				newAmalgamation[i] = fusionedLWD.getAmalgamation()[currentWD.getParameterIndex(fusionedWD.getWeatherParameters()[i])];
			}
			fusionedLWD.setQC(newQC);
			fusionedLWD.setAmalgamation(newAmalgamation);
			/* Uncomment to debug
			try
			{
				System.out.println(oMapper.writeValueAsString(fusionedWD));
			}
			catch(JsonProcessingException ex)
			{
				ex.printStackTrace();
			}*/
		}
		return fusionedWD;
	}
	
	/**
	 * Get weather data sources for this location and parameters in priority order
	 * @param longitude
	 * @param latitude
	 * @param requestedParameters
	 * @param timeStart
	 * @param timeEnd
	 * @return
	 * @throws IOException
	 */
	public List<WeatherDataSource> getWeatherDataSourcesInPriorityOrder(
			Double longitude, Double latitude, 
			List<Integer> requestedParameters,
			Instant timeStart,
			Instant timeEnd
			) throws IOException
	{
		
		// Location
		List<WeatherDataSource> candidates = weatherDataSourceBean.getWeatherDataSourcesForLocation(longitude, latitude, latitude);
		// Must have at least one of the requested parameters (including fallbacks) 
		// OR (a) parameter(s) that can be used to calculate one of the requested parameters
		// Must (according to meta data) contain data for the given period
		Set<Integer> requestedInterchangeableAndOrCalculationParameters = new HashSet<>();
		for(Integer requestedParameter:requestedParameters)
		{
			requestedInterchangeableAndOrCalculationParameters.add(requestedParameter);
			if(fallbackParams.get(requestedParameter) != null)
			{
				requestedInterchangeableAndOrCalculationParameters.addAll(fallbackParams.get(requestedParameter));
			}
			if(calculationParams.get(requestedParameter) != null)
			{
				requestedInterchangeableAndOrCalculationParameters.addAll(calculationParams.get(requestedParameter));
			}
		}
		//System.out.println("timeStart=" + timeStart + ", timeEnd=" + timeEnd);
		candidates = candidates.stream().filter(c -> {
				for(int i=0;i<c.getParameters().getCommon().length;i++)
				{
					// Must have at least one of the requested parameters (including fallbacks)
					if(requestedInterchangeableAndOrCalculationParameters.contains(c.getParameters().getCommon()[i]))
					{
						//System.out.println(c.getName() + " temporalStart: " + c.getTemporalStart() + ", temporalEnd: " + c.getTemporalEnd());
						
						// Must (according to meta data) contain data for the given period
						if(c.getTemporalStart().isBefore(timeEnd) && c.getTemporalEnd().isAfter(timeStart))
						{
							return true;
						}
					}
				}
				return false;
			}).collect(Collectors.toList());
		
		Collections.sort(candidates);
		Collections.reverse(candidates);
		return candidates;
	}
	
	/**
	 * Util method for getting a list of all interchangeable parameters (e.g. instantaneous and average temperature)
	 * @param parameter
	 * @return
	 */
	public List<Integer> getInterchangeableParameters(Integer parameter)
	{
		List<Integer> retVal = List.of(parameter);
		retVal.addAll(fallbackParams.get(parameter));
		return retVal;
	}
	
	/**
	 * Using this API: https://github.com/RomanIakovlev/timeshape
	 * @return
	 */
	private TimeZoneEngine getTimeZoneEngine()
	{
		if(this.tzEngine == null)
		{
			// Use bboxfinder to find a suitable bounding box - to reduce memory usage
			// http://bboxfinder.com/
			this.tzEngine = TimeZoneEngine.initialize(35.746512,-11.601563,71.469124,32.519531, true);
		}
		return this.tzEngine;
	}
	
	/**
	 * 
	 * @param longitude
	 * @param latitude
	 * @return a ZoneId for the specified location OR the system's default if not found
	 */
	public ZoneId getTimeZoneForLocation(Double longitude, Double latitude)
	{
		return this.getTimeZoneEngine().query(latitude, longitude).orElse(ZoneId.systemDefault());
	}
}
