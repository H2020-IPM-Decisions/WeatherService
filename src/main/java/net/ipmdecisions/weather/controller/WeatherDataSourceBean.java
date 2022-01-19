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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.ipmdecisions.weather.entity.WeatherDataSource;

@Stateless
public class WeatherDataSourceBean {
	
	/**
     * Util method to read the catalogue from a YAML file
     * @return
     * @throws IOException 
     */
    public List<WeatherDataSource> getAllWeatherDataSources() throws IOException{
            File dsFile = new File(System.getProperty("net.ipmdecisions.weatherservice.DATASOURCE_LIST_FILE"));
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.registerModule(new JavaTimeModule());
            List<Map> prelim =  (List<Map>) mapper.readValue(dsFile, HashMap.class).get("datasources");
            List<WeatherDataSource> retVal = new ArrayList<>();
            prelim.forEach((m) -> {
                retVal.add(mapper.convertValue(m, new TypeReference<WeatherDataSource>(){}));
            });
            return retVal;
    }

    public WeatherDataSource getWeatherDataSourceById(String id) throws IOException
    {
    	for(WeatherDataSource candidate:this.getAllWeatherDataSources())
    	{
    		if(candidate.getId().equals(id))
    		{
    			return candidate;
    		}
    	}
    	return null;
    }
}
