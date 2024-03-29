package net.ipmdecisions.weather.qc.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.ipmdecisions.weather.entity.LocationWeatherData;

import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.entity.WeatherParameter;
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
    public void testGettingWeatherParameterTypeAndAggType() throws Exception{
        
        System.out.println("testGettingWeatherParameterTypeAndAggType");
        
        Integer[] weatherParameters = {
            1002,     // mean
            4002,     // avg
            4005,     // min
            null,     // null
            5001,     // sum
            99999999, // null
        };
        
        QCWeatherParameter[] result = new ArrayList<Integer>(Arrays.asList(weatherParameters))
            .stream()
            .map((Integer weatherParameter) -> {
                return QCHelpers.getQCWeatherParameter(weatherParameter);
            }
        ).toArray(QCWeatherParameter[]::new);

        QCWeatherParameter[] expResult = {
            new QCWeatherParameter(100, QCWeatherParameterAggregationType.MEAN),
            new QCWeatherParameter(400, QCWeatherParameterAggregationType.AVERAGE),
            new QCWeatherParameter(400, QCWeatherParameterAggregationType.MINIMUM),
            new QCWeatherParameter(null, null),
            new QCWeatherParameter(500, QCWeatherParameterAggregationType.SUM),
            new QCWeatherParameter(null, null),
        };

        for (int i=0; i<weatherParameters.length; i++) {
            assertEquals(expResult[i].getType(), result[i].getType());
            assertEquals(expResult[i].getAggregationType(), result[i].getAggregationType());
        }
    }

    @Test
    public void testLogicalWeatherParameterPicking() throws Exception{
        
        System.out.println("testLogicalWeatherParameterPicking");
        
        Integer[] weatherParameters = {
            1002, // mean +
            1003, // min  +
            1004, // max  +
            3002, // mean -
            2001, //
            4005, // min  +
            4002, //
            4003, // mean +
            null, //
            4014, // max  +
            3101, //
            1023, // min  +
            1024, // max  +
            4013, // mean +
            5001, // 
        };
        
        Integer[][] result = QCHelpers.getLogicalTuplesFromWeatherParameters(weatherParameters);

        Integer[][] expResult = {
            {   0,    1,    2},
            {null,   11,   12},
            {   7,    5, null},
            {  13, null,    9},
        };
        
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
            5001,
            3022,
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
            3022,
        };
        
        assertArrayEquals(expResult, result);
    } 
    
}

