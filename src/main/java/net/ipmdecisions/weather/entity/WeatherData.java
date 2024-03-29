/*
 * Copyright (c) 2022 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of IPMDecisionsWeatherService.
 * IPMDecisionsWeatherService is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * IPMDecisionsWeatherService is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with IPMDecisionsWeatherService.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.ipmdecisions.weather.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaDescription;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaExamples;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaInject;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaString;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle;

import net.ipmdecisions.weather.entity.serializers.CustomInstantDeserializer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * NOTE: We do not use the meta data here to generate the schema. The schema
 * is a hard coded Json file at the root of the jar file. To get the schema for WeatherData,
 * use SchemaProvider.getWeatherDataSchema()
 * 
 * @copyright 2021-2022 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@JsonSchemaInject(strings = {
    @JsonSchemaString(path = "$id", value="https://platform.ipmdecisions.net/api/wx/rest/schema/weatherdata")
    }
)
@JsonSchemaTitle("Weather Data")
@JsonSchemaExamples("https://platform.ipmdecisions.net/api/wx/rest/weatheradapter/yr/?longitude=14.3711&latitude=67.2828&altitude=70")
@JsonSchemaDescription("Version 0.1. The schema describes the weather data format for the IPM Decisions platform. See an example here: https://platform.ipmdecisions.net/api/wx/rest/weatheradapter/yr/?longitude=14.3711&latitude=67.2828&altitude=70")
public class WeatherData {
    @NotNull
    @JsonSchemaTitle("Time start (yyyy-MM-dd'T'HH:mm:ssXXX)")
    @JsonPropertyDescription("The timestamp of the first weather observation. Format: \"yyyy-MM-dd'T'HH:mm:ssXXX\", e.g. 2020-04-09T18:00:00+02:00")
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    private Instant timeStart;
    @NotNull
    @JsonSchemaTitle("Time end (yyyy-MM-dd'T'HH:mm:ssXXX)")
    @JsonPropertyDescription("The timestamp of the last weather observation. Format: \"yyyy-MM-dd'T'HH:mm:ssXXX\", e.g. 2020-04-09T18:00:00+02:00")
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    private Instant timeEnd;
    @NotNull
    @Positive
    @JsonSchemaTitle("Sampling frequency (seconds)")
    @JsonPropertyDescription("The sampling frequency in seconds. E.g. 3600 = hourly values")
    private Integer interval; 
    @NotNull
    @Size(min=1)
    @JsonSchemaTitle("Weather parameters")
    @JsonPropertyDescription("The weather parameters. For reference, see https://platform.ipmdecisions.net/api/wx/rest/parameter")
    private Integer[] weatherParameters;
    
    @JsonSchemaTitle("Weather data")
    @JsonPropertyDescription("The weather data per location.")
    private List<LocationWeatherData> locationWeatherData;

    private static ObjectMapper objectMapper;
    
    public static WeatherData getInstanceFromString(String weatherDataAsString) throws JsonMappingException, JsonProcessingException
    {
    	if(WeatherData.objectMapper == null)
    	{
    		WeatherData.objectMapper = new ObjectMapper();
    	}

		return objectMapper.readValue(weatherDataAsString,
				WeatherData.class);

    }
    
    /**
     * @return the timeStart
     */
    public Instant getTimeStart() {
        return timeStart;
    }
    
    
    /**
     * @param timeStart the timeStart to set
     */
    public void setTimeStart(Instant timeStart) {
        this.timeStart = timeStart;
    }

    /**
     * @return the timeEnd
     */
    public Instant getTimeEnd() {
        return timeEnd;
    }
    

    /**
     * @param timeEnd the timeEnd to set
     */
    public void setTimeEnd(Instant timeEnd) {
        this.timeEnd = timeEnd;
    }

    /**
     * @return the interval
     */
    public Integer getInterval() {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    /**
     * @return the weatherParameters
     */
    public Integer[] getWeatherParameters() {
        return weatherParameters;
    }

    /**
     * @param weatherParameters the weatherParameters to set
     */
    public void setWeatherParameters(Integer[] weatherParameters) {
        this.weatherParameters = weatherParameters;
    }
    
    @JsonIgnore
    public boolean containsWeatherParameter(Integer weatherParameter)
    {
    	for(int i=0;i<this.getWeatherParameters().length;i++)
    	{
    		if(this.getWeatherParameters()[i].equals(weatherParameter))
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Removes data for the specified parameter from the data set
     * @param weatherParameter
     */
    @JsonIgnore
    public void removeParameter(Integer weatherParameter)
    {
    	// Quick check
    	if(! this.containsWeatherParameter(weatherParameter))
    	{
    		return;
    	}
    	
    	Integer paramIndex = this.getParameterIndex(weatherParameter);
    	
    	// Loop through LocationWeatherData, reduce dataset
    	for(LocationWeatherData lwd: this.getLocationWeatherData())
    	{
    		Double[][] oldData = lwd.getData();
			Double[][] newData = new Double[oldData.length][oldData[0].length - 1];
			// Removing the data column
			for(int row = 0; row < oldData.length; row++)
			{
				for(int col=0; col < oldData[0].length; col++)
				{
					// Old data are copied
					if(col < paramIndex)
					{
						newData[row][col] = oldData[row][col];
					}
					else if(col > paramIndex) // Shifting data
					{
						newData[row][col -1] = oldData[row][col];
					}
				}
			}
			// Remove the parameter from amalgamation list
			if(lwd.getAmalgamation() != null)
			{
				Integer[] newAmalgamationList = new Integer[lwd.getAmalgamation().length-1]; 
				for(int col=0; col < lwd.getAmalgamation().length; col++)
				{
					// Old data are copied
					if(col < paramIndex)
					{
						newAmalgamationList[col] = lwd.getAmalgamation()[col];
					}
					else if(col > paramIndex) // Shifting data
					{
						newAmalgamationList[col -1] = lwd.getAmalgamation()[col];
					}
				}
				lwd.setAmalgamation(newAmalgamationList);
			}
			// Remove the parameter from qc list
			if(lwd.getQC() != null)
			{
				Integer[] newQCList = new Integer[lwd.getQC().length-1]; 
				for(int col=0; col < lwd.getQC().length; col++)
				{
					// Old data are copied
					if(col < paramIndex)
					{
						newQCList[col] = lwd.getQC()[col];
					}
					else if(col > paramIndex) // Shifting data
					{
						newQCList[col -1] = lwd.getQC()[col];
					}
				}
				lwd.setQC(newQCList);
			}
			lwd.setData(newData);
    	}
    	// Remove the parameter from parameter list
    	Integer[] newParameterList = new Integer[this.getWeatherParameters().length-1];
    	for(int col=0; col < this.getWeatherParameters().length; col++)
		{
			// Old data are copied
			if(col < paramIndex)
			{
				newParameterList[col] = this.getWeatherParameters()[col];
			}
			else if(col > paramIndex) // Shifting data
			{
				newParameterList[col -1] = this.getWeatherParameters()[col];
			}
		}
    	this.setWeatherParameters(newParameterList);
    }

    /**
     * @return the locationWeatherData
     */
    public List<LocationWeatherData> getLocationWeatherData() {
        return locationWeatherData;
    }

    /**
     * @param locationWeatherData the locationWeatherData to set
     */
    public void setLocationWeatherData(List<LocationWeatherData> locationWeatherData) {
        this.locationWeatherData = locationWeatherData;
    }
    
    public void addLocationWeatherData(LocationWeatherData locationWeatherData) {
        if (this.locationWeatherData == null)
        {
            this.locationWeatherData = new ArrayList<>();
        }
        this.locationWeatherData.add(locationWeatherData);
    }

    /**
     * Get data for the specified parameter - for all locations
     * @param parameterCode
     * @return
     */
    @JsonIgnore
    public List<LocationWeatherData> getDataForParameter(Integer parameterCode)
    {
    	Integer paramIndex = this.getParameterIndex(parameterCode);
    	if(paramIndex == null)
    	{ 
    		return null;
    	}
    	return this.getLocationWeatherData().stream()
			.map(l->{
	    		LocationWeatherData n = new LocationWeatherData(
					l.getLongitude(),
					l.getLatitude(),
					l.getAltitude(),
					l.getData().length,
					1 // Only one parameter
				);
	    		for(int i=0;i<n.getData().length;i++)
	    		{
	    			n.getData()[i][0] = l.getData()[i][paramIndex];
	    		}
	    		return n;
			})
			.collect(Collectors.toList());
    	
    }
    
    @JsonIgnore
    public Integer getParameterIndex(Integer parameterCode)
    {
    	for(int i=0;i<this.getWeatherParameters().length;i++)
    	{
    		if(this.getWeatherParameters()[i].equals(parameterCode))
    		{
    			return i;
    		}
    	}
    	return null;
    }
    
}
