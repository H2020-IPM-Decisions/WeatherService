/*
 * Copyright (c) 2021 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of IPMDecisionsWeatherService.
 * IPMDecisionsWeatherService is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * IPMDecisionsWeatherService is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with IPMDecisionsWeatherService.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.ipmdecisions.weather.datasourceadapters;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConfigurationException;

import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;

/**
 * Fetches gridded seasonal weather data and forecasts for in Sweden. The service is provided by the Fältforsk database (https://www.slu.se/faltforsk)
 * at the Swedish University of Agricultural Sciences (https://www.slu.se/en/). It is compatible with the VIPS weather service protocol, and we're 
 * adapting it to IPM Decisions in this class.
 * 
 * In the Swedish growing season (March-October) 36 hours of weather forecasts are included.  
 * 
 * @author Tor-Einar Skog<tor-einar.skog@nibio.no>
 *
 */
public class SLULantMetAdapter {
	
	/*
	 * Parameters used
	 * centerWGS84n = Latitude (WGS84)
	 * centerWGS84e = Longitude (WGS84)
	 * outputType = [JSON(default),CSV] JSON is the VIPS standard weather data format
	 * inputType = [OBSERVED (default),GRID,GRID11] 
	 * logIntervalId = 1 (hourly) or 2 (daily)
	 * startDate = YYYY-mm-dd
	 * endDate = YYYY-mm-dd (set this to a couple of days into the future to get weather forecasts in the Swedish growing season March-October)
	 * elementMeasurementTypeList = comma separated list of weather parameters (VIPS parameter codes)
	 * nDegrees and eDegrees = defining the square around the centerWGSn/e to include. Set to 0 to get exactly one point
	 */
	private final static String SLU_API_URL = "https://www.ffe.slu.se/lm/json/LantmetDWL.cfm"
			+ "?centerWGS84n=%f"
			+ "&centerWGS84e=%f"
			+ "&outputType=JSON"
			+ "&inputType=GRID"
			+ "&logIntervalId=%d"
			+ "&startDate=%s"
			+ "&endDate=%s"
			+ "&elementMeasurementTypeList=%s"
			+ "&nDegrees=0&eDegrees=0";

	// See the IPM Decisions parameters list for details
	private Integer[] defaultParameters = {
			1002, // Mean temp (C) 2m
			1003, // Min temp (C) 2m
			1004, // Max temp (C) 2m
			2001, // Precipitation (mm)
			3002, // Mean RH (%) at 2m
			3003, // Min RH (%) at 2m
			3004, // Max RH (%) at 2m
			4003, // Mean wind speed (m/s) at 2m
			3101, // Leaf wetness (min/hour)
			1102, // Mean temperature (C) at -5cm
			1112 // Mean temperature (C) at -10cm
			};
	
	/**
     * 
     * @param longitude WGS84 Decimal degrees
     * @param latitude WGS84 Decimal degrees
     * @param dateFrom 
     * @param dateTo
     * @param weatherInterval hourly or daily
     * @return
     * @throws DatatypeConfigurationException 
     */
    public WeatherData getData(Double longitude, Double latitude, Date dateFrom, Date dateTo, Integer interval, List<Integer> parameters) throws DatatypeConfigurationException
    {
    	TimeZone tz = TimeZone.getTimeZone("GMT+1");
    	// Input check
    	if(longitude == null || latitude == null)
    	{
    		return null;
    	}
    	if(parameters == null || parameters.isEmpty())
    	{
    		parameters = Arrays.asList(this.defaultParameters);
    	}
    	interval = interval != null ? interval : 1;
    	URL sluURL;
    	LocationWeatherData sluValues;
    	
    	if(dateFrom == null || dateTo == null)
    	{
    		Calendar cal = Calendar.getInstance(tz);
    		cal.setTime(new Date());
    		if(dateTo == null) // Default CURRENT_DATE + 3 days
    		{
    			cal.add(Calendar.DATE, 3);
    			dateTo = cal.getTime();
    		}
    		if(dateFrom == null) // Default CURRENT_YEAR-01-01
    		{
    			cal.set(cal.get(Calendar.YEAR), Calendar.JANUARY,1,12,0,0);
    			dateFrom = cal.getTime();
    		}
    	}
    	
    	SimpleDateFormat format = new SimpleDateFormat("YYYY-mm-dd");

		try {
			sluURL = new URL(String.format(Locale.US,
					SLULantMetAdapter.SLU_API_URL, 
					latitude,
					longitude,
					(interval == 3600 ? 1 : 2),
					format.format(dateFrom),
					format.format(dateTo),
					parameters.stream().map(i->String.valueOf(i)).collect(Collectors.joining(","))
					));
			
			System.out.println(sluURL.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
    		
    	
    	return null;
    }
}
