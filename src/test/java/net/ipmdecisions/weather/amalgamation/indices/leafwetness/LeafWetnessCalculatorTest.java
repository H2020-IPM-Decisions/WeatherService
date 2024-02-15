/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package net.ipmdecisions.weather.amalgamation.indices.leafwetness;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
    public void testCalculateIndice() {
        System.out.println("calculateIndice");
        WeatherData weatherData = null;
        Integer weatherParameter = null;
        LeafWetnessCalculator instance = new LeafWetnessCalculator();
        WeatherData expResult = null;
        WeatherData result = instance.calculateIndice(weatherData, weatherParameter);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of calculateFromConstantRH method, of class LeafWetnessCalculator.
     */
    //@Test
    public void testCalculateFromConstantRH() {
        System.out.println("calculateFromConstantRH");
        WeatherData weatherData = null;
        LeafWetnessCalculator instance = new LeafWetnessCalculator();
        WeatherData expResult = null;
        WeatherData result = instance.calculateFromConstantRH(weatherData);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    @Test
    public void testCalculateFromLSTM() {
        System.out.println("calculateFromLSTM");
        try
        {
            FileUtils fileUtils = new FileUtils();
            String weatherDataJson = fileUtils.getStringFromFileInApp("/lmt_weatherdata_missing_lwd.json");
            ObjectMapper oMapper = new ObjectMapper();
            WeatherData weatherData = oMapper.readValue(weatherDataJson, WeatherData.class);
            LeafWetnessCalculator instance = new LeafWetnessCalculator();

            WeatherData result = instance.calculateFromLSTM(weatherData);
            assertNotNull(result);
        }
        catch(IOException ex)
        {
            fail(ex.getMessage());
        }
    }
    
}
