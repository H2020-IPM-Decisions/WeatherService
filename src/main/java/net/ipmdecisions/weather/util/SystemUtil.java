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
package net.ipmdecisions.weather.util;

/**
 * 
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 *
 */
public class SystemUtil {
	
	/**
	 * 
	 * @return the hostname to this instance of the API, if configured server side in the system property WEATHER_API_URL. Otherwise, the default url ("https://platform.ipmdecisions.net")
	 *
	 */
	public static String getWeatherAPIURL()
    {
    	return System.getProperty("net.ipmdecisions.weatherservice.WEATHER_API_URL") != null && ! System.getProperty("net.ipmdecisions.weatherservice.WEATHER_API_URL").isBlank() ? System.getProperty("net.ipmdecisions.weatherservice.WEATHER_API_URL")
    			: "https://platform.ipmdecisions.net";
    }
}
