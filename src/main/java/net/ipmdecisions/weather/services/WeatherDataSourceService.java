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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.ipmdecisions.weather.datasourceadapters.ParseWeatherDataException;
import net.ipmdecisions.weather.datasourceadapters.YrWeatherForecastAdapter;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.entity.WeatherDataSource;
import net.ipmdecisions.weather.entity.WeatherParameter;
import org.jboss.resteasy.annotations.GZIP;

/**
 * Provides information about the platform's weather data sources,
 * its weather data format and the parameters in use.
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@Path("rest")
public class WeatherDataSourceService {
    
    /**
     * Get 9 day weather forecasts from the Norwegian Meteorological Service. 
     * https://api.met.no/weatherapi/locationforecast/1.9/documentation 
     * @param longitude WGS84 Decimal degrees
     * @param latitude WGS84 Decimal degrees
     * @param altitude Meters above sea level. This is used for correction of 
     * temperatures (outside of Norway, where the local topological model is used)
     * @pathExample /rest/forecasts/yr/?longitude=14.3711&latitude=67.2828&altitude=70
     * @return 
     */
    @GET
    @POST
    @Path("forecasts/yr/")
    @GZIP
    @Produces(MediaType.APPLICATION_JSON)
    public Response getYRForecasts(
                    @QueryParam("longitude") Double longitude,
                    @QueryParam("latitude") Double latitude,
                    @QueryParam("altitude") Double altitude
    )
    {
        if(longitude == null || latitude == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing longitude and/or altitude. Please correct this.").build();
        }
        if(altitude == null)
        {
            altitude = 0.0;
        }
        
        try 
        {
            WeatherData theData = new YrWeatherForecastAdapter().getWeatherForecasts(longitude, latitude, altitude);
            
            return Response.ok().entity(theData).build();
        } 
        catch (ParseWeatherDataException ex) 
        {
            return Response.serverError().entity(ex.getMessage()).build();
        }

    }
    
    /**
     * 
     * @return A list of all the weather parameters defined in the platform
     */
    @GET
    @Path("parameter/list")
    @Produces(MediaType.APPLICATION_JSON)
    @TypeHint(WeatherParameter[].class)
    public Response listWeatherParameters()
    {
        try
        {
            BufferedInputStream inputStream = new BufferedInputStream(this.getClass().getResourceAsStream("/weather_parameters_draft_v2.yaml"));
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map prelimResult = mapper.readValue(inputStream, HashMap.class);
            List<Map> parameters = (List<Map>) prelimResult.get("parameters");
            List<WeatherParameter> retVal = new ArrayList<>();
            parameters.forEach((pre) -> {
                retVal.add(mapper.convertValue(pre, new TypeReference<WeatherParameter>(){}));
            });
           
            return Response.ok().entity(retVal).build();
        }
        catch(IOException ex)
        {
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
    
    /**
     * 
     * @return A list of all the available weather data sources
     */
    @GET
    @Path("weatherdatasource/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listWeatherDataSources(){
        try
        {
            File dsFile = new File(System.getProperty("net.ipmdecisions.weatherservice.DATASOURCE_LIST_FILE"));
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.registerModule(new JavaTimeModule());
            List<Map> prelim =  (List<Map>) mapper.readValue(dsFile, HashMap.class).get("datasources");
            List<WeatherDataSource> retVal = new ArrayList<>();
            for(Map m:prelim)
            {
                retVal.add(mapper.convertValue(m, new TypeReference<WeatherDataSource>(){}));
            }
            return Response.ok().entity(retVal).build();
        }
        catch(IOException ex)
        {
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
}
