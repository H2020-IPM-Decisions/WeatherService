/*
 * Copyright (c) 2022 NIBIO <http://www.nibio.no/>. 
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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.time.Instant;
import java.time.LocalDate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.iakovlev.timeshape.TimeZoneEngine;
import net.ipmdecisions.weather.amalgamation.Interpolation;
import net.ipmdecisions.weather.controller.AmalgamationBean;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.LocationWeatherDataException;
import net.ipmdecisions.weather.entity.WeatherDataSourceException;
import net.ipmdecisions.weather.qc.QualityControlMethods;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.entity.WeatherDataSource;

/**
 * This is the amalgamation service, which takes the responsibility of fixing
 * weather data that needs fixing: QC failing data, missing data, data that
 * needs to be calculated, you name it
 * 
 * @copyright 2021-2022 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@Path("rest/amalgamation")
public class AmalgamationService {
	
	
	
	
	
	@EJB
	AmalgamationBean amalgamationBean;
	
	/**
	 * Attempts to give you all the requested parameters for the given location
	 * in the specified period. It's a best effort.
	 * @param longitude
	 * @param latitude
	 * @param timeStartStr
	 * @param timeEndStr
	 * @param parametersStr
	 * @return
	 */
	@GET
	@Path("amalgamate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response amalgamate(
			@QueryParam("longitude") Double longitude,
			@QueryParam("latitude") Double latitude,
			@QueryParam("timeStart") String timeStartStr,
			@QueryParam("timeEnd") String timeEndStr,
			@QueryParam("interval") Integer interval,
			@QueryParam("parameters") String parametersStr
	) {
		try
		{
			
			List<Integer> requestedParameters = Arrays.asList(parametersStr.split(",")).stream()
					.map(p->Integer.valueOf(p))
					.collect(Collectors.toList());
			ZoneId tzForLocation = amalgamationBean.getTimeZoneForLocation(longitude, latitude);
			Instant timeStart = LocalDate.parse(timeStartStr).atStartOfDay(tzForLocation).toInstant();
			Instant timeEnd = LocalDate.parse(timeEndStr).atStartOfDay(tzForLocation).toInstant();
			WeatherDataSource wds = amalgamationBean.getWeatherDataSourceBestEffort(longitude, latitude);
			List<WeatherDataSource> wdss = amalgamationBean.getWeatherDataSourcesInPriorityOrder(longitude, latitude, 
					requestedParameters,
					timeStart,
					timeEnd);
			//wdss.forEach(w->System.out.println(w.getName()));
			
			List<WeatherData> weatherDataFromSources = new ArrayList<>();
			for(WeatherDataSource currentWDS:wdss)
			{
				URL url = new URL(currentWDS.getEndpoint() 
					+ "?longitude=" + longitude 
					+ "&latitude=" + latitude 
					+ "&timeStart=" + timeStartStr
					+ "&timeEnd=" + timeEndStr
					+ "&interval=" + interval
					// + "&parameters=" + parametersStr // Exclude this in order to collect all parameters from the source
					);
					
				weatherDataFromSources.add(this.getWeatherDataFromSource(url));
			
			}
			WeatherData fusionedData = amalgamationBean.getFusionedWeatherData(
					weatherDataFromSources,
					timeStart,
					timeEnd,
					interval
					);
			/*ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule()); 
			System.out.println(objectMapper.writeValueAsString(fusionedData));
			*/
			
			// 1.  Data control
			// 1.1 Are there missing parameters?
			
			Set<Integer> missingParameters = requestedParameters != null && ! requestedParameters.isEmpty() ? 
					this.getMissingParameters(requestedParameters, Arrays.asList(fusionedData.getWeatherParameters()))
					: new HashSet<>();
			// First fix: Map interchangeable parameters (e.g. instantaneous and average temperatures)
			if(missingParameters.size() > 0)
			{
				fusionedData = amalgamationBean.addFallbackParameters(fusionedData, missingParameters);
				// Refresh the missing parameters
				missingParameters = requestedParameters != null && ! requestedParameters.isEmpty() ? 
						this.getMissingParameters(requestedParameters, Arrays.asList(fusionedData.getWeatherParameters()))
						: new HashSet<>();
			}
			// 1.2 QC check
			// Need to add QC info if missing from data source
			// THIS WILL NEVER RUN - since the getQC() method always returns the correctly sized array
			for(LocationWeatherData lwd:fusionedData.getLocationWeatherData())
			{
				if(lwd.getQC() == null || lwd.getQC().length < fusionedData.getWeatherParameters().length)
				{
					Integer[] qc = new Integer[fusionedData.getWeatherParameters().length];
					for(int i=0;i<qc.length;i++)
					{
						qc[i] = (lwd.getQC() != null && i < lwd.getQC().length) ? lwd.getQC()[i] : 0;
					}
					lwd.setQC(qc);
				}
			}
			
			// Controlling the data
			QualityControlMethods qcm = new QualityControlMethods();
			fusionedData = qcm.getQC(fusionedData);
			// Collecting failed parameters
			Set<Integer> failedParameters = new HashSet<>();
			for(LocationWeatherData lwd:fusionedData.getLocationWeatherData())
			{
				Integer[] QC = lwd.getQC();
				if(QC != null)
				{
					for(int i=0;i<QC.length;i++)
					{
						if(QC[i] >= 4)
						{
							failedParameters.add(fusionedData.getWeatherParameters()[i]);
						}
					}
				}
			}
			
			
			// 2.  Data restoration/generation of failed parameters
			for(Integer failedParam:failedParameters)
			{
				// 2.1 Interpolate
				fusionedData = new Interpolation().interpolate(fusionedData, Set.of(1001,1002),1);
			}
			
			// Calculate entire missing parameters - now that we have the best available dataset 
			// E.g. Leaf wetness
			if(missingParameters.contains(3101))  // Leaf wetness
			{
				fusionedData = amalgamationBean.calculateLeafWetnessBestEffort(fusionedData);
				missingParameters = requestedParameters != null && ! requestedParameters.isEmpty() ? 
						this.getMissingParameters(requestedParameters, Arrays.asList(fusionedData.getWeatherParameters()))
						: new HashSet<>();
			}
			
			// Finally: Remove any parameters not requested
			List<Integer> parametersToRemove = Arrays.asList(fusionedData.getWeatherParameters()).stream()
					.filter(param->!requestedParameters.contains(param))
					.collect(Collectors.toList());
			for(Integer parameterToRemove:parametersToRemove)
			{
				fusionedData.removeParameter(parameterToRemove);
			}
			
			return Response.ok().entity(fusionedData).build();
		}
		catch(IOException | WeatherDataSourceException | LocationWeatherDataException ex)
		{
			return Response.serverError().entity(ex.getMessage()).build();
		}
		
	}

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
	@Path("amalgamate/proxy")
	@Produces(MediaType.APPLICATION_JSON)
	public Response amalgamateProxy(
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
					new ArrayList<Integer>(this.getMissingParameters(requestedParameters, Arrays.asList(dataFromSource.getWeatherParameters())))
					: new ArrayList<>();
			
			// 2. Parameters which failed QC tests?
			// NB!! Only checking one of potentially many locationweatherdata sets!!!!!
			Set<Integer> failedParameters = new HashSet<>();
			for(LocationWeatherData lwd:dataFromSource.getLocationWeatherData())
			{
				if(lwd.needsQC())
				{
					// TODO: Run the QC
				}
				
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
		} catch (IOException | LocationWeatherDataException | WeatherDataSourceException ex) {
			//ex.printStackTrace();
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
	
	
	private Set<Integer> getMissingParameters(List<Integer> requestedParameters, List<Integer> returnedParameters)
	{
		// Using set relative complement (difference) operation 
		Set<Integer> missingParameters = new HashSet<>(requestedParameters);
		missingParameters.removeAll(new HashSet<Integer>(returnedParameters));
		return missingParameters;
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
	
	private WeatherData getWeatherDataFromSource(URL theURL) throws JsonMappingException, JsonProcessingException, IOException, WeatherDataSourceException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		WeatherData weatherData = objectMapper.readValue(this.getResponseAsPlainText(theURL),
				WeatherData.class);
		return weatherData;
	}

	private String getResponseAsPlainText(URL theURL) throws IOException, WeatherDataSourceException {
		//System.out.println(theURL.toString());
		HttpURLConnection conn = (HttpURLConnection) theURL.openConnection();
		int resultCode = conn.getResponseCode();
		// Follow redirects, also https
		if(resultCode == HttpURLConnection.HTTP_MOVED_PERM || resultCode == HttpURLConnection.HTTP_MOVED_TEMP)
		{
			String location = conn.getHeaderField("Location");
			conn.disconnect();
			conn = (HttpURLConnection)  new URL(location).openConnection();
		}
		
		
		BufferedReader in = new BufferedReader(new InputStreamReader(resultCode < 400 ? conn.getInputStream():conn.getErrorStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		// Are we getting anything else but 200? Raise error
		if(conn.getResponseCode() != HttpURLConnection.HTTP_OK)
		{
			throw new WeatherDataSourceException("ERROR: Got Http response code " + conn.getResponseCode() + " from data source. Message from server was: " + response.toString());
		}
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
