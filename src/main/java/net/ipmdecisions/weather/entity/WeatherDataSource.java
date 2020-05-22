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

import java.time.LocalDate;

/**
 * A weather data source is an online service that provides weather data to the
 * platform. It could be anything from a national meteorological service to a 
 * single weather station set up by a farmer
 * 
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class WeatherDataSource {
    private String name, description, public_URL,endpoint, needs_data_control, access_type;
    private Temporal temporal;
    private Parameters parameters; 
    private Spatial spatial;
    
    /**
     * Spatial is GEOJson defined. "Sphere" simply means the whole globe (NOT part of GEOJson spec!)
     * If the resource is a gridded service, the Spatial property is a polygon or a set of polygons
     * The polygons may be specified directly in the GeoJSON property, or it may be a referenced polygon
     * in the countries list. Open data sources exist, such as https://github.com/datasets/geo-countries
     * If the resource is a FeatureCollection of points, this is a weather station network of between at 
     * least 1 and an indefinite number of stations
     */
    static class Spatial {
        private String[] countries;
        private String geoJSON;

        /**
         * @return Array of country codes that this service is valid for. 
         * https://en.wikipedia.org/wiki/ISO_3166-1#Current_codes
         */
        public String[] getCountries() {
            return countries;
        }

        /**
         * @param countries the countries to set
         */
        public void setCountries(String[] countries) {
            this.countries = countries;
        }

        /**
         * @return GeoJSON for the areas or locations that the service is valid for
         * If the access type = location, it is a FeatureCollection of points representing
         * weather stations
         */
        public String getGeoJSON() {
            return geoJSON;
        }

        /**
         * @param GeoJSON the GeoJSON to set
         */
        public void setGeoJSON(String GeoJSON) {
            this.geoJSON = GeoJSON;
        }
    }
    
    /**
     * Describes the period for which this data source can provide weather data
     * 
     */
    static class Temporal {
        private int forecast;
        private Historic historic;
        
        /**
         * To what extent does this data source contain historic/measured data (as opposed to
         * forecasts)
         */
        static class Historic {
            
            private LocalDate start, end;

            /**
             * @return The date of the first observations. If null, then this data
             * source does not contain historic/measured data
             */
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
             * @return The date of the last observations. If null (and the start
             * date is NOT null), the data source is continuously updated with new
             * weather data observations.
             */
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
         * @return The number of days ahead this data source provides weather 
         * forecasts. If 0, then this is not a weather forecast service
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
    
    /**
     * A list of weather parameters that this weather data source provides.
     * The parameters are given by their id. For lookup, see: 
     * https://ipmdecisions.nibio.no/weather/rest/parameter/list
     */
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
    public Spatial getSpatial() {
        return spatial;
    }

    /**
     * @param spatial the spatial to set
     */
    public void setSpatial(Spatial spatial) {
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
