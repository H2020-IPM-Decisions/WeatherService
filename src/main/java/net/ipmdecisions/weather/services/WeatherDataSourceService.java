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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import net.ipmdecisions.weather.datasourceadapters.ParseWeatherDataException;
import net.ipmdecisions.weather.datasourceadapters.YrWeatherForecastAdapter;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;
import org.jboss.resteasy.annotations.GZIP;

/**
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@Path("rest")
public class WeatherDataSourceService {
@GET
    @Path("test")
    @Produces("text/plain;charset=UTF-8")
    public Response test()
    {
        return Response.ok().entity("Hello world of weather").build();
    }
    
    
    @GET
    @POST
    @Path("forecasts/yr/")
    @GZIP
    @Produces("application/json;charset=UTF-8")
    public Response getYRForecasts(
                    @QueryParam("longitude") Double longitude,
                    @QueryParam("latitude") Double latitude,
                    @QueryParam("altitude") Double altitude
    )
    {
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
}
