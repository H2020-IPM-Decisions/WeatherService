/*
 * Copyright (c) 2020 NIBIO <http://www.nibio.no/>. 
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
 * Represents a QC status for a parameter. This is used in the qc property of
 * the weather data standard. The id is in binary, so it's bitmapped
 * This means that if several tests fails for a parameter, each test can be specified.
 * E.g. If logical test (id=16) and interval test (id=8) fails, then the QC value will be 16 + 8 = 24
 * 
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class QC {
    private Integer id;
    private String name, description;

    /**
     * @return the id
     * The id is in binary, so it's bitmapped
     * This means that if several tests fails for a parameter, each test can be specified.
     * E.g. If logical test (id=16) and interval test (id=8) fails, then the QC value will be 16 + 8 = 24
     */
    @DocumentationExample("8")
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
    @DocumentationExample("Failed. Interval test")
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
    @DocumentationExample("Long description goes here")
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
