/*
 * Copyright (c) 2020 NIBIO <http://www.nibio.no/>. 
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

package net.ipmdecisions.weather.services;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import net.ipmdecisions.weather.datasourceadapters.ParseWeatherDataException;
import net.ipmdecisions.weather.datasourceadapters.YrWeatherForecastAdapter;
import net.ipmdecisions.weather.datasourceadapters.finnishmeteorologicalinstitute.FinnishMeteorologicalInstituteAdapter;
import net.ipmdecisions.weather.entity.WeatherData;
import org.jboss.resteasy.annotations.GZIP;

/**
 * Some weather data sources may agree to deliver their weather data in the 
 * platform’s format directly. For the data sources that do not, adapters have 
 * to be programmed. The adapter's role is to download the data from the 
 * specified source and transform it into the platform's format. If the platform 
 * is using an adapter to download the weather data from a data source, the 
 * adapter's endpoint is specified in the weather data source catalogue.
 * 
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@Path("rest/weatheradapter")
public class WeatherAdapterService {
    /**
     * Get 9 day weather forecasts from <a href="https://www.met.no/en" target="new">The Norwegian Meteorological Institute</a>'s 
     * <a href="https://api.met.no/weatherapi/locationforecast/1.9/documentation" target="new">Locationforecast API</a> 
     * @param longitude WGS84 Decimal degrees
     * @param latitude WGS84 Decimal degrees
     * @param altitude Meters above sea level. This is used for correction of 
     * temperatures (outside of Norway, where the local topological model is used)
     * @pathExample /rest/weatheradapter/yr/?longitude=14.3711&latitude=67.2828&altitude=70
     * @return the weather forecast formatted in the IPM Decision platform's weather data format
     */
    @GET
    @POST
    @Path("yr/")
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
     * Get weather observations in the IPM Decision's weather data format from the Finnish Meteorological Institute https://en.ilmatieteenlaitos.fi/
     * Access is made through the Institute's open data API: https://en.ilmatieteenlaitos.fi/open-data
     * 
     * @param weatherStationId The weather station id (FMISID) in the open data API https://en.ilmatieteenlaitos.fi/observation-stations?filterKey=groups&filterQuery=weather
     * @param timeStart Start of weather data period (ISO-8601 Timestamp, e.g. 2020-06-12T00:00:00+03:00)
     * @param timeEnd End of weather data period (ISO-8601 Timestamp, e.g. 2020-07-03T00:00:00+03:00)
     * @param logInterval The measuring interval in seconds. Please note that the only allowed interval in this version is 3600 (hourly)
     * @param parameters Comma separated list of the requested weather parameters, given by <a href="/rest/parameter" target="new">their codes</a>
     * @param ignoreErrors Set to "true" if you want the service to return weather data regardless of there being errors in the service
     * @pathExample /rest/weatheradapter/fmi/?weatherStationId=101104&interval=3600&ignoreErrors=true&timeStart=2020-06-12T00:00:00%2B03:00&timeEnd=2020-07-03T00:00:00%2B03:00&parameters=1002,3002
     * @return 
     */
    @GET
    @POST
    @Path("fmi/")
    @GZIP
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFMIObservations(
            @QueryParam("weatherStationId") Integer weatherStationId,
            @QueryParam("timeStart") String timeStart,
            @QueryParam("timeEnd") String timeEnd,
            @QueryParam("interval") Integer logInterval,
            @QueryParam("parameters") String parameters,
            @QueryParam("ignoreErrors") String ignoreErrors
    )
    {
        List<Integer> ipmDecisionsParameters = Arrays.asList(parameters.split(",")).stream()
                    .map(paramstr->Integer.parseInt(paramstr.strip())).collect(Collectors.toList());
        // Date parsing
        Instant timeStartInstant = ZonedDateTime.parse(timeStart).toInstant();
        Instant timeEndInstant = ZonedDateTime.parse(timeEnd).toInstant();
        
        Boolean ignoreErrorsB = ignoreErrors != null ? ignoreErrors.equals("true") : false;
        
        // We only accept requests for hourly data
        if(!logInterval.equals(3600))
        {
            return Response.status(Status.BAD_REQUEST).entity("This service only provides hourly data").build();
        }

            WeatherData theData = new FinnishMeteorologicalInstituteAdapter().getHourlyData(weatherStationId, timeStartInstant, timeEndInstant, ipmDecisionsParameters, ignoreErrorsB);
            
            return Response.ok().entity(theData).build();       
//            return Response.serverError().entity(ex.getMessage()).build();


    }
    
    
}
