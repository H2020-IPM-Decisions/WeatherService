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
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.iakovlev.timeshape.TimeZoneEngine;
import net.ipmdecisions.weather.amalgamation.WeatherDataAggregationException;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.entity.WeatherDataSource;
import net.ipmdecisions.weather.entity.WeatherParameter;

/**
 * 
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 *
 */
@Stateless
public class AmalgamationBean {

    public AmalgamationBean() {
    }
    
    /**
     * Use this if you're not injecting this bean through @EJB
     * @param weatherDataSourceBean
     * @param metaDataBean 
     */
        public AmalgamationBean(WeatherDataSourceBean weatherDataSourceBean, MetaDataBean metaDataBean)
        {
            this.weatherDataSourceBean = weatherDataSourceBean;
            this.metaDataBean = metaDataBean;
        }
	
	TimeZoneEngine tzEngine;
	
	@EJB
	protected WeatherDataSourceBean weatherDataSourceBean;
	
	@EJB
	protected MetaDataBean metaDataBean;
	
	// Interchangeable parameters (e.g. instantaneous and average temperatures)
	// Temperature: 1001 (inst) - 1002 (avg) 
	// Relative humidity: 3001 (inst) - 3002 (avg)
	// Leaf wetness: 3103 (ground level) - 3101 (2m)
	// Wind speed: 4002 (inst 2m) - 4003 (avg 2m) - 4012 (inst 10m) - 4013 (avg 10m)
	// Used in  this.addFallbackParameters(WeatherData weatherData, Set<Integer> missingParameters)
	private Map<Integer, List<Integer>> fallbackParams = Map.of(
			1001, List.of(1002),
			1002, List.of(1001),
			3001, List.of(3002),
			3002, List.of(3001),
			3103, List.of(3101),
			3101, List.of(3103),
			4002, List.of(4003,4012,4013),
			4003, List.of(4002,4013,4012),
			4012, List.of(4013,4002,4003),
			4013, List.of(4012,4003,4002)
			);
	
	/*
	 * Parameters that can be used to calculate the requested parameter
	 */
	private final Map<Integer, List<Integer>> calculationParams = Map.of(
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
	
        /**
         * Combine a list of weatherData. Data from the first in the list are kept, any missing data are added from the other files
         * @param weatherData
         * @param timeStart
         * @param timeEnd
         * @param interval
         * @param zoneId
         * @return
         * @throws IOException 
         */
	public WeatherData getFusionedWeatherData(
			List<WeatherData> weatherData,
			Instant timeStart,
			Instant timeEnd,
			Integer interval,
			ZoneId zoneId
			) throws IOException
	{
		ObjectMapper oMapper = new ObjectMapper();
		oMapper.registerModule(new JavaTimeModule());
		Long length = 1 + (timeEnd.toEpochMilli() - timeStart.toEpochMilli()) / 1000 / interval;
		WeatherData fusionedWD = new WeatherData();
		fusionedWD.setTimeStart(timeStart);
		fusionedWD.setTimeEnd(timeEnd);
		fusionedWD.setInterval(interval);
		for(WeatherData currentWD:weatherData)
		{
			// Anything to work with here?
			// Has to contain relevant weather parameters
			// Has to have data with minimum the requested interval (hourly when requested daily are OK, the opposite is not)
			Set<Integer> currentWDParams = currentWD.getWeatherParameters() != null ? 
					Set.copyOf(Arrays.asList(currentWD.getWeatherParameters()))
					:null;
			if(currentWDParams == null || currentWD.getInterval() > interval)
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
			
			
			
			// Do we need to aggregate?
			if(currentWD.getInterval() != interval)
			{
				try
				{
					currentWD = this.aggregate(currentWD, interval, zoneId);
				}
				catch(WeatherDataAggregationException ex)
				{
					continue;
				}
			}
			
			// Currently only one set of data!
			LocationWeatherData currentLWD = currentWD.getLocationWeatherData().get(0);
			
			// From where to start putting data
			Long startRowInDataMatrix = Math.max(0,(currentWD.getTimeStart().toEpochMilli() - timeStart.toEpochMilli()) / 1000 / interval);
			// From where to start looking for data
			// E.g. if currentWD starts the day before (-1), start at element 1 (zero based array)
			Integer startRowInCurrentWD = (int) Math.max(0,(timeStart.toEpochMilli()- currentWD.getTimeStart().toEpochMilli()) / 1000 / interval);
			/*
			System.out.println("[AmalgamationBean.getFusionedWeatherData]currentWD.getTimeStart()=" + currentWD.getTimeStart() + "(" + currentWD.getTimeStart().toEpochMilli()+"), timeStart = " + timeStart +  "(" + timeStart.toEpochMilli()+")");
			System.out.println("[AmalgamationBean.getFusionedWeatherData]: startRowInDataMatrix=" + startRowInDataMatrix);
			System.out.println("[AmalgamationBean.getFusionedWeatherData]: startRowInCurrentWd=" + startRowInCurrentWD);
			*/
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
			/*
			System.out.println("dataMatrix length=" + length + ", currentLWD.getData().length=" + currentLWD.getData().length);
			System.out.println("dataMatrix width =" + (fusionedWDParams.size() + " + " + newParams.size()) + ", newParams = " 
			+ newParams.stream().map(i->String.valueOf(i)).collect(Collectors.joining(",")));
			*/
			for(int row = 0; (row + startRowInCurrentWD) < currentLWD.getData().length && startRowInDataMatrix.intValue() + row < dataMatrix.length; row++)
			{
				int col = 0;
				// Start with the existing
				for(; col < fusionedWDParams.size();col++)
				{
					// Try to fill in holes from the first sets
					if(dataMatrix[startRowInDataMatrix.intValue() + row][col] == null)
					{
						// We must also look for replacement parameter values
						Double replacementValue = null;
						
						for(Integer interchangeableParameter: this.getInterchangeableParameters(fusionedWD.getWeatherParameters()[col]))
						{
							
							if(currentWD.getParameterIndex(interchangeableParameter) != null)
							{
								replacementValue = currentLWD.getData()[row + startRowInCurrentWD][currentWD.getParameterIndex(interchangeableParameter)];
							}
							if(replacementValue != null)
							{
								break;
							}
						}
						//System.out.println("Adding value (" + replacementValue + ") at [" + (startRowInDataMatrix.intValue() + row )+ "]" + Instant.ofEpochMilli(timeStart.toEpochMilli() + (startRowInDataMatrix.intValue() + row) * interval * 1000l));
						//dataMatrix[startRowInDataMatrix.intValue() + row][col] = currentLWD.getData()[row + startRowInCurrentWD][currentWD.getParameterIndex(fusionedWD.getWeatherParameters()[col])];
						dataMatrix[startRowInDataMatrix.intValue() + row][col] = replacementValue;
					}
				}
				// Add the new at the end of each row
				for(Integer newParam:newParams)
				{
					Integer paramIndex = currentWD.getParameterIndex(newParam);
					dataMatrix[startRowInDataMatrix.intValue() + row][col++] = currentLWD.getData()[row + startRowInCurrentWD][paramIndex];
					//System.out.println("Adding value (" + currentLWD.getData()[row + startRowInCurrentWD][paramIndex] + ") at [" + (startRowInDataMatrix.intValue() + row )+ "]" + Instant.ofEpochMilli(timeStart.toEpochMilli() + (startRowInDataMatrix.intValue() + row) * interval * 1000l));
				}
				
			}
			fusionedLWD.setData(dataMatrix);
			List<Integer> newFusionedWDParams = new ArrayList<>(fusionedWDParams); // To make sure it's mutable
			newFusionedWDParams.addAll(newParams);
			fusionedWD.setWeatherParameters(newFusionedWDParams.toArray(Integer[]::new));
			
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
				if(currentLWD.getQC() != null && currentLWD.getQC().length > 0)
				{
					newQC[i] = currentLWD.getQC()[currentWD.getParameterIndex(fusionedWD.getWeatherParameters()[i])];
				}
				else
				{
					newQC[i] = 0;
				}
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
		Double tolerance = 2000.0; // Tolerance in meters for stations
		// Location
		List<WeatherDataSource> candidates = weatherDataSourceBean.getWeatherDataSourcesForLocation(longitude, latitude, tolerance);
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
		List<Integer> retVal = new ArrayList<>(List.of(parameter));
		if(fallbackParams.get(parameter) != null)
		{
			retVal.addAll(fallbackParams.get(parameter));
		}
		return retVal;
	}
	
	/**
	 * TODO Make more streamlined
	 * @param inputParameters
	 * @return all parameters that the API can calculate based on the provided parameters
	 */
	public List<Integer> getCalculatableParameters(Set<Integer> inputParameters)
	{
		List<Integer> retVal = new ArrayList<>();
		// If we have relative humidity, we can calculate Leaf wetness
		Set<Integer> rhParams = Set.of(3001,3002,3003);
		if(inputParameters.retainAll(rhParams))
		{
			retVal.add(3101);
		}
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
	
	/**
	 * 
	 * @param source
	 * @param requestedInterval
	 * @param timeZone
	 * @return
	 * @throws WeatherDataAggregationException
	 * @throws IOException 
	 */
	public WeatherData aggregate(WeatherData source, Integer requestedInterval, ZoneId timeZone) throws WeatherDataAggregationException, IOException
	{
		// Some basic input control
		if(source.getInterval() > requestedInterval)
		{
			throw new WeatherDataAggregationException("ERROR: requestedInterval(" + requestedInterval + ") can't be smaller than the source interval (" + source.getInterval() + ")");
		}
		if(source.getInterval().equals(requestedInterval))
		{
			return source;
		}
		
		// Bucket size
		Integer sourceValuesPerAggregationValue = requestedInterval / source.getInterval();
		//System.out.println("sourceValuesPerAggregationValue=" + sourceValuesPerAggregationValue);
		// What's the start index, considering midnight for time zone? (Assuming hourly to daily)
                //System.out.println("[AmalgamationBean.aggregate]: timeZone=" + timeZone);
                //System.out.println("[AmalgamationBean.aggregate]: source.getTimeStart().atZone(timeZone).getHour()=" + source.getTimeStart().atZone(timeZone).getHour());
		Integer startIndex = source.getTimeStart().atZone(timeZone).getHour() == 0 ? 0 : 23 - source.getTimeStart().atZone(timeZone).getHour();
		// TODO If the start index >= 15, consider creating a "day 1" using those values
		
		// Length of the aggregation array
		Integer length = (int) Math.ceil((source.getLocationWeatherData().get(0).getLength().doubleValue() - startIndex) / sourceValuesPerAggregationValue);
		//System.out.println("[AmalgamationBean.aggregate]: source data length=" + source.getLocationWeatherData().get(0).getLength());
		//System.out.println("[AmalgamationBean.aggregate]: startIndex=" + startIndex);
		//System.out.println("[AmalgamationBean.aggregate]: aggregation array length=" + length);
		for(LocationWeatherData lwd:source.getLocationWeatherData())
		{
			Double[][] aggregatedData = new Double[length][source.getWeatherParameters().length];
			Integer aggregatedRow = 0;
			for(Integer aggregationStartRow = startIndex; aggregationStartRow < lwd.getLength(); aggregationStartRow += sourceValuesPerAggregationValue)
			{
				//System.out.println("[AmalgamationBean.aggregate]: aggregationStartRow=" + aggregationStartRow);
				//System.out.println("[AmalgamationBean.aggregate]: aggregatedRow=" + aggregatedRow);
				for(int col=0;col<source.getWeatherParameters().length;col++)
				{
					Double[] sourceValuesToAggregate = new Double[sourceValuesPerAggregationValue];
					for(Integer subRow = 0; subRow < sourceValuesPerAggregationValue && (aggregationStartRow + subRow) < lwd.getLength() ;subRow++)
					{
						//System.out.println("subRow=" + subRow);
						sourceValuesToAggregate[subRow] = lwd.getData()[aggregationStartRow + subRow][col];
					}
					Double aggregatedValue = this.aggregateValues(sourceValuesToAggregate, source.getWeatherParameters()[col]);
					aggregatedData[aggregatedRow][col] = aggregatedValue;
				}
				aggregatedRow++;
			}
			lwd.setData(aggregatedData);
		}

		source.setTimeStart(source.getTimeStart().plusSeconds(startIndex * source.getInterval()));
		source.setTimeEnd(source.getTimeStart().plusSeconds((length - 1) * requestedInterval));
		//System.out.println("[AmalgamationBean.aggregate]: timeStart=" + source.getTimeStart());
		//System.out.println("[AmalgamationBean.aggregate]: timeEnd=" + source.getTimeEnd());
		source.setInterval(requestedInterval);
		return source;
	}
	
	/**
	 * Factory method for aggregating an array of values
	 * @param values
	 * @param parameterId
	 * @return
	 * @throws IOException
	 * @throws WeatherDataAggregationException
	 */
	public Double aggregateValues(Double[] values, Integer parameterId) throws IOException, WeatherDataAggregationException
	{
		if(values == null)
		{
			//System.out.println("values==null");
			return null;
		}
		switch(metaDataBean.getWeatherParameter(parameterId).getAggregationType()) {
			case WeatherParameter.AGGREGATION_TYPE_AVERAGE:
				return this.aggregateValuesAverage(values);
			case WeatherParameter.AGGREGATION_TYPE_SUM:
				return this.aggregateValuesSum(values);
			case WeatherParameter.AGGREGATION_TYPE_MAXIMUM:
				return this.aggregateValuesMaximum(values);
			case WeatherParameter.AGGREGATION_TYPE_MINIMUM:
				return this.aggregateValuesMinimum(values);
			default:
				throw new WeatherDataAggregationException("ERROR: Could not find method for aggregation type " + metaDataBean.getWeatherParameter(parameterId).getAggregationType());
		}
	}
	
	public Double aggregateValuesAverage(Double[] values)
	{
		return this.aggregateValuesSum(values) / values.length;
	}
	
	public Double aggregateValuesSum(Double[] values)
	{
		return Arrays.asList(values).stream().filter(v -> v != null).reduce(0d, Double::sum);
	}
	
	public Double aggregateValuesMinimum(Double[] values)
	{
		return Arrays.asList(values).stream().filter(v -> v != null).min(Double::compare).get();
	}
	
	public Double aggregateValuesMaximum(Double[] values)
	{
		return Arrays.asList(values).stream().filter(v -> v != null).max(Double::compare).get();
	}
	
}
