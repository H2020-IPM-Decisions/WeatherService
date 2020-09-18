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
package net.ipmdecisions.weather.datasourceadapters.finnishmeteorologicalinstitute;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.FileUtils;
import net.ipmdecisions.weather.util.vips.VIPSWeatherObservation;
import net.ipmdecisions.weather.util.vips.WeatherUtils;
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
public class FinnishMeteorologicalInstituteAdapterTest {
    private final WeatherUtils weatherUtils;
    public FinnishMeteorologicalInstituteAdapterTest() {
        this.weatherUtils = new WeatherUtils();
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
     * Test of getWeatherDataFromVIPSWeatherObservations method, of class FinnishMeteorologicalInstituteAdapter.
     */
    @Test
    public void testGetWeatherDataFromVIPSWeatherObservations() {
        System.out.println("getWeatherDataFromVIPSWeatherObservations");
        String testXML = new FileUtils().getStringFromFileInApp("/fmi_forecast_sample.xml");
        FmiOpenDataForecastParser fdp = new FmiOpenDataForecastParser();
        List<VIPSWeatherObservation> observations = fdp.getVIPSWeatherObservations(testXML);
        Double longitude = 12.1;
        Double latitude = 61.8;
        FinnishMeteorologicalInstituteAdapter instance = new FinnishMeteorologicalInstituteAdapter();
        WeatherData result = this.weatherUtils.getWeatherDataFromVIPSWeatherObservations(observations, longitude, latitude,1);
        assertNotNull(result);
        
    }

    // Also testing other classes in same package
    @Test
    public void testFmiOpenDataForecastParser_Forecasts(){
        System.out.println("FmiOpenDataForecastParser_Forecasts");
        String testXML = new FileUtils().getStringFromFileInApp("/fmi_forecast_sample.xml");
        FmiOpenDataForecastParser instance = new FmiOpenDataForecastParser();
        List<VIPSWeatherObservation> result = instance.getVIPSWeatherObservations(testXML);
        int expResult = 258;
        assertEquals(expResult,result.size());
    }
    
    @Test
    public void testFmiOpenDataRadiationParser(){
        System.out.println("FmiOpenDataRadiationParser");
        String testXML = new FileUtils().getStringFromFileInApp("/fmi_radiation_sample.xml");
        FmiOpenDataRadiationParser instance = new FmiOpenDataRadiationParser();
        List<VIPSWeatherObservation> result = instance.getVIPSWeatherObservations(testXML);
        int expResult = 96;
        assertEquals(expResult,result.size());
        
    }
    
    @Test
    public void testFmiOpenDataParser(){
        System.out.println("FmiOpenDataParser");
        String testXML = new FileUtils().getStringFromFileInApp("/fmi_observation_sample.xml");
        testXML = testXML.replaceAll("\\n", "");
        FmiOpenDataParser instance = new FmiOpenDataParser();
        String testJson = instance.getAsJSON_prototype(testXML, "00");
        ObjectMapper mapper = new ObjectMapper();
        try{
            List<VIPSWeatherObservation> result = mapper.readValue(testJson, new TypeReference<List<VIPSWeatherObservation>>(){});
            int expResult = 580;
            assertEquals(expResult,result.size());
        }catch(JsonProcessingException ex)
        {
            fail(ex.getMessage());
        }
        
    }
}
