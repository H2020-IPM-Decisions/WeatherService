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
package net.ipmdecisions.weather.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.geojson.GeoJSONFactory;
import org.wololo.geojson.Point;
import org.wololo.jts2geojson.GeoJSONReader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.ipmdecisions.weather.entity.WeatherDataSource;
import net.ipmdecisions.weather.util.GISUtils;

@Stateless
public class WeatherDataSourceBean {
	
	/**
     * Util method to read the catalogue from a YAML file
     * @return
     * @throws IOException 
     */
    public List<WeatherDataSource> getAllWeatherDataSources() throws IOException{
            File dsFile = new File(System.getProperty("net.ipmdecisions.weatherservice.DATASOURCE_LIST_FILE"));
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.registerModule(new JavaTimeModule());
            List<Map> prelim =  (List<Map>) mapper.readValue(dsFile, HashMap.class).get("datasources");
            List<WeatherDataSource> retVal = new ArrayList<>();
            prelim.forEach((m) -> {
                retVal.add(mapper.convertValue(m, new TypeReference<WeatherDataSource>(){}));
            });
            return retVal;
    }

    public WeatherDataSource getWeatherDataSourceById(String id) throws IOException
    {
    	for(WeatherDataSource candidate:this.getAllWeatherDataSources())
    	{
    		if(candidate.getId().equals(id))
    		{
    			return candidate;
    		}
    	}
    	return null;
    }
    
    public List<WeatherDataSource> getWeatherDataSourcesForLocation(Double longitude, Double latitude, Double tolerance) throws IOException
    {
    	tolerance = tolerance == null ? 0.0 : tolerance;
        // Generate GeoJSON for a point and call the general method
        double[] coordinate = new double[2];
        coordinate[0] = longitude;
        coordinate[1] = latitude;
        Coordinate c = new Coordinate(longitude, latitude);
        GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
        org.locationtech.jts.geom.Point point = gf.createPoint(c);
        List<Geometry> listWithPoint = List.of(point);
        return this.getWeatherDataSourcesForLocation(listWithPoint, tolerance);
    }
    
    /**
     * List all weather data sources that cover the provided geometries
     * @param clientGeometries The list of geometry objects to match
     * @param toleranceInput Add some tolerance (in meters) to allow for e.g. a point to match
     * the location of a weather station. The default is 0 meters (no tolerance)
     * @return
     * @throws IOException
     */
    public List<WeatherDataSource> getWeatherDataSourcesForLocation(List<Geometry> clientGeometries, Double toleranceInput) throws IOException
    {
    	final Double tolerance = toleranceInput == null ? 0.0 : toleranceInput; // Have to do this to make it work in the streaming API
    	GeoJSONReader reader = new GeoJSONReader();
    	GISUtils gisUtils = new GISUtils();
        List<WeatherDataSource> retVal = this.getAllWeatherDataSources().stream().filter(dataSource->{
            // Get all geometries in current weather data source
            String dataSourceGeoJsonStr = ""; 
            try
            {
            	dataSourceGeoJsonStr = dataSource.getSpatial().getGeoJSON();

            }
            catch(NullPointerException ex)
            { /* Pass */ }
            
            // We do a brute force search for the string "Sphere" in the geoJSON string
            // to bypass any issues in deserialization of that custom type, which is 
            // short for creating a polygon that covers the entire globe
            if(dataSourceGeoJsonStr != null && dataSourceGeoJsonStr.contains("\"Sphere\""))
            {
                return true;
            }
            
            // Get all geometries for current Weather data source
            // Country boundaries
            List<Feature> dataSourceFeatures = new ArrayList<>();
            try
            {
            	dataSourceFeatures = Arrays.asList(gisUtils.getCountryBoundaries(new HashSet<>(Arrays.asList(
	                    dataSource.getSpatial().getCountries()
	            ))).getFeatures());
            }
            catch(NullPointerException ex)
            { /* Pass */ }
            
            try
            {
            	if(!dataSourceGeoJsonStr.isBlank())
            	{
            		dataSourceFeatures.addAll(Arrays.asList(
            				((FeatureCollection) GeoJSONFactory.create(dataSourceGeoJsonStr)).getFeatures()
            				));
            	}
            }
            catch(RuntimeException ex) {}
            //FeatureCollection dataSourceFeatures = (FeatureCollection) GeoJSONFactory.create(dataSourceGeoJsonStr);
            // Match with all geometries in request. If found, add data source to
            // list of matching data sources
            List<Geometry> dataSourceGeometries = dataSourceFeatures.stream()
            .map(f->{ return reader.read(f.getGeometry()); })
            .filter(dataSourceGeometry->{
                Boolean matching = false;
                for(Geometry clientGeometry: clientGeometries)
                {
                    if(dataSourceGeometry.intersects(clientGeometry) || gisUtils.getDistanceInMetersWGS84(dataSourceGeometry.distance(clientGeometry)) <= tolerance)
                    {
                        //System.out.println("Distance: " + gisUtils.getDistanceInMetersWGS84(dataSourceGeometry.distance(clientGeometry)));
                        matching = true;
                    }
                }
                return matching;
            })
            .collect(Collectors.toList());
            // The number of matching geometries for this weather data source
            // Used as filter criteria
            return dataSourceGeometries.size() > 0; 
        })
        .collect(Collectors.toList());
        
        return retVal;
    }
}
