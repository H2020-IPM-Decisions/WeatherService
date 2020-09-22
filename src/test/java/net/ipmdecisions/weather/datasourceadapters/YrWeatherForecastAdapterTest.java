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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author treinar
 */
public class YrWeatherForecastAdapterTest {
    
    public YrWeatherForecastAdapterTest() {
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
     * Test of getWeatherForecasts method, of class YrWeatherForecastAdapter.
     */
    //@Test
    public void testGetWeatherForecasts() throws Exception {
        System.out.println("getWeatherForecasts");
        Double longitude = 14.3711;
        Double latitude = 67.2828;
        Double altitude = 70.0;
        YrWeatherForecastAdapter instance = new YrWeatherForecastAdapter();
        WeatherData result = instance.getWeatherForecasts(longitude, latitude, altitude);
        assertNotNull(result);
        //System.out.println(result.toString());
    }
    
}
