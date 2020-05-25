/*
 * Copyright (c) 2020 NIBIO <http://www.nibio.no/>. 
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

package net.ipmdecisions.weather.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.webcohesion.enunciate.metadata.rs.TypeHint;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.ipmdecisions.weather.entity.WeatherDataSource;

/**
 * This service provides information about the platform's weather data sources,
 * its weather data format and the weather parameters in use.
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@Path("rest")
public class WeatherDataSourceService {

    /**
     * Get a list of all the available weather data sources
     * @return A list of all the available weather data sources
     */
    @GET
    @Path("weatherdatasource")
    @Produces(MediaType.APPLICATION_JSON)
    @TypeHint(WeatherDataSource[].class)
    public Response listWeatherDataSources(){
        try
        {
            File dsFile = new File(System.getProperty("net.ipmdecisions.weatherservice.DATASOURCE_LIST_FILE"));
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.registerModule(new JavaTimeModule());
            List<Map> prelim =  (List<Map>) mapper.readValue(dsFile, HashMap.class).get("datasources");
            List<WeatherDataSource> retVal = new ArrayList<>();
            prelim.forEach((m) -> {
                retVal.add(mapper.convertValue(m, new TypeReference<WeatherDataSource>(){}));
            });
            return Response.ok().entity(retVal).build();
        }
        catch(IOException ex)
        {
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
}
