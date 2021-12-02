package net.ipmdecisions.weather.qc.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import net.ipmdecisions.weather.entity.LocationWeatherData;

import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.FileUtils;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class QCHelpersTest {
        
    public QCHelpersTest() {
    
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
    public void testSwitchingRowsAndColumnsOfLocationWeatherData() throws Exception{
        
        System.out.println("testSwitchingRowsAndColumnsOfLocationWeatherData");
        
        Double[][] data = {
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0},
            {7.0, 8.0, 9.0},
            {10.0, 11.0, 12.0},
            {13.0, 14.0, 15.0}
        };
        
        Double[][] result = QCHelpers.switchRowsAndColumnsForLocationWeatherData(data, 3);
        
        Double[][] expResult = {
            {1.0, 4.0, 7.0, 10.0, 13.0},
            {2.0, 5.0, 8.0, 11.0, 14.0},
            {3.0, 6.0, 9.0, 12.0, 15.0}
        };

        assertArrayEquals(expResult,result);
    }
    
    @Test
    public void testLogicalWeatherParameterPicking() throws Exception{
        
        System.out.println("testLogicalWeatherParameterPicking");
        
        Integer[] weatherParameters = {
            1002,
            1003,
            1004,
            3002,
            2001,
            4005,
            4002,
            4003,
            null,
            4014,
            3101,
            1112,
            1114,
            5001
        };
        
        Integer[][] result = QCHelpers.getLogicalTuplesFromWeatherParameters(weatherParameters);

        Integer[][] expResult = {{0,1,2}, {11, null, 12}, {6, 7, null}};
        
        assertArrayEquals(expResult,result);
    }
    
    @Test
    public void testWeatherParameterFilteringByQCTest() throws Exception{
        
        System.out.println("testWeatherParameterFilteringByQCTest");
        
        Integer[] weatherParameters = {
            1002,
            1003,
            1004,
            3002,
            2001,
            4005,
            4003,
            4004,
            3101,
            1112,
            5001
        };
        
        Integer[] result = QCHelpers.filterWeatherParametersBasedOnQCType(weatherParameters, QCTestType.LOGICAL);
        
        Integer[] expResult = {
            1002,
            1003,
            1004,
            3002,
            null,
            4005,
            4003,
            4004,
            3101,
            1112,
            null,
        };
        
        assertArrayEquals(expResult, result);
    } 
    
}

