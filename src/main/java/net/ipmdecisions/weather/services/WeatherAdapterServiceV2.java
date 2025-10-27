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
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import net.ipmdecisions.weather.controller.AmalgamationBean;
import net.ipmdecisions.weather.datasourceadapters.*;
import net.ipmdecisions.weather.datasourceadapters.dmi.DMIPointWebDataParser;
import net.ipmdecisions.weather.datasourceadapters.finnishmeteorologicalinstitute.FinnishMeteorologicalInstituteAdapter;
import net.ipmdecisions.weather.datasourceadapters.v2.service.WeatherDataService;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.entity.WeatherDataSourceException;
import net.ipmdecisions.weather.util.WeatherDataUtil;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Some weather data sources may agree to deliver their weather data in the 
 * platform’s format directly. For the data sources that do not, adapters have 
 * to be programmed. The adapter's role is to download the data from the 
 * specified source and transform it into the platform's format. If the platform 
 * is using an adapter to download the weather data from a data source, the 
 * adapter's endpoint is specified in the weather data source catalogue.
 * 
 * @copyright 2020-2024 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@Path("rest/weatheradapter/v2")
public class WeatherAdapterServiceV2 {
	
    private static Logger LOGGER = LoggerFactory.getLogger(WeatherAdapterServiceV2.class);
    
    @Inject
    AmalgamationBean amalgamationBean;

    @Inject
    WeatherDataService weatherDataService;

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
            return Response.status(Status.BAD_REQUEST).entity("Missing longitude and/or altitude. Please correct this.").build();
        }
        if(altitude == null)
        {
            altitude = 0.0;
        }
        
        Set<Integer> ipmDecisionsParameters = parameters != null ? Arrays.asList(parameters.split(",")).stream()
                .map(paramstr->Integer.parseInt(paramstr.strip())).collect(Collectors.toSet())
                : null;

        WeatherData theData = weatherDataService.getWeatherData("yr", Map.of("longitude", longitude, "latitude", latitude, "altitude", altitude));//new YrWeatherForecastAdapter().getWeatherForecasts(longitude, latitude, altitude);
        if(ipmDecisionsParameters != null && ipmDecisionsParameters.size() > 0)
        {
            theData = new WeatherDataUtil().filterParameters(theData, ipmDecisionsParameters);
        }
        return Response.ok().entity(theData).build();

    }
    
    /**
     * Get 9 day weather forecasts from <a href="https://data.gov.ie/" target="new">Met Éireann (Ireland)</a>'s 
     * <a href="https://data.gov.ie/dataset/met-eireann-weather-forecast-api" target="new">Locationforecast API</a> 
     * @param longitude WGS84 Decimal degrees
     * @param latitude WGS84 Decimal degrees
     * @pathExample /rest/weatheradapter/meteireann/?longitude=-7.644361&latitude=52.597709&parameters=1001, 3001
     * @return the weather forecast formatted in the IPM Decision platform's weather data format
     */
    @GET
    @POST
    @Path("meteireann/")
    @GZIP
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMetIrelandForecasts(
                    @QueryParam("longitude") Double longitude,
                    @QueryParam("latitude") Double latitude,
                    @QueryParam("altitude") Double altitude,
                    @QueryParam("parameters") String parameters
    )
    {
        if(longitude == null || latitude == null)
        {
            return Response.status(Status.BAD_REQUEST).entity("Missing longitude and/or altitude. Please correct this.").build();
        }
        if(altitude == null)
        {
            altitude = 0.0;
        }
        
        Set<Integer> ipmDecisionsParameters = parameters != null ? Arrays.asList(parameters.split(",")).stream()
                .map(paramstr->Integer.parseInt(paramstr.strip())).collect(Collectors.toSet())
                : null;

        WeatherData theData = weatherDataService.getWeatherData("meteireann", Map.of("longitude", longitude, "latitude", latitude, "altitude", altitude));
        if(ipmDecisionsParameters != null && ipmDecisionsParameters.size() > 0)
        {
            theData = new WeatherDataUtil().filterParameters(theData, ipmDecisionsParameters);
        }
        return Response.ok().entity(theData).build();

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
            return Response.status(Status.BAD_REQUEST).entity("Missing longitude and/or latitude. Please correct this.").build();
        }
        
        Set<Integer> ipmDecisionsParameters = parameters != null ? Arrays.asList(parameters.split(",")).stream()
                .map(paramstr->Integer.parseInt(paramstr.strip())).collect(Collectors.toSet())
                : null;

        var theData = weatherDataService.getWeatherData("FMI", Map.of("longitude", longitude, "latitude", latitude));
        if(ipmDecisionsParameters != null && ipmDecisionsParameters.size() > 0)
        {
        	theData = new WeatherDataUtil().filterParameters(theData, ipmDecisionsParameters);
        }
        return Response.ok().entity(theData).build();
    }
}
