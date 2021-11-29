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

package net.ipmdecisions.weather.entity;

import com.webcohesion.enunciate.metadata.DocumentationExample;
import com.webcohesion.enunciate.metadata.rs.TypeHint;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A weather data source is an online service that provides weather data to the
 * platform. It could be anything from a national meteorological service to a 
 * single weather station set up by a farmer
 * 
 * @copyright 2021 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class WeatherDataSource {
    private String id, name, description, public_URL,endpoint, authentication_type, needs_data_control, access_type;
    private Temporal temporal;
    private Parameters parameters; 
    private Spatial spatial;
    private Organization organization;
    
    /** No authentication required for the data source */
    public static String AUTHENTICATION_TYPE_NONE = "NONE";
    
    /** 
     * <p>
     * This implies http POST and sending 
     * parameters x-www-form-urlencoded, adding parameter 
     * <code>credentials={"username":"XXX", "password":"XXX"}</code>
     * </p>
     */
    public static String AUTHENTICATION_TYPE_CREDENTIALS = "CREDENTIALS";
    
    /**
     * A bearer token must be provided in the Authorization header
     */
    public static String AUTHENTICATION_TYPE_BEARER_TOKEN = "BEARER_TOKEN";
    
    private List<String> authenticationTypes = Stream.of(
    		WeatherDataSource.AUTHENTICATION_TYPE_NONE,
    		WeatherDataSource.AUTHENTICATION_TYPE_CREDENTIALS,
    		WeatherDataSource.AUTHENTICATION_TYPE_BEARER_TOKEN
    		).collect(Collectors.toList());
    
    /**
     * A data class for identifying the Organization behind/responsible for the Weather data source
     */
    static class Organization {
        private String name, country, address, postal_code, city, email, url;

        /**
         * @return the name of the Organization. E.g. ADAS, NIBIO
         */
        @DocumentationExample("NIBIO")
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
         * @return the country of the Organization
         */
        @DocumentationExample("Norway")
        public String getCountry() {
            return country;
        }

        /**
         * @param country the country to set
         */
        public void setCountry(String country) {
            this.country = country;
        }

        /**
         * @return the address
         */
        @DocumentationExample("Postboks 115")
        public String getAddress() {
            return address;
        }

        /**
         * @param address the address to set
         */
        public void setAddress(String address) {
            this.address = address;
        }

        /**
         * @return the postal_code
         */
        @DocumentationExample("1431")
        public String getPostal_code() {
            return postal_code;
        }

        /**
         * @param postal_code the postal_code to set
         */
        public void setPostal_code(String postal_code) {
            this.postal_code = postal_code;
        }

        /**
         * @return the city
         */
        @DocumentationExample("Ås")
        public String getCity() {
            return city;
        }

        /**
         * @param city the city to set
         */
        public void setCity(String city) {
            this.city = city;
        }

        /**
         * @return the email. Preferably the email to a person
         * or department responsible for the DSS
         */
        @DocumentationExample("acme@foobar.com")
        public String getEmail() {
            return email;
        }

        /**
         * @param email the email to set
         */
        public void setEmail(String email) {
            this.email = email;
        }

        /**
         * @return the url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @param url the url to set
         */
        public void setUrl(String url) {
            this.url = url;
        }
        
    }
    
    
    /**
     * <p>Spatial is GEOJson defined. 
     * If the resource is a gridded service, the Spatial property is a polygon or a set of polygons
     * The polygons may be specified directly in the GeoJSON property, or it may be a referenced polygon
     * in the countries list. Open data sources exist, such as https://github.com/datasets/geo-countries
     * If the resource is a FeatureCollection of points, this is a weather station network of between at 
     * least 1 and an indefinite number of stations.
     * </p>
     * <p>
     * If you want to describe a spatial for a service that covers the entire globe, you can use this 
     * GeoJson: <code>{"type": "Sphere"}</code>. "Sphere" simply means the whole globe (this is NOT part of the GEOJson spec)
     * 
     * </p>
     */
    public static class Spatial {
        private String[] countries;
        private String geoJSON;

        /**
         * @return Array of country codes that this service is valid for. 
         * https://en.wikipedia.org/wiki/ISO_3166-1#Current_codes
         */
        @DocumentationExample(value="NOR", value2="SWE")
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
        @DocumentationExample(value = "{\n" +
            "\"type\": \"FeatureCollection\",\n" +
            "\"features\": [\n" +
            "  {\"type\": \"Feature\", \"geometry\": {\"type\": \"Point\", \"coordinates\": [8.68956,62.98474,5]}, \"properties\": {\"name\": \"Surnadal\", \"id\":\"46\",\"WMOCertified\": 5}},\n" +
            "  {\"type\": \"Feature\", \"geometry\": {\"type\": \"Point\", \"coordinates\": [5.60533332824707,59.0185012817383]}, \"properties\": {\"name\": \"Rygg\", \"id\":\"98\"}}"
            + "]"
            + "}", value2 = "{\"type\": \"Sphere\"}")
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
             * source does not contain historic/measured data.
             * @jsonExampleOverride "2010-01-01"
             */
            @TypeHint(String.class)
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
             * Example value: <code>2019-01-01</code> or <code>null</code>
             * @jsonExampleOverride null
             */
            @TypeHint(String.class)
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
        @DocumentationExample("0")
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
         * @return Information about the contents of measured data in this service
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
         * These parameters are always available from this service
         * @return These parameters are always available from this service
         */
        @DocumentationExample(value = "1002", value2 = "2001")
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
         * These parameters are available from some of the stations or some of 
         * the locations in this service, but not guaranteed from everywhere
         * @return List of optional parameters
         */
        @DocumentationExample(value = "1132", value2 = "3103")
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

    @DocumentationExample("no.met.locationforecast")
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
     * @return the name
     */
    @DocumentationExample("Agromet Norway")
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
    @DocumentationExample("Weather station network covering major agricultural areas of Norway. Data before 2010 are available by request. Email lmt@nibio.no")
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
    @DocumentationExample("https://lmt.nibio.no/")
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
    @DocumentationExample("https://ipmdecisions.nibio.no/lmtservices/rest/ipmdecisions/getdata/")
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
    @DocumentationExample("true")
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
    @DocumentationExample("stations")
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

    /**
     * @return the organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * @param organization the organization to set
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * <p>
     * The default value is "NONE". 
     * </p>
     * @return the authentication_required
     * @DocumentationExample("NONE")
     */
    public String getAuthentication_type() {
    	return this.authentication_type != null ? this.authentication_type : WeatherDataSource.AUTHENTICATION_TYPE_NONE;
    }
    
    public void setAuthentication_type(String authentication_type)
    {
    	if(!this.authenticationTypes.contains(authentication_type))
    	{
    		throw new IllegalArgumentException("Authentication type must be one of [" + String.join(",", authenticationTypes) + "]");
    	}
    	this.authentication_type = authentication_type;
    }
}
