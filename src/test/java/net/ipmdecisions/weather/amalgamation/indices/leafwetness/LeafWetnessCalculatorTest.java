/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package net.ipmdecisions.weather.amalgamation.indices.leafwetness;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.LocationWeatherDataException;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.FileUtils;
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
public class LeafWetnessCalculatorTest {
    
    public LeafWetnessCalculatorTest() {
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
     * Test of calculateIndice method, of class LeafWetnessCalculator.
     */
    //@Test
    public void testCalculateIndice() throws JsonProcessingException, LocationWeatherDataException {
        System.out.println("calculateIndice");
        try {
            FileUtils fileUtils = new FileUtils();
            String weatherDataJson = fileUtils.getStringFromFileInApp("/lmt_weatherdata_missing_lwd.json");
            ObjectMapper oMapper = new ObjectMapper();
            WeatherData weatherData = oMapper.readValue(weatherDataJson, WeatherData.class);
            Integer weatherParameter = null;
            LeafWetnessCalculator instance = new LeafWetnessCalculator();
            WeatherData result = instance.calculateIndice(weatherData,weatherParameter);
            
            
            for (LocationWeatherData lwd : result.getLocationWeatherData()) {
                Double[] bt = lwd.getColumn(result.getParameterIndex(3101));
                testLWD(bt);
            }
          
            assertNotNull(result);
        }
        catch(IOException ex)
        {
            fail(ex.getMessage());
        }
    }

    /**
     * Test of calculateFromConstantRH method, of class LeafWetnessCalculator.
     */
    //@Test
    public void testCalculateFromConstantRH() throws JsonProcessingException, LocationWeatherDataException {
        System.out.println("calculateFromConstantRH");
        FileUtils fileUtils = new FileUtils();
        String weatherDataJson = fileUtils.getStringFromFileInApp("/lmt_weatherdata_missing_lwd.json");
        ObjectMapper oMapper = new ObjectMapper();
        WeatherData weatherData = oMapper.readValue(weatherDataJson, WeatherData.class);
        LeafWetnessCalculator instance = new LeafWetnessCalculator();
        
        WeatherData result = instance.calculateFromConstantRH(weatherData);
        
        assertNotNull(result);
        
        for(LocationWeatherData lwd : result.getLocationWeatherData())
    	{
    		Double[] bt = lwd.getColumn(result.getParameterIndex(3101));
                testLWD(bt);
                
    	}
    }
    
    @Test
    public void testCalculateFromLSTM() throws LocationWeatherDataException {
        System.out.println("calculateFromLSTM");
        try
        {
            FileUtils fileUtils = new FileUtils();
            String weatherDataJson = fileUtils.getStringFromFileInApp("/lmt_weatherdata_missing_lwd.json");
            ObjectMapper oMapper = new ObjectMapper();
            new ObjectMapper().configure(
                           SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            JavaTimeModule javaTimeModule =  new JavaTimeModule();
            oMapper.registerModule(javaTimeModule);
            oMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"));
            WeatherData weatherData = oMapper.readValue(weatherDataJson, WeatherData.class);
            LeafWetnessCalculator instance = new LeafWetnessCalculator();
            
            //System.out.println(oMapper.writeValueAsString(weatherData));

            try {
                // Can this be mocked or fixed in CI/CD?
                System.setProperty("net.ipmdecisions.weatherservice.LWD_LSTM_HOSTNAME", "http://localhost:5000");
                WeatherData result = instance.calculateFromLSTM(weatherData);
                for (LocationWeatherData lwd : result.getLocationWeatherData()) {
                    Double[] bt = lwd.getColumn(result.getParameterIndex(3101));
                    testLWD(bt);
            }
                
            } catch (IOException ex) {
                System.out.println("LSTM docker not reachable, LSTM calculation not tested. Reason: " + ex.getMessage()); 
            }    
        }
        catch(IOException ex)
        {
            fail(ex.getMessage());
        }
    }

    private void testLWD(Double[] bt) {
        
        if(bt.length == 0)
        {
            fail("No leaf wetness calculated");
        }
        for(int i=0; i < bt.length; i++)
        {
            if(bt[i] == null)
            {
                System.out.println(i + "=" + bt[i]);
                fail("Found a null leaf wetness value");
            }
        }
       
    }
    
}
