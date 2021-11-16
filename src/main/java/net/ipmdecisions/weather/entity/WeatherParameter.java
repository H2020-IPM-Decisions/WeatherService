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

import com.webcohesion.enunciate.metadata.DocumentationExample;
import javax.validation.constraints.NotNull;

/**
 * Represents a type of agrometeorological measurement relevant to the 
 * IPM Decisions platform. Complete list: https://ipmdecisions.nibio.no/weather/rest/parameter/list
 * 
 * @copyright 2021 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class WeatherParameter {
	
	/** Method for how to create e.g. daily values from hourly values */
	public final static String AGGREGATION_TYPE_AVERAGE = "AVG";
	/** Method for how to create e.g. daily values from hourly values */
	public final static String AGGREGATION_TYPE_MINIMUM = "MIN";
	/** Method for how to create e.g. daily values from hourly values */
	public final static String AGGREGATION_TYPE_MAXIMUM = "MAX";
	/** Method for how to create e.g. daily values from hourly values */
	public final static String AGGREGATION_TYPE_SUM = "SUM";
	
    @NotNull
    private Integer id;
    @NotNull
    private String name;
    private String description;
    @NotNull
    private String unit;
    private String aggregationType;

    /**
     * @return A numeric code for unique reference to this parameter
     */
    @DocumentationExample("1002")
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The common name of this parameter
     */
    @DocumentationExample("Mean air temperature at 2m")
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Potentially, a longer description and definition of the parameter
     */
    @DocumentationExample("Long and thorough description of the parameter goes here.")
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The unit this parameter is measured in. E.g. celcius, mm, Watt/square meter
     */
    @DocumentationExample("°C")
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

	/**
	 * @return Method for how to create e.g. daily values from hourly values. 
	 * Possible values: 
	 * <ul>
	 * <li>WeatherParameter.AGGREGATION_TYPE_AVERAGE</li>
	 * <li>WeatherParameter.AGGREGATION_TYPE_MINIMUM</li>
	 * <li>WeatherParameter.AGGREGATION_TYPE_MAXIMUM</li>
	 * <li>WeatherParameter.AGGREGATION_TYPE_SUM</li>
	 * </ul>
	 */
	public String getAggregationType() {
		return aggregationType;
	}

	/**
	 * @param aggregationType the aggregationType to set
	 */
	public void setAggregationType(String aggregationType) {
		this.aggregationType = aggregationType;
	}
    
}
