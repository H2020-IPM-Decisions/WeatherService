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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
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
import org.jboss.resteasy.annotations.GZIP;

/**
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@Path("rest")
public class WeatherDataSourceService {
    
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
    
    @GET
    @Path("parameter/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listWeatherParameters()
    {
        try
        {
            BufferedInputStream inputStream = new BufferedInputStream(this.getClass().getResourceAsStream("/weather_parameters_draft_v2.yaml"));
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return Response.ok().entity(mapper.readValue(inputStream, HashMap.class)).build();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
    
    @GET
    @Path("weatherdatasource/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listWeatherDataSources(){
        try
        {
            File dsFile = new File(System.getProperty("net.ipmdecisions.weatherservice.DATASOURCE_LIST_FILE"));
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return Response.ok().entity(mapper.readValue(dsFile, HashMap.class)).build();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
}
