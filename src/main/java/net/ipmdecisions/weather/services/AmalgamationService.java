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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ipmdecisions.weather.amalgamation.Interpolation;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.LocationWeatherDataException;
import net.ipmdecisions.weather.entity.WeatherData;

/**
 * This is the amalgamation service, which takes the responsibility of fixing
 * weather data that needs fixing: QC failing data, missing data, data that
 * needs to be calculated, you name it
 * 
 * @copyright 2021 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@Path("rest/amalgamation")
public class AmalgamationService {

	/**
	 * Given that you can only provide one coordinate, the method handles single locations, not grids
	 * @param endpointURL The URL (excluding query parameters). Must be URL encoded (https://en.wikipedia.org/wiki/Percent-encoding)
	 * @param endpointQueryStr The query parameters. Must be URL encoded (https://en.wikipedia.org/wiki/Percent-encoding)
	 * @param longitude Location Weather data location longitude (Decimal degrees) for amalgamation purposes
	 * @param latitude Location Weather data location latitude (Decimal degrees) for amalgamation purposes
	 * 
	 * @pathExample /rest/amalgamation/amalgamate/?endpointURL=https%3A%2F%2Fipmdecisions.nibio.no%2Fapi%2Fwx%2Frest%2Fweatheradapter%2Fyr%2F&endpointQueryStr=longitude%3D14.3711%26latitude%3D67.2828%26altitude%3D70%26parameters%3D1001%2C3001
	 * 
	 * @return
	 */
	@GET
	@Path("amalgamate/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response amalgamate(
			@QueryParam("endpointURL") String endpointURL,
			@QueryParam("endpointQueryStr") String endpointQueryStr,
			@QueryParam("longitude") Double longitude,
			@QueryParam("latitude") Double latitude
	) {
		try {
			//System.out.println(endpointURL);
			//System.out.println(endpointQueryStr);
			URL completeURL = new URL(endpointURL + (endpointQueryStr.indexOf("?") == 0 ? "" : "?") + endpointQueryStr);
			WeatherData dataFromSource = this.getWeatherDataFromSource(completeURL); 
			// Checks!
			
			// 1. Are there missing parameters?
			List<Integer> requestedParameters = this.getParametersFromQueryString(endpointQueryStr);
			List<Integer> missingParameters = requestedParameters != null && ! requestedParameters.isEmpty() ? 
					this.getMissingParameters(requestedParameters, Arrays.asList(dataFromSource.getWeatherParameters()))
					: new ArrayList<>();
			
			// 2. Parameters which failed QC tests?
			// NB!! Only checking one of potentially many locationweatherdata sets!!!!!
			LocationWeatherData lwd = dataFromSource.getLocationWeatherData().get(0);
			
			if(lwd.needsQC())
			{
				// TODO: Run the QC
			}
			Set<Integer> failedParameters = new HashSet<>();
			Integer[] QC = lwd.getQC();
			if(QC != null)
			{
				for(int i=0;i<QC.length;i++)
				{
					if(QC[i] >= 4)
					{
						failedParameters.add(dataFromSource.getWeatherParameters()[i]);
					}
				}
			}
			
			if(missingParameters.size() + failedParameters.size() > 0)
			{
				//System.out.println("We need to amalgamate!");
				// Amalgamations!
				// ---> Prioritized replacement sources (advanced algorithm??)
				// ---> Data that can be calculated (LW, others?)
				
				// Parameters missing entirely
				for(Integer missingParameter:missingParameters)
				{
					// Can it be calculated? Case of LW
					// Can it be obtained from Mars/ECMWF?
				}
				
				// 
				// ---> Data that can be interpolated (TM, TT, NOT others), and max 1 hour (for now)
				// Failed parameters that are interpolatable should be attempted interpolated
				// NOTE: Failed parameters may be NOT missing (but rather be of value -6999 etc). Should we NULL them first? And how?
				// Need a list of these parameters
				// 2021-09-28 Testing with three missing values: https://lmt.nibio.no/services/rest/ipmdecisions/getdata/?weatherStationId=23&parameters=2001,1002&interval=3600&timeStart=2021-08-19T00:00:00%2B02:00&timeEnd=2021-08-20T15:00:00%2B02:00
				Set<Integer> paramsToInterpolate = failedParameters.stream()
						.filter(p-> p >= 1000 && p < 2000) // Temperatures are in the range of 1000-1999
						.collect(Collectors.toSet());
				dataFromSource = new Interpolation().interpolate(dataFromSource, paramsToInterpolate, 1);
			}
			
			
			
			// this.dumpResponse(completeURL);
			return Response.ok().entity(dataFromSource).build();
		} catch (IOException | LocationWeatherDataException ex) {
			ex.printStackTrace();
			return Response.serverError().entity(ex.getMessage()).build();
		}
	}
	
	/**
	 * Linear interpolation. Send the weather data in the request body. Specify what to do with query parameters
	 * @param maxMissingValues Don't interpolate if the number of contiguously missing values exceeds this number (default = 1)
	 * @param paramsToInterpolate the parameters to interpolate
	 * @param weatherDataStr Json weather data in the request body. Can very well include parameters that aren't to be interpolated 
	 * @pathExample /rest/amalgamation/interpolate?maxMissingValues=3&paramsToInterpolate=1002
	 * @return the weather data set with performed interpolation of the specified parameters
	 */
	@GET
	@Path("interpolate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInterpolatedData(
			@QueryParam("maxMissingValues") Integer maxMissingValues,
			@QueryParam("paramsToInterpolate") Set<Integer> paramsToInterpolate,
			String weatherDataStr // This actually is the request body. Quite neat!
			)
	{
		try
		{
			maxMissingValues = maxMissingValues != null ? maxMissingValues : 1;
			WeatherData input = WeatherData.getInstanceFromString(weatherDataStr);
			WeatherData output = new Interpolation().interpolate(input, paramsToInterpolate, maxMissingValues);
			return Response.ok().entity(output).build();
		}
		catch(JsonProcessingException | LocationWeatherDataException ex)
		{
			return Response.serverError().entity(ex.getMessage()).build();
		}
	}
	
	
	private List<Integer> getMissingParameters(List<Integer> requestedParameters, List<Integer> returnedParameters)
	{
		// Using set relative complement (difference) operation 
		Set<Integer> missingParameters = new HashSet<>(requestedParameters);
		missingParameters.removeAll(new HashSet<Integer>(returnedParameters));
		return new ArrayList<Integer>(missingParameters);
	}
	
	/**
	 * 
	 * @param queryStr
	 * @return
	 */
	private List<Integer> getParametersFromQueryString(String queryStr)
	{
		String[] parts = queryStr.split("&");
		for(String part:parts)
		{
			if(part.indexOf("parameters") >= 0)
			{
				return Arrays.asList(part.substring(part.indexOf("=") + 1).split(",")).stream()
				.map(parameter-> Integer.valueOf(parameter))
				.collect(Collectors.toList());
			}
		}
		return null;
	}
	
	private WeatherData getWeatherDataFromSource(URL theURL) throws JsonMappingException, JsonProcessingException, IOException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		WeatherData weatherData = objectMapper.readValue(this.getResponseAsPlainText(theURL),
				WeatherData.class);
		return weatherData;
	}

	private String getResponseAsPlainText(URL theURL) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) theURL.openConnection();
		int resultCode = conn.getResponseCode();
		// Follow redirects, also https
		if(resultCode == HttpURLConnection.HTTP_MOVED_PERM || resultCode == HttpURLConnection.HTTP_MOVED_TEMP)
		{
			String location = conn.getHeaderField("Location");
			conn.disconnect();
			conn = (HttpURLConnection)  new URL(location).openConnection();
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		//System.out.println(response.toString());
		return response.toString();
	}

	@GET
	@Path("heartbeat/")
	@Produces(MediaType.TEXT_PLAIN)
	public Response heartbeat() {
		return Response.ok().entity("Amalgamate service: Pulse detected!").build();
	}
}
