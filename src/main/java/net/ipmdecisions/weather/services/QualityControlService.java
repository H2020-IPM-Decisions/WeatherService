package net.ipmdecisions.weather.services;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.JSONObject;

import net.ipmdecisions.weather.qc.QualityControlMethods;


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
    
    @POST
    @Path("prototype")
    @Consumes (MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setQC(String weatherData) {
        JSONObject inboundWeatherDataAsJsonObject = new JSONObject(weatherData);
        QualityControlMethods qualityControlMethods = new QualityControlMethods();
        return Response.ok().entity(qualityControlMethods.getQC(inboundWeatherDataAsJsonObject).toString()).build();
    }
    
}
