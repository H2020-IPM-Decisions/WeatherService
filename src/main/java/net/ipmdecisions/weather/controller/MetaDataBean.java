/*
 * Copyright (c) 2021 NIBIO <http://www.nibio.no/>. 
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import net.ipmdecisions.weather.entity.WeatherParameter;

/**
 * 
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 *
 */
@Stateless
public class MetaDataBean {
	
	private List<WeatherParameter> weatherParameterList;
	private Map<Integer,WeatherParameter> weatherParameterMap;
	
	/**
     * Get a list of all the weather parameters defined in the platform
     * @return A list of all the weather parameters defined in the platform
     */
    public List<WeatherParameter> getWeatherParameterList() throws IOException
    {
    	if(this.weatherParameterList == null)
    	{
	    	BufferedInputStream inputStream = new BufferedInputStream(this.getClass().getResourceAsStream("/weather_parameters_draft_v2.yaml"));
	        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	        Map prelimResult = mapper.readValue(inputStream, HashMap.class);
	        List<Map> parameters = (List<Map>) prelimResult.get("parameters");
	        this.weatherParameterList = new ArrayList<>();
	        parameters.forEach((pre) -> {
	        	this.weatherParameterList.add(mapper.convertValue(pre, new TypeReference<WeatherParameter>(){}));
	        });
    	}
        
        return this.weatherParameterList;
    }
	
    public Map<Integer,WeatherParameter> getWeatherParameterMap() throws IOException
    {
    	if(this.weatherParameterMap == null)
    	{
    		this.weatherParameterMap = this.getWeatherParameterList().stream().collect(Collectors.toMap(WeatherParameter::getId, Function.identity()));
    	}
    	return this.weatherParameterMap;
    }
	
	
	public WeatherParameter getWeatherParameter(Integer weatherParameterId) throws IOException
    {
    	return this.getWeatherParameterMap().get(weatherParameterId);
    }

}
