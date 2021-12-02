package net.ipmdecisions.weather.qc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import net.ipmdecisions.weather.entity.LocationWeatherData;

//import static org.junit.jupiter.api.Assertions.*;

//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Set;

//import net.ipmdecisions.weather.entity.LocationWeatherData;
//import net.ipmdecisions.weather.entity.LocationWeatherDataException;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.FileUtils;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class QualityControlTest {
    
    public QualityControlTest() {
    
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
    
    @Test
    public void testRTQualityControl() throws Exception{
        
        System.out.println("testRTQualityControl");
    	FileUtils fileUtils = new FileUtils();
    	String weatherDataJson = fileUtils.getStringFromFileInApp("/weatherdata_no_errors.json");
        ObjectMapper oMapper = new ObjectMapper();
        WeatherData testData = oMapper.readValue(weatherDataJson, WeatherData.class);
        
        QualityControlMethods instance = new QualityControlMethods();
        WeatherData wd = instance.getQC(testData, "RT");
        List<LocationWeatherData> lwd = wd.getLocationWeatherData();
        Integer[] result = lwd.get(0).getQC();
        
        Integer[] expResult = {2,2,2,2,2,2,2,2,2,2,2}; // All tests passed ok
        
        assertArrayEquals(expResult,result);
    }
    
    
    @Test
    public void testRTQualityControlFail() throws Exception{
        
        System.out.println("testRTQualityControlFail");
    	FileUtils fileUtils = new FileUtils();
    	String weatherDataJson = fileUtils.getStringFromFileInApp("/weatherdata_rt_errors.json");
        ObjectMapper oMapper = new ObjectMapper();
        WeatherData testData = oMapper.readValue(weatherDataJson, WeatherData.class);
        
        QualityControlMethods instance = new QualityControlMethods();
        WeatherData wd = instance.getQC(testData, "RT");
        List<LocationWeatherData> lwd = wd.getLocationWeatherData();
        Integer[] result = lwd.get(0).getQC();
        
        Integer[] expResult = {2,8,16,24,2,2,8,2,8,4,8,2,8}; // need to be changed to correct bitmap values for failure on all tests
        
        assertEquals(expResult,result);
    }
    
    @Test
    public void testNONRTQualityControl() throws Exception{
        
        System.out.println("testNONRTQualityControl");
    	FileUtils fileUtils = new FileUtils();
    	String weatherDataJson = fileUtils.getStringFromFileInApp("/weatherdata_no_errors.json");
        ObjectMapper oMapper = new ObjectMapper();
        WeatherData testData = oMapper.readValue(weatherDataJson, WeatherData.class);
        
        QualityControlMethods instance = new QualityControlMethods();
        WeatherData result = instance.getQC(testData, "NONRT");
        
        Integer[] expResult = {2,2,2,2,2,2,2,2,2,2,2}; // All tests passed ok
        
        assertNotEquals(expResult,result);
    }
    
    @Test
    public void testNONRTQualityControlFail() throws Exception{
        
        System.out.println("testNONRTQualityControlFail");
    	FileUtils fileUtils = new FileUtils();
    	String weatherDataJson = fileUtils.getStringFromFileInApp("/weatherdata_nonrt_errors.json");
        ObjectMapper oMapper = new ObjectMapper();
        WeatherData testData = oMapper.readValue(weatherDataJson, WeatherData.class);
        
        QualityControlMethods instance = new QualityControlMethods();
        WeatherData result = instance.getQC(testData, "NONRT");
        
        Integer[] expResult = {2,32,64,2,2,2,64,2,2,2,2,32,2}; // All tests passed ok
        
        assertNotEquals(expResult,result);
    }
    
    
}

