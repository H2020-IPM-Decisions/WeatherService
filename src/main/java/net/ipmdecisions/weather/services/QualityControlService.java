package net.ipmdecisions.weather.services;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.qc.QualityControlMethods;
import net.ipmdecisions.weather.qc.ThresholdData;


/**
 * Quality control endpoints for both real time and non-real time weather data.
 * 
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Markku Koistinen <markku.koistinen@luke.fi>
 */
@Path("rest/weatherdata/qualitycontrol")
public class QualityControlService {
    
    /**
     * Get API heart beat
     * @pathExample /rest/weatherdata/qualitycontrol/heartbeat
     * @return JSON Object with the message Heartbeat received
     */
    @GET
    @Path("heartbeat")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHeartBeat() {
        return Response.ok().entity("{\"result\":\"Heartbeat received\"}").build();
    }
    
    /**
     * Get weather parameter specific threshold data object with applicable min, max, threshold value and threshold value type
     * @param parameterid Weather parameter id
     * @pathExample /rest/weatherdata/qualitycontrol/threshold?parameterid=1101
     * @return JSON Object with parameter specific threshold data
     */
    @GET
    @Path("threshold")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getThreshold(
                    @QueryParam("parameterid") String parameterId
    ) {
        ThresholdData thd = new ThresholdData();
        return Response.ok().entity(thd.getThresholdDataObject(parameterId).toString()).build();
    }
    
    /**
     * Post real time weather data for quality control
     * RT QC implements
     * - Simple prequalification for values (value is numeric)
     * - Interval test for temperature, soil temperature and wind (parameter values
     * are tested against lower and upper physical limits)
     * - Logical test for temperature, soil temperature, humidity and wind (TBD)
     * @body Wather data in platform defined format
     * @pathExample /rest/weatherdata/qualitycontrol/rt
     * @return Quality controlled weather data. The end-point appends qc arrays 
     * into location weather data objects.
    */
    @POST
    @Path("rt")
    @Consumes (MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQC_RT(String weatherDataStr) {
    	try
    	{
	        QualityControlMethods qualityControlMethods = new QualityControlMethods();
	        return Response.ok().entity(qualityControlMethods.getQC(WeatherData.getInstanceFromString(weatherDataStr), "RT")).build();
    	}
    	catch(JsonProcessingException ex)
    	{
    		return Response.serverError().entity(ex.getMessage()).build();
    	}
    }
    
    /**
     * Post non-real time weather data for quality control
     * NON-RT QC implaments
     * - Step test (parameter values are tested against parameter specific threshold values,
     * both absolute and relative when applicable)
     * - Freeze test (parameter values are frozen)
    */
    @POST
    @Path("nonrt")
    @Consumes (MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQC_nonRT(String weatherDataStr) {
    	try
    	{
	        QualityControlMethods qualityControlMethods = new QualityControlMethods();
	        return Response.ok().entity(qualityControlMethods.getQC(WeatherData.getInstanceFromString(weatherDataStr), "NONRT")).build();
    	}
    	catch(JsonProcessingException ex)
    	{
    		return Response.serverError().entity(ex.getMessage()).build();
    	}
    }
    
}
