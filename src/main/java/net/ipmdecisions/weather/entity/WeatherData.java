/*
 * Copyright (c) 2020 NIBIO <http://www.nibio.no/>. 
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

package net.ipmdecisions.weather.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class WeatherData {
    private Instant timeStart; // Timestamp of first observation
    private Instant timeEnd; // Timestamp of last observation
    private Integer interval; // Sampling frequency in seconds
    private Integer[] weatherParameters;
    private List<LocationWeatherData> locationWeatherData;

    /**
     * @return the timeStart
     */
    public Instant getTimeStart() {
        return timeStart;
    }
    
    
    /**
     * @param timeStart the timeStart to set
     */
    public void setTimeStart(Instant timeStart) {
        this.timeStart = timeStart;
    }

    /**
     * @return the timeEnd
     */
    public Instant getTimeEnd() {
        return timeEnd;
    }
    

    /**
     * @param timeEnd the timeEnd to set
     */
    public void setTimeEnd(Instant timeEnd) {
        this.timeEnd = timeEnd;
    }

    /**
     * @return the interval
     */
    public Integer getInterval() {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    /**
     * @return the weatherParameters
     */
    public Integer[] getWeatherParameters() {
        return weatherParameters;
    }

    /**
     * @param weatherParameters the weatherParameters to set
     */
    public void setWeatherParameters(Integer[] weatherParameters) {
        this.weatherParameters = weatherParameters;
    }

    /**
     * @return the locationWeatherData
     */
    public List<LocationWeatherData> getLocationWeatherData() {
        return locationWeatherData;
    }

    /**
     * @param locationWeatherData the locationWeatherData to set
     */
    public void setLocationWeatherData(List<LocationWeatherData> locationWeatherData) {
        this.locationWeatherData = locationWeatherData;
    }
    
    public void addLocationWeatherData(LocationWeatherData locationWeatherData) {
        if (this.locationWeatherData == null)
        {
            this.locationWeatherData = new ArrayList<>();
        }
        this.locationWeatherData.add(locationWeatherData);
    }
}
