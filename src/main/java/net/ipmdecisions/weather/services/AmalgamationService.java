/*
 * Copyright (c) 2021 NIBIO <http://www.nibio.no/>. 
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ipmdecisions.weather.entity.WeatherData;

/**
 * This is the amalgamation service, which takes the responsibility
 * of fixing weather data that needs fixing: QC failing data, missing data,
 * data that needs to be calculated, you name it
 * 
 * @copyright 2021 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@Path("rest/amalgamation")
public class AmalgamationService {
	
	@GET
	@Path("amalgamate/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response amalgamate(
			@QueryParam("endpointURL") String endpointURL,
			@QueryParam("endpointQueryStr") String endpointQueryStr
			)
	{
		try
			{
			System.out.println(endpointURL);
			System.out.println(endpointQueryStr);
			URL completeURL = new URL(endpointURL + "?" + endpointQueryStr);
			//this.dumpResponse(completeURL);
			ObjectMapper objectMapper = new ObjectMapper();
			WeatherData weatherData = objectMapper.readValue(this.getResponseAsPlainText(completeURL), WeatherData.class);
			return Response.ok().entity(weatherData).build();
			//return Response.ok().entity("{}").build();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			return Response.serverError().entity(ex.getMessage()).build();
		} 
	}
	
	private String getResponseAsPlainText(URL theURL) throws IOException
	{
		HttpURLConnection conn = (HttpURLConnection) theURL.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		//System.out.println(response.toString());
		// print result
		return response.toString();
	}
	
	@GET
	@Path("heartbeat/")
	@Produces(MediaType.TEXT_PLAIN)
	public Response heartbeat()
	{
		return Response.ok().entity("Amalgamate service: Pulse detected!").build();
	}
}
