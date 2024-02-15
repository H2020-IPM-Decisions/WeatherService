/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package net.ipmdecisions.weather.datasourceadapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.WeatherDataUtil;
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
public class OpenMeteoAdapterTest {
    
    public OpenMeteoAdapterTest() {
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
     * Test of getData method, of class OpenMeteoAdapter.
     */
    @Test
    public void testGetData() {
        System.out.println("getData");
        Double longitude =13.41;
        Double latitude = 52.52;
        ZoneId tzForLocation = ZoneId.of("Europe/Berlin");
        Instant timeStart = Instant.now().atZone(tzForLocation).toLocalDate().minusDays(2).atStartOfDay(tzForLocation).toInstant();
        Instant timeEnd = Instant.now().atZone(tzForLocation).toLocalDate().plusDays(2).atStartOfDay(tzForLocation).toInstant();
        //Instant timeStart = LocalDate.of(2024, Month.JANUARY, 24).atStartOfDay(tzForLocation).toInstant();
        //Instant timeEnd = LocalDate.of(2024, Month.JANUARY, 28).atStartOfDay(tzForLocation).toInstant();
         System.out.println("timeStart = " + timeStart + ", timeEnd=" + timeEnd);
        Integer interval = WeatherDataUtil.INTERVAL_HOURLY;
        List<Integer> parameters = List.of(1002,1003,3101,1111,5001);
        OpenMeteoAdapter instance = new OpenMeteoAdapter();
        
        WeatherData result = instance.getData(longitude, latitude, tzForLocation, timeStart, timeEnd, interval, parameters);
        
        assertNotNull(result);
        Integer expectedLength = 120; // 5 days * 24 hours
        assertEquals(expectedLength, result.getLocationWeatherData().get(0).getLength());
        

        boolean output = false;
        
        if(output)
        {
            ObjectMapper objectMapper = new ObjectMapper();
            JavaTimeModule javaTimeModule =  new JavaTimeModule();
            objectMapper.registerModule(javaTimeModule);
            try
            {
                System.out.println(objectMapper.writeValueAsString(result));
            }
            catch(JsonProcessingException ex)
            {
                ex.printStackTrace();
            }
        }
        
        interval = WeatherDataUtil.INTERVAL_DAILY;
        result = instance.getData(longitude, latitude, tzForLocation, timeStart, timeEnd, interval, parameters);
        assertNotNull(result);
        expectedLength = 5; // 5 days
        assertEquals(expectedLength, result.getLocationWeatherData().get(0).getLength());
        
        output = false;
        if(output)
        {
            ObjectMapper objectMapper = new ObjectMapper();
            JavaTimeModule javaTimeModule =  new JavaTimeModule();
            objectMapper.registerModule(javaTimeModule);
            try
            {
                System.out.println(objectMapper.writeValueAsString(result));
            }
            catch(JsonProcessingException ex)
            {
                ex.printStackTrace();
            }
        }
        //assertEquals(expResult, result);

    }
    
}
