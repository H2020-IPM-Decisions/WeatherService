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

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;

import net.ipmdecisions.weather.util.FileUtils;

/**
 * Various test data
 * 
 * @copyright 2021 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 *
 */
@Path("rest/testdata")
public class TestDataService {

	/**
	 * 
	 * @return A weatherdataset with some missing temperatures
	 */
	@GET
    @Path("weather/interpolation/temperature")
    @GZIP
    @Produces(MediaType.APPLICATION_JSON)
	public Response getTemperaturesThatNeedInterpolation()
	{
		FileUtils fileUtils = new FileUtils();
		try 
		{
			String weatherDataJson = fileUtils.getStringFromFileInApp("/lmt_weatherdata_temps_with_missing_data.json");
			return Response.ok().entity(weatherDataJson).build();
		}
		catch(Exception ex)
		{
			return Response.serverError().entity(ex.getMessage()).build();
		}
    	
	}
      
        @GET
    @Path("weather/qualitycontrol")
    @GZIP
    @Produces(MediaType.APPLICATION_JSON)
	public Response getQualityControlData()
	{
		FileUtils fileUtils = new FileUtils();
		try 
		{
			String weatherDataJson = fileUtils.getStringFromFileInApp("/weatherdata_no_errors.json");
			return Response.ok().entity(weatherDataJson).build();
		}
		catch(Exception ex)
		{
			return Response.serverError().entity(ex.getMessage()).build();
		}
    	
	}
        
}
