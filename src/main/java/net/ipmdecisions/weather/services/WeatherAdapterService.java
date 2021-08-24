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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import net.ipmdecisions.weather.datasourceadapters.DavisFruitwebAdapter;
import net.ipmdecisions.weather.datasourceadapters.MeteobotAPIAdapter;
import net.ipmdecisions.weather.datasourceadapters.MetosAPIAdapter;
import net.ipmdecisions.weather.datasourceadapters.ParseWeatherDataException;
import net.ipmdecisions.weather.datasourceadapters.YrWeatherForecastAdapter;
import net.ipmdecisions.weather.datasourceadapters.finnishmeteorologicalinstitute.FinnishMeteorologicalInstituteAdapter;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.WeatherDataUtil;
import org.jboss.resteasy.annotations.GZIP;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    
    private WeatherDataUtil weatherDataUtil;
    
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
                    @QueryParam("altitude") Double altitude,
                    @QueryParam("parameters") String parameters
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
        
        Set<Integer> ipmDecisionsParameters = parameters != null ? Arrays.asList(parameters.split(",")).stream()
                .map(paramstr->Integer.parseInt(paramstr.strip())).collect(Collectors.toSet())
                : null;
        
        try 
        {
            WeatherData theData = new YrWeatherForecastAdapter().getWeatherForecasts(longitude, latitude, altitude);
            if(ipmDecisionsParameters != null && ipmDecisionsParameters.size() > 0)
            {
            	theData = new WeatherDataUtil().filterParameters(theData, ipmDecisionsParameters);
            }
            return Response.ok().entity(theData).build();
        } 
        catch (ParseWeatherDataException ex) 
        {
            return Response.serverError().entity(ex.getMessage()).build();
        }

    }
    
    /**
     * Get 36 hour forecasts from FMI (The Finnish Meteorological Institute),
     * using their OpenData services at https://en.ilmatieteenlaitos.fi/open-data 
     * @param longitude WGS84 Decimal degrees
     * @param latitude WGS84 Decimal degrees

     * @pathExample /rest/weatheradapter/fmi/forecasts?latitude=67.2828&longitude=14.3711
     * @return the weather forecast formatted in the IPM Decision platform's weather data format
     */
    @GET
    @POST
    @Path("fmi/forecasts/")
    @GZIP
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFMIForecasts(
                    @QueryParam("longitude") Double longitude,
                    @QueryParam("latitude") Double latitude,
                    @QueryParam("parameters") String parameters
    )
    {
        if(longitude == null || latitude == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing longitude and/or latitude. Please correct this.").build();
        }
        
        Set<Integer> ipmDecisionsParameters = parameters != null ? Arrays.asList(parameters.split(",")).stream()
                .map(paramstr->Integer.parseInt(paramstr.strip())).collect(Collectors.toSet())
                : null;
        
        WeatherData theData = new FinnishMeteorologicalInstituteAdapter().getWeatherForecasts(longitude, latitude);
        if(ipmDecisionsParameters != null && ipmDecisionsParameters.size() > 0)
        {
        	theData = new WeatherDataUtil().filterParameters(theData, ipmDecisionsParameters);
        }
        return Response.ok().entity(theData).build();
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
        
        
        Instant timeStartInstant;
        Instant timeEndInstant;
        
        // Date parsing
        // Is it a ISO-8601 timestamp or date?
        try
        {
            timeStartInstant = ZonedDateTime.parse(timeStart).toInstant();
            timeEndInstant = ZonedDateTime.parse(timeEnd).toInstant();
        }
        catch(DateTimeParseException ex)
        {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            timeStartInstant = LocalDate.parse(timeStart, dtf).atStartOfDay(ZoneId.of("Europe/Helsinki")).toInstant();//.atZone().toInstant();
            timeEndInstant = LocalDate.parse(timeEnd, dtf).atStartOfDay(ZoneId.of("Europe/Helsinki")).toInstant();//.atZone(ZoneId.of("Europe/Helsinki")).toInstant();     
        }
        
        Boolean ignoreErrorsB = ignoreErrors != null ? ignoreErrors.equals("true") : false;
        
        // We only accept requests for hourly data
        if(!logInterval.equals(3600))
        {
            return Response.status(Status.BAD_REQUEST).entity("This service only provides hourly data").build();
        }

        WeatherData theData = new FinnishMeteorologicalInstituteAdapter().getHourlyData(weatherStationId, timeStartInstant, timeEndInstant, ipmDecisionsParameters, ignoreErrorsB);

        return Response.ok().entity(theData).build();       
    }
    
    /**
     * Get weather observations in the IPM Decision's weather data format from the the network of MeteoBot stations 
     * [https://meteobot.com/en/]
     * 
     * This is a network of privately owned weather stations, which all require 
     * authentication to access.
     * 
     * @param weatherStationId The weather station id 
     * @param timeStart Start of weather data period (ISO-8601 Timestamp, e.g. 2020-06-12T00:00:00+03:00)
     * @param timeEnd End of weather data period (ISO-8601 Timestamp, e.g. 2020-07-03T00:00:00+03:00)
     * @param logInterval The measuring interval in seconds. Please note that the only allowed interval in this version is 3600 (hourly)
     * @param parameters Comma separated list of the requested weather parameters, given by <a href="/rest/parameter" target="new">their codes</a>
     * @param ignoreErrors Set to "true" if you want the service to return weather data regardless of there being errors in the service
     * @param credentials json object with "userName" and "password" properties set
     * @requestExample application/x-www-form-urlencoded
     *   weatherStationId:536
     *   interval:3600
     *   ignoreErrors:true
     *   timeStart:2020-06-12
     *   timeEnd:2020-07-03
     *   parameters:1002,3002,2001
     *   credentials:{"userName":"XXXXX","password":"XXXX"}
     * @return 
     */
    @POST
    @Path("meteobot/")
    @GZIP
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMeteobotObservations(
            @FormParam("weatherStationId") Integer weatherStationId,
            @FormParam("timeStart") String timeStart,
            @FormParam("timeEnd") String timeEnd,
            @FormParam("interval") Integer logInterval,
            @FormParam("parameters") String parameters,
            @FormParam("ignoreErrors") String ignoreErrors,
            @FormParam("credentials") String credentials
    )
    {
        // We only accept requests for hourly data
        if(!logInterval.equals(3600))
        {
            return Response.status(Status.BAD_REQUEST).entity("This service only provides hourly data").build();
        }
        try
        {
            JsonNode json = new ObjectMapper().readTree(credentials);
            String userName = json.get("userName").asText();
            String password = json.get("password").asText();

            Set<Integer> ipmDecisionsParameters = new HashSet(Arrays.asList(parameters.split(",")).stream()
                        .map(paramstr->Integer.parseInt(paramstr.strip())).collect(Collectors.toList()));
            // Date parsing
            LocalDate startDate = LocalDate.parse(timeStart);
            LocalDate endDate = LocalDate.parse(timeEnd);

            Boolean ignoreErrorsB = ignoreErrors != null ? ignoreErrors.equals("true") : false;

            WeatherData theData = new MeteobotAPIAdapter().getWeatherData(weatherStationId,userName,password,startDate, endDate);
            return Response.ok().entity(this.getWeatherDataUtil().filterParameters(theData, ipmDecisionsParameters)).build();
        }
        catch(JsonProcessingException | ParseWeatherDataException ex)
        {
            return Response.serverError().entity(ex).build();
        }
    }
    
    /**
     * Get weather observations in the IPM Decision's weather data format from the the network of Pessl Instruments Metos stations 
     * [https://metos.at/]
     * 
     * This is a network of privately owned weather stations, which all require 
     * authentication to access.
     * 
     * @param weatherStationId The weather station id 
     * @param timeStart Start of weather data period (ISO-8601 Timestamp, e.g. 2020-06-12T00:00:00+03:00)
     * @param timeEnd End of weather data period (ISO-8601 Timestamp, e.g. 2020-07-03T00:00:00+03:00)
     * @param logInterval The measuring interval in seconds. Please note that the only allowed interval in this version is 3600 (hourly)
     * @param parameters Comma separated list of the requested weather parameters, given by <a href="/rest/parameter" target="new">their codes</a>
     * @param ignoreErrors Set to "true" if you want the service to return weather data regardless of there being errors in the service
     * @param credentials json object with "userName" and "password" properties set
     * @requestExample application/x-www-form-urlencoded
     *   weatherStationId:536
     *   interval:3600
     *   ignoreErrors:true
     *   timeStart:2020-06-12
     *   timeEnd:2020-07-03
     *   parameters:1002,3002,2001
     *   credentials:{"userName":"XXXXX","password":"XXXX"}
     * @return 
     */
    @POST
    @Path("metos/")
    @GZIP
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMetosObservations(
            @FormParam("weatherStationId") String weatherStationId,
            @FormParam("timeStart") String timeStart,
            @FormParam("timeEnd") String timeEnd,
            @FormParam("interval") Integer logInterval,
            @FormParam("parameters") String parameters,
            @FormParam("ignoreErrors") String ignoreErrors,
            @FormParam("credentials") String credentials
    )
    {
        // We only accept requests for hourly data
        if(!logInterval.equals(3600))
        {
            return Response.status(Status.BAD_REQUEST).entity("This service only provides hourly data").build();
        }
        try
        {
            JsonNode json = new ObjectMapper().readTree(credentials);
            String publicKey = json.get("userName").asText();
            String privateKey = json.get("password").asText();

            Set<Integer> ipmDecisionsParameters = new HashSet(Arrays.asList(parameters.split(",")).stream()
                        .map(paramstr->Integer.parseInt(paramstr.strip())).collect(Collectors.toList()));
            // Date parsing
            LocalDate startDate = LocalDate.parse(timeStart);
            LocalDate endDate = LocalDate.parse(timeEnd);

            Boolean ignoreErrorsB = ignoreErrors != null ? ignoreErrors.equals("true") : false;

            WeatherData theData = new MetosAPIAdapter().getWeatherData(weatherStationId,publicKey,privateKey,startDate, endDate);
            if(theData != null)
            {
            	return Response.ok().entity(this.getWeatherDataUtil().filterParameters(theData, ipmDecisionsParameters)).build();
            }
            else
            {
            	return Response.status(Status.NO_CONTENT).build();
            }
        }
        catch(ParseWeatherDataException | GeneralSecurityException | IOException ex)
        {
            return Response.serverError().entity(ex).build();
        }
    }
    
    /**
     * Get weather observations in the IPM Decision's weather data format from the the network of Fruitweb attached stations 
     * [https://www.fruitweb.info/en/]
     * 
     * This is a network of privately owned weather stations, which all require 
     * authentication to access.
     * 
     * @param weatherStationId The weather station id 
     * @param timeStart Start of weather data period (ISO-8601 Timestamp, e.g. 2020-06-12T00:00:00+03:00)
     * @param timeEnd End of weather data period (ISO-8601 Timestamp, e.g. 2020-07-03T00:00:00+03:00)
     * @param logInterval The measuring interval in seconds. Please note that the only allowed interval in this version is 3600 (hourly)
     * @param parameters Comma separated list of the requested weather parameters, given by <a href="/rest/parameter" target="new">their codes</a>
     * @param ignoreErrors Set to "true" if you want the service to return weather data regardless of there being errors in the service
     * @param credentials json object with "userName" and "password" properties set
     * @requestExample application/x-www-form-urlencoded
     *   weatherStationId:536
     *   interval:3600
     *   ignoreErrors:true
     *   timeStart:2020-06-12
     *   timeEnd:2020-07-03
     *   parameters:1002,3002,2001
     *   credentials:{"userName":"XXXXX","password":"XXXX"}
     * @return 
     */
    @POST
    @Path("davisfruitweb/")
    @GZIP
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDavisFruitwebObservations(
            @FormParam("weatherStationId") String weatherStationId,
            @FormParam("timeStart") String timeStart,
            @FormParam("timeEnd") String timeEnd,
            @FormParam("interval") Integer logInterval,
            @FormParam("parameters") String parameters,
            @FormParam("ignoreErrors") String ignoreErrors,
            @FormParam("credentials") String credentials
    )
    {
        // We only accept requests for hourly data
        if(!logInterval.equals(3600))
        {
            return Response.status(Status.BAD_REQUEST).entity("This service only provides hourly data").build();
        }
        try
        {
            JsonNode json = new ObjectMapper().readTree(credentials);
            String userName = json.get("userName").asText();
            String password = json.get("password").asText();

            Set<Integer> ipmDecisionsParameters = new HashSet(Arrays.asList(parameters.split(",")).stream()
                        .map(paramstr->Integer.parseInt(paramstr.strip())).collect(Collectors.toList()));
            // Date parsing
            LocalDate startDate = LocalDate.parse(timeStart);
            LocalDate endDate = LocalDate.parse(timeEnd);

            Boolean ignoreErrorsB = ignoreErrors != null ? ignoreErrors.equals("true") : false;

            
            
            WeatherData theData = new DavisFruitwebAdapter().getWeatherData(weatherStationId, password, startDate, endDate);
            return Response.ok().entity(this.getWeatherDataUtil().filterParameters(theData, ipmDecisionsParameters)).build();
        }
        catch(ParseWeatherDataException | IOException ex)
        {
            return Response.serverError().entity(ex).build();
        }
    }
    
    private WeatherDataUtil getWeatherDataUtil()
    {
        if(this.weatherDataUtil == null)
        {
            this.weatherDataUtil = new WeatherDataUtil();
        }
        return this.weatherDataUtil;
    }
}
