package net.ipmdecisions.weather.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import net.ipmdecisions.weather.datasourceadapters.v2.service.WeatherDataService;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.WeatherDataUtil;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.*;
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
    WeatherDataService weatherDataService;

    private WeatherDataUtil weatherDataUtil;

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

    @GET
    @POST
    @Path("lantmet/")
    @GZIP
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSLULantMetObservations(
            @QueryParam("longitude") Double longitude,
            @QueryParam("latitude") Double latitude,
            @QueryParam("timeStart") String timeStart,
            @QueryParam("timeEnd") String timeEnd,
            @QueryParam("interval") Integer logInterval,
            @QueryParam("parameters") String parameters,
            @QueryParam("ignoreErrors") String ignoreErrors
    )
    {
        List<Integer> ipmDecisionsParameters = parameters != null ? Arrays.asList(parameters.split(",")).stream()
                .map(paramstr->Integer.parseInt(paramstr.strip())).collect(Collectors.toList())
                : null;


        Instant timeStartInstant;
        Instant timeEndInstant;

        // Date parsing
        // Is it a ISO-8601 timestamp or date?
        DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE;
        try
        {
            timeStartInstant = ZonedDateTime.parse(timeStart).toInstant();
            timeEndInstant = ZonedDateTime.parse(timeEnd).toInstant();
        }
        catch(DateTimeParseException ex)
        {

            timeStartInstant = LocalDate.parse(timeStart, dtf).atStartOfDay(ZoneId.of("GMT+1")).toInstant();//.atZone().toInstant();
            timeEndInstant = LocalDate.parse(timeEnd, dtf).atStartOfDay(ZoneId.of("GMT+1")).toInstant();//.atZone(ZoneId.of("Europe/Helsinki")).toInstant();
        }

        Boolean ignoreErrorsB = ignoreErrors != null ? ignoreErrors.equals("true") : false;


        // Default is hourly, optional is daily
        logInterval = (logInterval == null || logInterval != 86400) ? 3600 : 86400;

        if(longitude == null || latitude == null)
        {
            return Response.status(Status.BAD_REQUEST).entity("Missing longitude and/or latitude. Please correct this.").build();
        }


            //WeatherData theData = new SLULantMetAdapter().getData(
            //        longitude, latitude,
            //        timeStartInstant,timeEndInstant,
            //        logInterval,
            //        ipmDecisionsParameters
            //);
            var theData = weatherDataService.getWeatherData("SLU", Map.of(
                    "longitude", longitude,
                    "latitude", latitude,
                    "timeStart", timeStartInstant,
                    "timeEnd", timeEndInstant,
                    "interval", logInterval,
                    "ignoreErrors", ignoreErrorsB,
                    "parameters", ipmDecisionsParameters
            ));
            if(theData == null)
            {
                return Response.noContent().build();
            }

            return Response.ok().entity(theData).build();


    }

    @POST
    @Path("davisfruitweb/")
    @GZIP
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDavisFruitwebObservations(
            @FormParam("weatherStationId") String weatherStationId,
            @FormParam("timeZone") String timeZoneId,
            @FormParam("timeStart") String timeStart,
            @FormParam("timeEnd") String timeEnd,
            @FormParam("interval") Integer logInterval,
            @FormParam("parameters") String parameters,
            @FormParam("ignoreErrors") String ignoreErrors,
            @FormParam("credentials") String credentials
    )
    {
        TimeZone timeZone = timeZoneId != null ? TimeZone.getTimeZone(ZoneId.of(timeZoneId)) : TimeZone.getTimeZone("UTC");
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
                    .map(paramstr->Integer.valueOf(paramstr.strip())).collect(Collectors.toList()));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
            ZoneId zone = timeZoneId != null ? ZoneId.of(timeZoneId) : ZoneOffset.UTC;

            Instant timeStartInstant;
            Instant timeEndInstant;

            if (timeStart.contains("T")) {
                timeStartInstant = ZonedDateTime.parse(timeStart).toInstant();
            } else {
                LocalDate ld = LocalDate.parse(timeStart, dateFormatter);
                timeStartInstant = ld.atStartOfDay(zone).toInstant();
            }

            if (timeEnd.contains("T")) {
                timeEndInstant = ZonedDateTime.parse(timeEnd).toInstant();
            } else {
                LocalDate ld = LocalDate.parse(timeEnd, dateFormatter);
                timeEndInstant = ld.atStartOfDay(zone).toInstant();
            }

            Boolean ignoreErrorsB = ignoreErrors != null ? ignoreErrors.equals("true") : false;



            var theData = weatherDataService.getWeatherData("DavisFruit", Map.of(
                    "stationID", weatherStationId, "username", userName,"password", password, "startDate", timeStartInstant, "endDate", timeEndInstant, "timeZone", timeZone));
            return Response.ok().entity(this.getWeatherDataUtil().filterParameters(theData, ipmDecisionsParameters)).build();
        }
        catch(IOException ex)
        {
            return Response.serverError().entity(ex).build();
        }
        catch(NotAuthorizedException ex)
        {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
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
