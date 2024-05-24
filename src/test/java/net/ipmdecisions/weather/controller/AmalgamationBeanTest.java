package net.ipmdecisions.weather.controller;

import net.ipmdecisions.weather.entity.WeatherDataSource;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AmalgamationBeanTest {

    @Test
    public void testFallbackParameters() throws Exception {
        System.out.println("testFallbackParameters");

        Double longitude = 10.782823562622072;
        Double latitude = 59.66266062605126;
        Double tolerance=1000.0;

        Boolean includeFallbackParams = true;

        WeatherDataSourceBean instance = new WeatherDataSourceBean();
        AmalgamationBean amInstance = new AmalgamationBean();

        List<WeatherDataSource> dataSourcesForLocation = instance.getWeatherDataSourcesForLocation(longitude, latitude, tolerance); 
        Set<Integer> retVal = new HashSet<>();
        List<Geometry> clientGeometries = new ArrayList<>();
        double[] coordinate = new double[2];
        coordinate[0] = longitude;
        coordinate[1] = latitude;
        Coordinate c = new Coordinate(longitude, latitude);
        GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
        org.locationtech.jts.geom.Point point = gf.createPoint(c);
        clientGeometries.add(point);
        for(WeatherDataSource ds: dataSourcesForLocation)
        {
            // Get common weather parameters for this datasource
            retVal.addAll(Arrays.stream(ds.getParameters().getCommon()).boxed().collect(Collectors.toList()));
            // Get additional parameters for this location
            List<Integer> additionalParameters = ds.getAdditionalParametersForLocation(clientGeometries, tolerance);
            if(additionalParameters != null)
            {
                retVal.addAll(additionalParameters);
            }
        }
        if(includeFallbackParams)
        {
            Set<Integer> fallbackParams = new HashSet<>();
            for(Integer original:retVal)
            {
                fallbackParams.addAll(amInstance.getInterchangeableParameters(original));
            }
            retVal.addAll(fallbackParams);
        }
        assertTrue(retVal.contains(3103));
        //        retVal.forEach(v->System.out.println(v));
    }

}
