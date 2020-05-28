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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.ipmdecisions.weather.entity.WeatherDataSource;
import net.ipmdecisions.weather.util.GISUtils;
import org.locationtech.jts.geom.Coordinate;
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
            return Response.ok().entity(this.getAllWeatherDataSources()).build();
        }
        catch(IOException ex)
        {
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
    
    /**
     * Search for weather data sources that serve the specific location. The location
     * can by any valid Geometry, such as Point or Polygon. 
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
        GISUtils gisUtils = new GISUtils();
        List<WeatherDataSource> retVal = this.getAllWeatherDataSources().stream().filter(dataSource->{
            // Get all geometries in current weather data source
            String dataSourceGeoJsonStr = dataSource.getSpatial().getGeoJSON();
            // We do a brute force search for the string "Sphere" in the geoJSON string
            // to bypass any issues in deserialization of that custom type, which is 
            // short for creating a polygon that covers the entire globe
            if(dataSourceGeoJsonStr.contains("\"Sphere\""))
            {
                return true;
            }
            FeatureCollection dataSourceFeatures = (FeatureCollection) GeoJSONFactory.create(dataSourceGeoJsonStr);
            // Match with all geometries in request. If found, add data source to
            // list of matching data sources
            List<Geometry> dataSourceGeometries = Arrays.asList(dataSourceFeatures.getFeatures()).stream()
            .map(f->{ return reader.read(f.getGeometry()); })
            .filter(dataSourceGeometry->{
                Boolean matching = false;
                for(Geometry clientGeometry: clientGeometries)
                {
                    if(dataSourceGeometry.intersects(clientGeometry) || gisUtils.getDistanceInMetersWGS84(dataSourceGeometry.distance(clientGeometry)) <= toleranceFinal)
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
        tolerance = tolerance == null ? 0.0 : tolerance;
        // Generate GeoJSON for a point and call the general method
        double[] coordinate = new double[2];
        coordinate[0] = longitude;
        coordinate[1] = latitude;
        Point point = new Point(coordinate);
        List<Feature> features = new ArrayList<>();
        Map<String, Object> properties = new HashMap<>();
        features.add(new Feature(point,properties));
        GeoJSONWriter writer = new GeoJSONWriter();
        return this.listWeatherDataSourcesForLocation(tolerance, writer.write(features).toString());
    }
    
    /**
     * Util method to read the catalogue from a YAML file
     * @return
     * @throws IOException 
     */
    private List<WeatherDataSource> getAllWeatherDataSources() throws IOException{
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
}
