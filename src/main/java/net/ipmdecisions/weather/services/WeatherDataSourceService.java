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

package net.ipmdecisions.weather.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.webcohesion.enunciate.metadata.rs.TypeHint;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.ipmdecisions.weather.controller.WeatherDataSourceBean;
import net.ipmdecisions.weather.entity.WeatherDataSource;
import net.ipmdecisions.weather.util.GISUtils;
import org.locationtech.jts.geom.Geometry;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.geojson.GeoJSONFactory;
import org.wololo.geojson.Point;
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;

/**
 * This service provides information about the platform's weather data sources,
 * its weather data format and the weather parameters in use.
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@Path("rest")
public class WeatherDataSourceService {
	
	@EJB
	WeatherDataSourceBean weatherDataSourceBean;

    /**
     * Get a list of all the available weather data sources
     * @return A list of all the available weather data sources
     */
    @GET
    @Path("weatherdatasource")
    @Produces(MediaType.APPLICATION_JSON)
    @TypeHint(WeatherDataSource[].class)
    public Response listWeatherDataSources(){
        try
        {
            return Response.ok().entity(weatherDataSourceBean.getAllWeatherDataSources()).build();
        }
        catch(IOException ex)
        {
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
    
    /**
     * Search for weather data sources that serve the specific location. The location
     * can by any valid Geometry, such as Point or Polygon. 
     * Example GeoJson input
     * <pre>
     * {
        "type": "FeatureCollection",
        "features": [
          {
            "type": "Feature",
            "properties": {},
            "geometry": {
              "type": "Point",
              "coordinates": [
                12.01629638671875,
                59.678835236960765
              ]
            }
          }
        ]
      }
     * 
     * </pre>
     * @param tolerance Add some tolerance (in meters) to allow for e.g. a point to match
     * the location of a weather station. The default is 0 meters (no tolerance)
     * @param geoJson valid GeoJSON https://geojson.org/
     * @return A list of all the matching weather data sources
     */
    @POST
    @Path("weatherdatasource/location")
    @Consumes("application/json")
    @Produces("application/json")
    @TypeHint(WeatherDataSource[].class)
    public Response listWeatherDataSourcesForLocation(@QueryParam("tolerance") Double tolerance, String geoJson)
    {
        Double toleranceFinal = tolerance == null ? 0.0 : tolerance;
        try
        {
	        FeatureCollection clientFeatures = (FeatureCollection) GeoJSONFactory.create(geoJson);
	        GeoJSONReader reader = new GeoJSONReader();
	        // Get all geometries in request
	        List<Geometry> clientGeometries = new ArrayList<>();
	        for(Feature feature: clientFeatures.getFeatures())
	        {
	            Geometry geom = reader.read(feature.getGeometry());
	            clientGeometries.add(geom);
	        }
	        // Loop through all weather data sources
	        // Return only data sources with geometries intersecting with the client's
	        // specified geometries
	        List<WeatherDataSource> retVal = weatherDataSourceBean.getWeatherDataSourcesForLocation(clientGeometries, toleranceFinal); 
	        		
	        return Response.ok().entity(retVal).build();
        }catch(IOException ex)
        {
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
    
    /**
     * Search for weather data sources that serve the specific point. 
     * @param tolerance Add some tolerance (in meters) to allow for e.g. a point to match
     * the location of a weather station. The default is 0 meters (no tolerance)
     * @param latitude in decimal degrees (WGS84)
     * @param longitude in decimal degrees (WGS84)
     * @return A list of all the matching weather data sources
     * 
     * @pathExample /rest/weatherdatasource/location/point/?latitude=59.678835236960765&longitude=12.01629638671875
     */
    @GET
    @Path("weatherdatasource/location/point")
    @Produces("application/json")
    @TypeHint(WeatherDataSource[].class)
    public Response listWeatherDataSourcesForPoint(
            @QueryParam("latitude") Double latitude, 
            @QueryParam("longitude") Double longitude,
            @QueryParam("tolerance") Double tolerance
            )
    {
    	try
    	{
	        tolerance = tolerance == null ? 0.0 : tolerance;
	        return Response.ok().entity(weatherDataSourceBean.getWeatherDataSourcesForLocation(longitude, latitude, tolerance)).build();
    	}
    	catch(IOException ex)
    	{
    		return Response.serverError().entity(ex.getMessage()).build();
    	}
    }
    
    @GET
    @Path("weatherdatasource/{id}")
    @Produces("application/json")
    @TypeHint(WeatherDataSource[].class)
    public Response getWeatherDataSourceById(
    		@PathParam("id") String id
    		)
    {
    	try
    	{
    		WeatherDataSource wds = weatherDataSourceBean.getWeatherDataSourceById(id);
	    	return wds != null ? Response.ok().entity(wds).build() 
	    			: Response.status(Status.NOT_FOUND).build();
    	}
    	catch(IOException ex)
    	{
    		return Response.serverError().entity(ex.getMessage()).build();
    	}
    }
    
    
}
