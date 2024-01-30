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
import java.net.URLEncoder;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;


import net.ipmdecisions.weather.amalgamation.AmalgamationServiceErrorMessage;
import net.ipmdecisions.weather.amalgamation.Interpolation;
import net.ipmdecisions.weather.amalgamation.indices.IndicesBean;
import net.ipmdecisions.weather.controller.AmalgamationBean;
import net.ipmdecisions.weather.controller.WeatherDataSourceBean;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.LocationWeatherDataException;
import net.ipmdecisions.weather.entity.WeatherDataSourceException;
import net.ipmdecisions.weather.qc.QualityControlMethods;
import net.ipmdecisions.weather.util.WeatherDataUtil;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.entity.WeatherDataSource;

/**
 * This is the amalgamation service, which takes the responsibility of fixing
 * weather data that needs fixing: QC failing data, missing data, data that
 * needs to be calculated, you name it
 * 
 * @copyright 2021-2024 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@Path("rest/amalgamation")
public class AmalgamationService {
	
	private static Logger LOGGER = LoggerFactory.getLogger(AmalgamationService.class);
	
	private Response returnError(Integer statusCode, String message)
	{
		// TODO: Create errormessage object - return it
		return Response.status(statusCode).entity(message).build();
	}
	
	
	@EJB
	AmalgamationBean amalgamationBean;
	
	@EJB
	IndicesBean indicesBean;
        
        @EJB
        WeatherDataSourceBean weatherDataSourceBean;
        
        
        /**
	 * Attempts to give you all the requested parameters for the given location
	 * in the specified period. It's a best effort.
	 * @param longitude
	 * @param latitude
	 * @param timeStartStr ISO Date (e.g. 2021-03-01) 
	 * @param timeEndStr ISO Date (e.g. 2021-09-01) 
         * @param interval logging interval for weather data in seconds. Hourly = 3600, daily= 86400
	 * @param parametersStr
	 * @return
	 */
	@GET
	@Path("amalgamate")
	@Produces(MediaType.APPLICATION_JSON)
        public Response amalgamateGET(@QueryParam("longitude") Double longitude,
			@QueryParam("latitude") Double latitude,
			@QueryParam("timeStart") String timeStartStr,
			@QueryParam("timeEnd") String timeEndStr,
			@QueryParam("interval") Integer interval,
			@QueryParam("parameters") String parametersStr)
        {
            return this.amalgamate(longitude, latitude, timeStartStr, timeEndStr, interval, parametersStr, null);
        }
        
        /**
	 * Attempts to give you all the requested parameters for the given location
	 * in the specified period. It's a best effort.
	 * @param longitude
	 * @param latitude
	 * @param timeStartStr ISO Date (e.g. 2021-03-01) 
	 * @param timeEndStr ISO Date (e.g. 2021-09-01) 
         * @param interval logging interval for weather data in seconds. Hourly = 3600, daily= 86400
         * @param privateWeatherStationInfo Json information with information about the private weather data source. Example: {"weatherStationId": "18150444", "weatherSourceId": "com.meteobot", "userName": "theUser","password":"theSuperPassword"}
	 * @param parametersStr
	 * @return
	 */
	@POST
	@Path("amalgamate/private")
        @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
        public Response amalgamatePOST(@QueryParam("longitude") Double longitude,
			@QueryParam("latitude") Double latitude,
			@QueryParam("timeStart") String timeStartStr,
			@QueryParam("timeEnd") String timeEndStr,
			@QueryParam("interval") Integer interval,
			@QueryParam("parameters") String parametersStr,
                        JsonNode privateWeatherStationInfo
            )
        {
            return this.amalgamate(longitude, latitude, timeStartStr, timeEndStr, interval, parametersStr, privateWeatherStationInfo);
        }
	
	/**
	 * Attempts to give you all the requested parameters for the given location
	 * in the specified period. It's a best effort.
	 * @param longitude
	 * @param latitude
	 * @param timeStartStr ISO Date (e.g. 2021-03-01) 
	 * @param timeEndStr ISO Date (e.g. 2021-09-01) 
         * @param interval logging interval for weather data in seconds. Hourly = 3600, daily= 86400
	 * @param parametersStr
	 * @return
	 */
	@GET
	@Path("amalgamate")
	@Produces(MediaType.APPLICATION_JSON)
	private Response amalgamate(
			@QueryParam("longitude") Double longitude,
			@QueryParam("latitude") Double latitude,
			@QueryParam("timeStart") String timeStartStr,
			@QueryParam("timeEnd") String timeEndStr,
			@QueryParam("interval") Integer interval,
			@QueryParam("parameters") String parametersStr,
                        JsonNode privateWeatherStationInfo
	) {
            WeatherDataUtil wdUtil = new WeatherDataUtil();
		try
		{
			if(parametersStr == null)
			{
				return Response.status(Status.BAD_REQUEST).entity(
						List.of(new AmalgamationServiceErrorMessage(null,"No weather parameters requested", Status.BAD_REQUEST.getStatusCode()))
						).build();
			}
			List<Integer> requestedParameters = Arrays.asList(parametersStr.split(",")).stream()
					.map(p->Integer.valueOf(p.trim()))
					.collect(Collectors.toList());
			ZoneId tzForLocation = amalgamationBean.getTimeZoneForLocation(longitude, latitude);
			Instant timeStart = LocalDate.parse(timeStartStr).atStartOfDay(tzForLocation).toInstant();
			Instant timeEnd = LocalDate.parse(timeEndStr).atStartOfDay(tzForLocation).toInstant();
			DateTimeFormatter format = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(tzForLocation);
			List<WeatherDataSource> wdss = amalgamationBean.getWeatherDataSourcesInPriorityOrder(longitude, latitude, 
					requestedParameters,
					timeStart,
					timeEnd);
			//wdss.forEach(w->System.out.println(w.getName()));
                        
                        // If the user has provided info about a private weather station,
                        // add this to the list of weather data sources
                        LOGGER.debug("privateWeatherStationInfo is " + (privateWeatherStationInfo != null? "not" : "") + " null");
                        WeatherDataSource privateWeatherDataSource = null;
                        if(privateWeatherStationInfo != null)
                        {
                            String weatherDataSourceId = privateWeatherStationInfo.get("weatherSourceId").asText();
                            privateWeatherDataSource = weatherDataSourceBean.getWeatherDataSourceById(weatherDataSourceId);
                            wdss.add(privateWeatherDataSource);
                            // Places the private source as the first (top priority)
                            Integer index = wdss.indexOf(privateWeatherDataSource);
                            Collections.rotate(wdss.subList(0, index+1), 1);
                        }
                        
			if(wdss.isEmpty())
			{
				return Response.status(Status.NOT_FOUND).entity(
						List.of(new AmalgamationServiceErrorMessage(null,"No weather data found for given location and period", Status.NOT_FOUND.getStatusCode()))
						).build();
			}
			List<WeatherData> weatherDataFromSources = new ArrayList<>();
			List<AmalgamationServiceErrorMessage> errorLog = new ArrayList<>();
			for(WeatherDataSource currentWDS:wdss)
			{
				// The data source might not provide the requested interval. 
				// If so: Find the least fine-grained one that's still
				// more fine-grained than the requested interval.
				Integer bestAvailableInterval = Arrays.asList(currentWDS.getTemporal().getIntervals()).stream()
						.filter(i -> i <= interval)
						.max(Integer::compare).get();
						
				// Is the data source location or station based?
				String endpoint;
                                String parameters;
				//URLEncoder urlEncoder = URLEncoder.
				if(currentWDS.getAccess_type().equals(WeatherDataSource.ACCESS_TYPE_STATIONS))
				{
					String weatherStationId = currentWDS !=  privateWeatherDataSource ? 
                                                currentWDS.getIdOfClosestStation(longitude, latitude)
                                                : privateWeatherStationInfo.get("weatherStationId").asText();
					// Is it close enough??
					// For now: Set default max distance between location and distance to 3 km (3000 m)
					// TODO: Define the tolerance more generally
                                        try
                                        {
                                            if(currentWDS.getDistanceToStation(weatherStationId, longitude, latitude) > 3000.0){
                                                    continue;
                                            }
					}
                                        catch(NullPointerException ex)
                                        {

                                        }
					// Making sure we get all the parameters available for the station
					Set<Integer> wdsParameters = Arrays.stream(currentWDS.getParameters().getCommon()).boxed().collect(Collectors.toSet());
					if(wdsParameters == null)
					{
						wdsParameters = new HashSet<>();
					}
					wdsParameters.addAll(currentWDS.getAdditionalParametersForStation(weatherStationId));

					endpoint = currentWDS.getEndpointFullPath() ;
                                        parameters = "weatherStationId=" +  weatherStationId
                                                        + "&timeZone=" + URLEncoder.encode(tzForLocation.getId(), "UTF-8")
							+ "&timeStart=" + URLEncoder.encode(format.format(timeStart), "UTF-8")
							+ "&timeEnd=" + URLEncoder.encode(format.format(timeEnd), "UTF-8")
							+ "&interval=" + bestAvailableInterval
							+ "&parameters=" + wdsParameters.stream().map(String::valueOf).collect(Collectors.joining(","));
                                        LOGGER.debug("currentWDS.getAccess_type()=" + currentWDS.getAccess_type());
                                        if(currentWDS.getAuthentication_type() != null && currentWDS.getAuthentication_type().equals(WeatherDataSource.AUTHENTICATION_TYPE_CREDENTIALS))
                                        {
                                            parameters += "&credentials={\"userName\":\"" + privateWeatherStationInfo.get("userName").asText() + "\", \"password\":\"" + privateWeatherStationInfo.get("password").asText() + "\"}";
                                        }
				}
				else
				{
					endpoint = currentWDS.getEndpointFullPath();
					parameters = "longitude=" + longitude 
						+ "&latitude=" + latitude 
						+ "&timeStart=" + URLEncoder.encode(format.format(timeStart), "UTF-8")
						+ "&timeEnd=" + URLEncoder.encode(format.format(timeEnd), "UTF-8")
						+ "&interval=" + bestAvailableInterval;
					
				}
				
				LOGGER.debug(currentWDS.getName() + ":  " + endpoint + "?" + parameters);

				try
				{
					Map<String, String> authentication = null;
					if(currentWDS.getAuthentication_type().equals(WeatherDataSource.AUTHENTICATION_TYPE_BEARER_TOKEN))
					{
						authentication = new HashMap<>();
						authentication.put(WeatherDataSource.AUTHENTICATION_TYPE_BEARER_TOKEN, this.getWeatherDataSourceBearerToken(currentWDS.getId()));
					}
					weatherDataFromSources.add(this.getWeatherDataFromSource(endpoint, parameters, currentWDS.getAuthentication_type(),authentication));
					//System.out.println("Successfully added " + currentWDS.getName());
				}
				catch(WeatherDataSourceException ex)
				{
					errorLog.add( new AmalgamationServiceErrorMessage(
								ex.getDataSourceURL(),
								ex.getMessage(), 
								ex.getHttpErrorCode()
								)
							);

				}
			}
			
			// Fail or success?
			// Error on all data sources -> safe to say that we've failed
			if(weatherDataFromSources.isEmpty())
			{
				return Response.status(Status.SERVICE_UNAVAILABLE).entity(errorLog).build();
			}
			
			// TODO: Catch that some sources have not failed, but no or almost no data has been fetched
                        /*WeatherData test = weatherDataFromSources.get(0);
                        Arrays.asList(test.getLocationWeatherData().get(0).getData()[0]).forEach(d->System.out.println(d));
                        Arrays.asList(test.getWeatherParameters()).forEach(d->System.out.println(d));
                        System.out.println("timeStart=" + test.getTimeStart() + ", timeEnd=" + test.getTimeEnd());*/
			WeatherData fusionedData = amalgamationBean.getFusionedWeatherData(
					weatherDataFromSources,
					timeStart,
					timeEnd,
					interval,
					tzForLocation
					);

			// Dumping current weather data to console
			//System.out.println(wdUtil.serializeWeatherData(fusionedData));

			// 1.  Data control
			// 1.1 Are there missing parameters?
			Set<Integer> missingParameters = requestedParameters != null && ! requestedParameters.isEmpty() && fusionedData.getWeatherParameters() != null ? 
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
			fusionedData = indicesBean.calculateIndicesBestEffort(fusionedData, missingParameters);
			missingParameters = requestedParameters != null && ! requestedParameters.isEmpty() ? 
					this.getMissingParameters(requestedParameters, Arrays.asList(fusionedData.getWeatherParameters()))
					: new HashSet<>();
			
			// Remove any parameters not requested
			List<Integer> parametersToRemove = Arrays.asList(fusionedData.getWeatherParameters()).stream()
					.filter(param->!requestedParameters.contains(param))
					.collect(Collectors.toList());
			for(Integer parameterToRemove:parametersToRemove)
			{
				fusionedData.removeParameter(parameterToRemove);
			}
			
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule()); 
			//System.out.println(objectMapper.writeValueAsString(fusionedData));
			
			// Chop away any missing data at the beginning and end of the data set
			
			fusionedData = wdUtil.trimDataSet(fusionedData);
			
			return Response.ok().entity(fusionedData).build();
		}
		catch(IOException | LocationWeatherDataException ex)
		{
			LOGGER.error(ex.getMessage(), ex);
				return Response.status(Status.SERVICE_UNAVAILABLE).entity(
						new AmalgamationServiceErrorMessage(null, ex.getMessage(), Status.SERVICE_UNAVAILABLE.getStatusCode())
						).build();
				
		}
		
		
	}

	/**
	 * Given that you can only provide one coordinate, the method handles single locations, not grids
	 * @param endpointURL The URL (excluding query parameters). Must be URL encoded (https://en.wikipedia.org/wiki/Percent-encoding)
	 * @param endpointQueryStr The query parameters. Must be URL encoded (https://en.wikipedia.org/wiki/Percent-encoding)
	 * @param longitude Location Weather data location longitude (Decimal degrees) for amalgamation purposes
	 * @param latitude Location Weather data location latitude (Decimal degrees) for amalgamation purposes
	 * 
	 * @pathExample /rest/amalgamation/amalgamate/?endpointURL=https%3A%2F%2Fplatform.ipmdecisions.net%2Fapi%2Fwx%2Frest%2Fweatheradapter%2Fyr%2F&endpointQueryStr=longitude%3D14.3711%26latitude%3D67.2828%26altitude%3D70%26parameters%3D1001%2C3001
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
			//URL completeURL = new URL(endpointURL + (endpointQueryStr.indexOf("?") == 0 ? "" : "?") + endpointQueryStr);
			WeatherData dataFromSource = this.getWeatherDataFromSource(endpointURL, endpointQueryStr, null, null); 
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
	
	private WeatherData getWeatherDataFromSource(String endpoint, String parameters, String authenticationType, Map<String,String> authentication) throws JsonMappingException, JsonProcessingException, IOException, WeatherDataSourceException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		WeatherData weatherData = objectMapper.readValue(this.getResponseAsPlainText(endpoint, parameters, authenticationType, authentication),
				WeatherData.class);
		return weatherData;
	}

	private String getResponseAsPlainText(String endpoint, String parameters, String authenticationType, Map<String,String> authentication) throws IOException, WeatherDataSourceException {
		
                URL theURL = new URL(endpoint + (
                            authenticationType != null && authenticationType.equals(WeatherDataSource.AUTHENTICATION_TYPE_CREDENTIALS) ?
                                ""
                                :"?" + parameters
                            )
                        );
                HttpURLConnection conn = (HttpURLConnection) theURL.openConnection();
		if(authenticationType != null && ! authenticationType.equals(WeatherDataSource.AUTHENTICATION_TYPE_NONE))
		{
			if(authenticationType.equals(WeatherDataSource.AUTHENTICATION_TYPE_BEARER_TOKEN))
			{
				conn.setRequestProperty("Authorization",authentication.get(WeatherDataSource.AUTHENTICATION_TYPE_BEARER_TOKEN));
			}
                        else if (authenticationType.equals(WeatherDataSource.AUTHENTICATION_TYPE_CREDENTIALS))
                        {
                            conn.setRequestMethod("POST");
                            byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);
                            int postDataLength = postData.length;
                            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                            conn.setDoOutput(true);
                            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                                wr.write(postData);
                            }
                        }
		}
                
		int resultCode = conn.getResponseCode();
		// Follow redirects, also https
		if(resultCode == HttpURLConnection.HTTP_MOVED_PERM || resultCode == HttpURLConnection.HTTP_MOVED_TEMP)
		{
			String location = conn.getHeaderField("Location");
			conn.disconnect();
			conn = (HttpURLConnection)  new URL(location).openConnection();
		}
		
		StringBuffer response = new StringBuffer();
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(resultCode < 400 ? conn.getInputStream():conn.getErrorStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		}
		catch(NullPointerException ex)
		{
			throw new WeatherDataSourceException("ERROR: No data returned from data source. The request was: " + theURL.toString());
		}
		
		// Are we getting anything else but 200? Throw Exception
		resultCode = conn.getResponseCode();
		if(resultCode != HttpURLConnection.HTTP_OK)
		{
			throw new WeatherDataSourceException(
					theURL.toString(),
					response.toString(),
					resultCode
					);
		}
		//LOGGER.debug(response.toString());
		return response.toString();
	}

	@GET
	@Path("heartbeat/")
	@Produces(MediaType.TEXT_PLAIN)
	public Response heartbeat() {
		return Response.ok().entity("Amalgamate service: Pulse detected!").build();
	}
	
	
	private String getWeatherDataSourceBearerToken(String weatherDataSourceId)
	{
		return "Bearer " + System.getProperty("net.ipmdecisions.weatherservice.BEARER_TOKEN_" + weatherDataSourceId);
	}
	
}
