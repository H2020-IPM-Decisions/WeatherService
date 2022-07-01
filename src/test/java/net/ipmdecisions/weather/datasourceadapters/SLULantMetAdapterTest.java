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
package net.ipmdecisions.weather.datasourceadapters;

import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.entity.WeatherDataSourceException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static org.junit.jupiter.api.Assertions.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 *
 * @author treinar
 */
public class SLULantMetAdapterTest {
    
    public SLULantMetAdapterTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getWeatherForecasts method, of class SLULantMetAdapter.
     * @throws WeatherDataSourceException 
     */
    @Test
    public void testGetWeatherForecasts() throws Exception, WeatherDataSourceException {
        
        System.out.println("getWeatherForecasts");
        
        TimeZone tz = TimeZone.getTimeZone("GMT+1");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(tz);
        Double longitude = 13.039;
        Double latitude = 55.752;
        Date dateFrom = format.parse("2021-08-01");
        Date dateTo = format.parse("2021-08-03");
        Integer interval = 3600;
        Integer[] params = {1002,2001};
        List<Integer> parameters = Arrays.asList(params); 
        SLULantMetAdapter instance = new SLULantMetAdapter();
        WeatherData result = instance.getData(longitude, latitude, dateFrom.toInstant(), dateTo.toInstant(), interval, parameters);
        assertNotNull(result);
        
        // Optional print
        /*
        ObjectMapper om = new ObjectMapper();
        JavaTimeModule javaTimeModule =  new JavaTimeModule();
        om.registerModule(javaTimeModule);
        System.out.println(om.writeValueAsString(result));
        */
    }
    
}
