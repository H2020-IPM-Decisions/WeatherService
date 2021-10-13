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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import net.ipmdecisions.weather.entity.WeatherParameter;

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
