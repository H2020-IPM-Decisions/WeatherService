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

import java.net.URL;
import java.util.Date;
import java.util.Locale;

import javax.xml.datatype.DatatypeConfigurationException;

import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;

/**
 * Fetches gridded seasonal weather data and forecasts for in Sweden. The service is provided by the Fältforsk database (https://www.slu.se/faltforsk)
 * at the Swedish University of Agricultural Sciences (https://www.slu.se/en/). It is compatible with the VIPS weather service protocol, and we're 
 * adapting it to IPM Decisions in this class
 * 
 * @author Tor-Einar Skog<tor-einar.skog@nibio.no>
 *
 */
public class SLUFaltforskAdapter {
	
	private final static String SLU_API_URL = "https://www.ffe.slu.se/lm/json/DownloadJS.cfm?centerWGS84n=%d,";

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
    public WeatherData getData(Double longitude, Double latitude, Date dateFrom, Date dateTo, Integer interval) throws DatatypeConfigurationException
    {
    	URL sluURL;
    	LocationWeatherData sluValues;
    	try
    	{
    		sluURL = new URL(String.format(Locale.US,
    				SLUFaltforskAdapter.SLU_API_URL, 
    				latitude,
    				longitude))
    	}
    	return null;
    }
}
