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

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.time.LocalDate;

/**
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class WeatherDataSource {
    private String name, description, public_URL,endpoint, needs_data_control, access_type, spatial;
    private Temporal temporal;
    private Parameters parameters; 
    
    static class Temporal {
        private int forecast;
        private Historic historic;
        
        static class Historic {
            
            private LocalDate start, end;

            /**
             * @return the start
             */
            @JsonFormat(pattern="yyyy-MM-dd")
            public LocalDate getStart() {
                return start;
            }

            /**
             * @param start the start to set
             */
            public void setStart(LocalDate start) {
                this.start = start;
            }

            /**
             * @return the end
             */
            @JsonFormat(pattern="yyyy-MM-dd")
            public LocalDate getEnd() {
                return end;
            }

            /**
             * @param end the end to set
             */
            public void setEnd(LocalDate end) {
                this.end = end;
            }
        }

        /**
         * @return the forecast
         */
        public int getForecast() {
            return forecast;
        }

        /**
         * @param forecast the forecast to set
         */
        public void setForecast(int forecast) {
            this.forecast = forecast;
        }

        /**
         * @return the historic
         */
        public Historic getHistoric() {
            return historic;
        }

        /**
         * @param historic the historic to set
         */
        public void setHistoric(Historic historic) {
            this.historic = historic;
        }
    }
    
    static class Parameters {
        private int[] common, optional;

        /**
         * @return the common
         */
        public int[] getCommon() {
            return common;
        }

        /**
         * @param common the common to set
         */
        public void setCommon(int[] common) {
            this.common = common;
        }

        /**
         * @return the optional
         */
        public int[] getOptional() {
            return optional;
        }

        /**
         * @param optional the optional to set
         */
        public void setOptional(int[] optional) {
            this.optional = optional;
        }
    }            

    /**
     * @return the name
     */
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
     * @return the public_URL
     */
    public String getPublic_URL() {
        return public_URL;
    }

    /**
     * @param public_URL the public_URL to set
     */
    public void setPublic_URL(String public_URL) {
        this.public_URL = public_URL;
    }

    /**
     * @return the endpoint
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * @return the needs_data_control
     */
    public String getNeeds_data_control() {
        return needs_data_control;
    }

    /**
     * @param needs_data_control the needs_data_control to set
     */
    public void setNeeds_data_control(String needs_data_control) {
        this.needs_data_control = needs_data_control;
    }

    /**
     * @return the access_type
     */
    public String getAccess_type() {
        return access_type;
    }

    /**
     * @param access_type the access_type to set
     */
    public void setAccess_type(String access_type) {
        this.access_type = access_type;
    }

    /**
     * @return the spatial
     */
    public String getSpatial() {
        return spatial;
    }

    /**
     * @param spatial the spatial to set
     */
    public void setSpatial(String spatial) {
        this.spatial = spatial;
    }

    /**
     * @return the temporal
     */
    public Temporal getTemporal() {
        return temporal;
    }

    /**
     * @param temporal the temporal to set
     */
    public void setTemporal(Temporal temporal) {
        this.temporal = temporal;
    }

    /**
     * @return the parameters
     */
    public Parameters getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }
}
