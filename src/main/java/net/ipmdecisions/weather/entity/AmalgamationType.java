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

package net.ipmdecisions.weather.entity;

import com.webcohesion.enunciate.metadata.DocumentationExample;

/**
 * Represents an amalgamation type for a parameter. This is used in the amalgamation property of
 * the weather data standard. The id is in binary, so it's bitmapped
 * This means that if several tests fails for a parameter, each test can be specified.
 * E.g. If a parameter has been both interpolated (value=2) and replaced (value=2), the 
 * value will be 3 (1 + 2)
 * 
 * @copyright 2021 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class AmalgamationType {
    
    public final static Integer NONE = 0;
    public final static Integer REPLACED = 1;
    public final static Integer INTERPOLATED = 2;
    public final static Integer CALCULATED = 4;
    
    private Integer id;
    private String name, description;

    /**
     * @return the id
     * The id is in binary, so it's bitmapped
     * This means that if several tests fails for a parameter, each test can be specified.
     * E.g. If logical test (id=16) and interval test (id=8) fails, then the QC value will be 16 + 8 = 24
     */
    @DocumentationExample("4")
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
     * @return the name
     */
    @DocumentationExample("Calculated")
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
     * @return the description
     */
    @DocumentationExample("The weather parameter is missing from the set of data and has been calculated based on other parameters in the set")
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
