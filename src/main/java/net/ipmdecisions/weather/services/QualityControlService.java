package net.ipmdecisions.weather.services;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.JSONObject;

import net.ipmdecisions.weather.qc.QualityControlMethods;
import net.ipmdecisions.weather.qc.ThresholdData;


/**
 *
 * @author 03080928
 */

@Path("rest/weatherdata/qualitycontrol")
public class QualityControlService {
    
    @GET
    @Path("heartbeat")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHeartBeat() {
        return Response.ok().entity("{\"result\":\"Heartbeat received\"}").build();
    }
    
    // PROTOTYPE IS DEPRECATED
    /*
    @POST
    @Path("prototype")
    @Consumes (MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setQC(String weatherData) {
        JSONObject inboundWeatherDataAsJsonObject = new JSONObject(weatherData);
        QualityControlMethods qualityControlMethods = new QualityControlMethods();
        return Response.ok().entity(qualityControlMethods.getQC(inboundWeatherDataAsJsonObject)).build();
    }
    */
    
    @GET
    @Path("threshold")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getThreshold(
                    @QueryParam("parameterid") String parameterId
    ) {
        ThresholdData thd = new ThresholdData();
        return Response.ok().entity(thd.getLowerAndUpperLimits(parameterId).toString()).build();
    }
    
    /*
     * QC for RT data
     * Implements
     * - Simple prequalification for values
     * - Interval test for temperature, soil temperature and wind
     * - Logical test for temperature, soil temperature, humidity and wind
    */
    @POST
    @Path("rt")
    @Consumes (MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQC_RT(String weatherData) {
        //JSONObject inboundWeatherDataAsJsonObject = new JSONObject(weatherData);
        QualityControlMethods qualityControlMethods = new QualityControlMethods();
        return Response.ok().entity(qualityControlMethods.getQC(weatherData, "RT")).build();
    }
    
    /*
     * QC for NON-RT data
     * - Step test
     * - Freeze test
    */
    @POST
    @Path("nonrt")
    @Consumes (MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQC_nonRT(String weatherData) {
        //JSONObject inboundWeatherDataAsJsonObject = new JSONObject(weatherData);
        QualityControlMethods qualityControlMethods = new QualityControlMethods();
        return Response.ok().entity(qualityControlMethods.getQC(weatherData, "NONRT")).build();
    }
    
}
