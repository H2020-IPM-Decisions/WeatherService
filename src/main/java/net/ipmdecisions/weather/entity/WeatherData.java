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

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaDescription;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaExamples;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaInject;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaString;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * We are using this tool to serialize to Json Schema: https://github.com/mbknor/mbknor-jackson-jsonSchema
 * 
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@JsonSchemaInject(strings = {
    @JsonSchemaString(path = "$id", value="https://ipmdecisions.nibio.no/WeatherService/rest/schema/weatherdata"),
    @JsonSchemaString(path = "properties/timeStart/type", value="string")
    }
)
@JsonSchemaTitle("Weather Data")
@JsonSchemaExamples("http://ipmdecisions.nibio.no/WeatherService/rest/forecasts/yr/?longitude=14.3711&latitude=67.2828&altitude=70")
@JsonSchemaDescription("Version 0.1. The schema describes the weather data format for the IPM Decisions platform. See an example here: http://ipmdecisions.nibio.no/WeatherService/rest/forecasts/yr/?longitude=14.3711&latitude=67.2828&altitude=70")
public class WeatherData {
    @NotNull
    @JsonPropertyDescription("The timestamp of the first weather observation")
    private Instant timeStart;
    @NotNull
    @JsonPropertyDescription("The timestamp of the last weather observation")
    private Instant timeEnd;
    @NotNull
    @Positive
    @JsonPropertyDescription("The sampling frequency in seconds. E.g. 3600 = hourly values")
    private Integer interval; 
    @NotNull
    @Size(min=1)
    @JsonPropertyDescription("The weather parameters. For reference, see https://ipmdecisions.nibio.no/WeatherService/rest/parameter/list")
    private Integer[] weatherParameters;
    @JsonPropertyDescription("The weather data per location.")
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
