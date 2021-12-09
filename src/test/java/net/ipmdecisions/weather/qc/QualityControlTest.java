package net.ipmdecisions.weather.qc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.QCType;

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
    
    // ------------------------------------------------
    // COMPLEX OVERALL UNIT TESTS
    // ------------------------------------------------

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
        
        Integer[] expResult = {16,24,16,24,2,16,8,2,8,4,8,2,8}; // need to be changed to correct bitmap values for failure on all tests
        
        assertArrayEquals(expResult,result);
    }
    
    @Test
    public void testNONRTQualityControl() throws Exception{
        
        System.out.println("testNONRTQualityControl");
    	FileUtils fileUtils = new FileUtils();
    	String weatherDataJson = fileUtils.getStringFromFileInApp("/weatherdata_no_errors.json");
        ObjectMapper oMapper = new ObjectMapper();
        WeatherData testData = oMapper.readValue(weatherDataJson, WeatherData.class);
        
        QualityControlMethods instance = new QualityControlMethods();
        WeatherData wd = instance.getQC(testData, "NONRT");
        List<LocationWeatherData> lwd = wd.getLocationWeatherData();
        Integer[] result = lwd.get(0).getQC();
        
        Integer[] expResult = {2,2,2,2,2,2,2,2,2,2,2}; // All tests passed ok
        
        assertEquals(expResult,result);
    }
    
    @Test
    public void testNONRTQualityControlFail() throws Exception{
        
        System.out.println("testNONRTQualityControlFail");
    	FileUtils fileUtils = new FileUtils();
    	String weatherDataJson = fileUtils.getStringFromFileInApp("/weatherdata_nonrt_errors.json");
        ObjectMapper oMapper = new ObjectMapper();
        WeatherData testData = oMapper.readValue(weatherDataJson, WeatherData.class);
        
        QualityControlMethods instance = new QualityControlMethods();
        WeatherData wd = instance.getQC(testData, "NONRT");
        List<LocationWeatherData> lwd = wd.getLocationWeatherData();
        Integer[] result = lwd.get(0).getQC();
        
        Integer[] expResult = {2,32,64,2,2,2,64,2,2,2,2,32,2}; // All tests passed ok
        
        assertEquals(expResult,result);
    }

    // ------------------------------------------------
    // HELPER FUNCTIONS
    // ------------------------------------------------

    /**
     * Helper function for creating a new instance of `WeatherData` with
     * given data and weather parameters inside it.
     * 
     * @param weatherParameters - weather parameters to insert inside `WeatherData`.
     * @param data - data to insert inside `WeatherData`.
     * @return `WeatherData` instance containing given weather parameters and data.
     * @throws Exception 
     */
    private static WeatherData getWeatherDataForTests(Integer[] weatherParameters, Double[][] data) throws Exception {
        Integer[] qc = new Integer[weatherParameters.length];
        Arrays.fill(qc, QCType.NO_QC);
                
        FileUtils fileUtils = new FileUtils();
    	String weatherDataJson = fileUtils.getStringFromFileInApp("/weatherdata_empty.json");
        ObjectMapper oMapper = new ObjectMapper();
        WeatherData testData = oMapper.readValue(weatherDataJson, WeatherData.class);
        
        List<LocationWeatherData> locationWeatherData = testData.getLocationWeatherData();
        locationWeatherData.get(0).setData(data);
        locationWeatherData.get(0).setQC(qc);

        testData.setWeatherParameters(weatherParameters);
        testData.setLocationWeatherData(locationWeatherData);

        return testData;
    }
    
    /**
     * Helper function for getting QC results array (for weather parameters)
     * given WeatherData and a QC type.
     * 
     * @param testData - weather data
     * @param QCType - string: either "RT" or "NONRT".
     * @return QC results for weather parameters.
     * @throws Exception 
     */
    private static Integer[] getQCResultForTests(WeatherData testData, String QCType) throws Exception {
        QualityControlMethods instance = new QualityControlMethods();
        WeatherData wd = instance.getQC(testData, QCType);
        List<LocationWeatherData> lwd = wd.getLocationWeatherData();
        Integer[] result = lwd.get(0).getQC();
        
        return result;
    }
    
    /**
     * Prints the name of the method (that called this method) on a new line.
     * 
     * As it is used inside a unit test, it prints out the name of the unit test.
     */
    private static void printTestName() {
        System.out.println(new Exception().getStackTrace()[1].getMethodName());
    }
    
    // ------------------------------------------------
    // SMALLER FEATURE BASED UNIT TESTS
    // ------------------------------------------------
    
    // ------------------------
    // RT - PREQUALIFICATION QC
    // ------------------------

    @Test
    public void testRTQCPrequalificationSuccess() throws Exception {

        QualityControlTest.printTestName();

        Integer[] weatherParameters = {1001};
        // doubles are valid values
        Double[][] data = {{2.0}, {0.0}};
        
        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "RT");

        Integer[] expResult = {2};

        assertArrayEquals(expResult,result);
    }
    
    @Test
    public void testRTQCPrequalificationNull() throws Exception {

        QualityControlTest.printTestName();

        Integer[] weatherParameters = {1001};
        Double[][] data = {
            {null}, // fail: null is not valid value 
            {0.0},
        };

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "RT");

        Integer[] expResult = {4};

        assertArrayEquals(expResult,result);
    }

    /**
     * Check that strings in data are marked to fail prequalification QC for
     * real time data.
     * 
     * @todo Both the test and the check for strings need to be done differently if done at all.
     * Currently this test is not needed.
     * @throws Exception 
     */
    /*
    @Test
    public void testRTQCPrequalificationString() throws Exception {
        QualityControlTest.printTestName();

        Integer[] weatherParameters = {1001};
        // TODO: both test and checking for strings needs to be done differently.
        Double[][] data = {{"a string is not a double"}, {0.0}};
        
        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "RT");

        Integer[] expResult = {4};

        assertArrayEquals(expResult,result);
    }
    */

    // ------------------------
    // RT - INTERVAL QC
    // ------------------------

    @Test
    public void testRTQCIntervalSuccess() throws Exception {
        /*
        "parameter":"Air temperature",
        "id_array":["1001","1002","1003","1004"],
        "lower_limit":-55,
        "upper_limit":50,
        "step_test_threshold": 7.5,
        "step_test_threshold_type": "absolute"
        */
        
        QualityControlTest.printTestName();

        Integer[] weatherParameters = {1001, 1002};
        // sanity check
        Double[][] data = {{0.0, 10.0}};

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "RT");

        Integer[] expResult = {2, 2};

        assertArrayEquals(expResult,result);
    }

    @Test
    public void testRTQCIntervalLowerLimit() throws Exception {
        /*
        "parameter":"Air temperature",
        "id_array":["1001","1002","1003","1004"],
        "lower_limit":-55,
        "upper_limit":50,
        "step_test_threshold": 7.5,
        "step_test_threshold_type": "absolute"
        */
        
        QualityControlTest.printTestName();

        Integer[] weatherParameters = {1001, 1002};
        // 1001: fail - below (-55.1) is incorrect
        // 1002: success - equal to limit (-55.0) is correct
        Double[][] data = {
            {-55.1, -55.0}, // 1001 fails.
            {0.0, 0.0},
        };

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "RT");

        Integer[] expResult = {8, 2};

        assertArrayEquals(expResult,result);
    }

    @Test
    public void testRTQCIntervalUpperLimit() throws Exception {
        /*
        "parameter":"Air temperature",
        "id_array":["1001","1002","1003","1004"],
        "lower_limit":-55,
        "upper_limit":50,
        "step_test_threshold": 7.5,
        "step_test_threshold_type": "absolute"
        */

        QualityControlTest.printTestName();

        Integer[] weatherParameters = {1001, 1002};
        // 1001: fail - above (50.1) is incorrect
        // 1002: success - equal to limit (50.0) is correct
        Double[][] data = {
            {50.1, 50.0}, // 1001 fails.
            {0.0, 0.0}
        };

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "RT");

        Integer[] expResult = {8, 2};

        assertArrayEquals(expResult,result);
    }

    // ------------------------
    // RT - LOGICAL QC
    // ------------------------

    @Test
    public void testRTQCLogicalSuccess() throws Exception {
        QualityControlTest.printTestName();

        Integer[] weatherParameters = {1003, 1002, 1004};
        Double[][] data = {
            {0.0, 1.0, 2.0}, // success: min <  mean <  max
            {0.0, 0.0, 0.0}, // success: min <= mean <= max
        };

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "RT");

        Integer[] expResult = {2, 2, 2};

        assertArrayEquals(expResult,result);
    }

    @Test
    public void testRTQCLogicalMinMax() throws Exception {
        QualityControlTest.printTestName();

        Integer[] weatherParameters = {1003, 1004};
        Double[][] data = {
            {1.0, 0.0}, // fail:    min >  max
            {0.0, 0.0}, // success: min <= max
        };

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "RT");

        Integer[] expResult = {16, 16};

        assertArrayEquals(expResult,result);
    }

    @Test
    public void testRTQCLogicalMinMean() throws Exception {
        QualityControlTest.printTestName();

        Integer[] weatherParameters = {1003, 1002, 1004};
        Double[][] data = {
            {1.0, 0.0, 2.0}, // fail:    min >  mean
            {0.0, 0.0, 0.0}, // success: min <= mean
        };

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "RT");

        Integer[] expResult = {16, 16, 2};

        assertArrayEquals(expResult,result);
    }

    @Test
    public void testRTQCLogicalMeanMax() throws Exception {
        QualityControlTest.printTestName();

        Integer[] weatherParameters = {1003, 1002, 1004, 3022, 3023, 3024};
        Double[][] data = {
            {0.0, 1.0, 0.0,                     // fail: mean >  max
                            103.0, 79.7, 88.0}, // fail: mean >  max
        };

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "RT");

        Integer[] expResult = {2, 16, 16, 16, 2, 16};

        assertArrayEquals(expResult,result);
    }

    // ------------------------
    // NON RT - STEP QC
    // ------------------------

    @Test
    public void testNonRTQCStepSuccess() throws Exception {
        /*
        "parameter":"Air temperature",
        "id_array":["1001","1002","1003","1004"],
        "lower_limit":-55,
        "upper_limit":50,
        "step_test_threshold": 7.5,
        "step_test_threshold_type": "absolute"
        */

        QualityControlTest.printTestName();

        Integer[] weatherParameters = {1001, 1002, 1003};
        // Sanity check: values increasing, decreasing or staying same do not 
        // break things (when inside step threshold).
        //
        // 1001: increases
        // 1002: decreases
        // 1003: stays same
        Double[][] data = {
            {0.0, 1.0, 0.0}, 
            {1.0, 0.0, 0.0},
        };

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "NONRT");

        Integer[] expResult = {2, 2, 2};

        assertArrayEquals(expResult,result);
    }

    @Test
    public void testNonRTQCStepPrecipitationIsNotTested() throws Exception {
        /*
        "parameter":"Precipitation",
        "id_array":["2001"],
        "lower_limit":-0.5,
        "upper_limit":100
        */
        
        QualityControlTest.printTestName();

        Integer[] weatherParameters = {2001};
        // no step tests are done for precipitation, even if it does maximum jumps
        Double[][] data = {{-0.5}, {100.0}, {-0.5}, {100.0}};

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "NONRT");

        Integer[] expResult = {2};

        assertArrayEquals(expResult,result);
    }

    @Test
    public void testNonRTQCStepLeafWetnessIsNotTested() throws Exception {
        /*
        "parameter":"Leaf wetness",
        "id_array":["3101","3102","3103"],
        "lower_limit":0,
        "upper_limit":60
        */
        
        QualityControlTest.printTestName();

        Integer[] weatherParameters = {3101, 3102, 3103};
        // No step tests are done for leaf wetness, even if they do maximum jumps.
        Double[][] data = {
            { 0.0,  0.0,  0.0}, 
            {60.0, 60.0, 60.0},
            { 0.0,  0.0,  0.0}, 
            {60.0, 60.0, 60.0},
        };

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "NONRT");

        Integer[] expResult = {2, 2, 2};

        assertArrayEquals(expResult,result);
    }
    
    @Test
    public void testNonRTQCStepAbsolute() throws Exception {
        /*
        "parameter":"Air temperature",
        "id_array":["1001","1002","1003","1004"],
        "lower_limit":-55,
        "upper_limit":50,
        "step_test_threshold": 7.5,
        "step_test_threshold_type": "absolute"
        */
        
        QualityControlTest.printTestName();

        Integer[] weatherParameters = {1001, 1002, 1003, 1004};
        // 1001: fail - large positive failing step
        // 1002: fail - large negative failing step
        // 1003: fail - minimal failing step
        // 1004: valid - maximal non-failing step
        Double[][] data = {
            {-55.0,  50.0, 0.0, 0.0}, 
            { 50.0, -55.0, 7.6, 7.5}, // 1001, 1002 and 1003 fail.
            { 50.0, -55.0, 7.6, 7.5},
        };

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "NONRT");

        Integer[] expResult = {32, 32, 32, 2};

        assertArrayEquals(expResult,result);
    }

    
    @Test
    public void testNonRTQCStepRelative() throws Exception {
        /*
        "parameter":"Relative humidty",
        "id_array":["3001","3002","3003","3004","3021","3022","3023","3024"],
        "lower_limit":0,
        "upper_limit":103,
        "step_test_threshold": 30,
        "step_test_threshold_type": "relative"
        */
        
        QualityControlTest.printTestName();

        Integer[] weatherParameters = {3001, 3002, 3021, 3022, 3023};
        // 3001: fail - large positive failing step
        // 3002: fail - large negative failing step
        // 3021: fail - minimal failing step
        // 3022: fail - incorrect relative jump
        // 3023: valid - maximal non-failing step
        Double[][] data = {
            {  0.0, 103.0, 100.0,  0.0, 100.0}, 
            {103.0,   0.0,  70.0, 30.0,  70.1}, // 3001, 3002 and 3021 fail. 
            {103.0,   0.0,  70.0, 60.0,  60.0},
        };

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "NONRT");

        Integer[] expResult = {32, 32, 32, 32, 2};

        assertArrayEquals(expResult,result);
    }

    // ------------------------
    // NON RT - FREEZE QC
    // ------------------------

    @Test
    public void testNonRTQCFreezeSuccess() throws Exception {
        /*
        "parameter":"Relative humidty",
        "id_array":["3001","3002","3003","3004","3021","3022","3023","3024"],
        "lower_limit":0,
        "upper_limit":103,
        "step_test_threshold": 30,
        "step_test_threshold_type": "relative"
        */
        
        QualityControlTest.printTestName();

        Integer[] weatherParameters = {1001, 1002, 1003};
        // Sanity check: values changing doesn't break things.
        // monotonically increasing
        // monotonically decreasing 
        // random jumps
        Double[][] data = {
            {0.0, 7.0, 10.0}, // 1st
            {1.0, 6.0, 11.0}, // 2nd
            {2.0, 5.0, 12.0}, // 3rd
            {3.0, 4.0, 11.0}, // 4th
            {4.0, 3.0, 10.0}, // 5th
            {5.0, 2.0, 11.0}, // 6th
            {6.0, 1.0, 12.0}, // 7th
            {7.0, 0.0, 11.0}, // 8th - no freeze
        };

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "NONRT");

        Integer[] expResult = {2, 2, 2};

        assertArrayEquals(expResult,result);
    }

    @Test
    public void testNonRTQCFreezePrecipitationZeroCannotFail() throws Exception {
        /*
        "parameter":"Precipitation",
        "id_array":["2001"],
        "lower_limit":-0.5,
        "upper_limit":100
        */
        
        QualityControlTest.printTestName();

        Integer[] weatherParameters = {2001};
        // Precipitation value 0.0 is a special case that cannot fail freeze QC.
        Double[][] data = {
            {0.0}, // 1st
            {0.0}, // 2nd
            {0.0}, // 3rd
            {0.0}, // 4th
            {0.0}, // 5th
            {0.0}, // 6th
            {0.0}, // 7th
            {0.0}, // 8th - still valid
        };

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "NONRT");

        Integer[] expResult = {2};

        assertArrayEquals(expResult,result);
    }
    
    @Test
    public void testNonRTQCFreezeLeafWetnessCannotFail() throws Exception {
        /*
        "parameter":"Leaf wetness",
        "id_array":["3101","3102","3103"],
        "lower_limit":0,
        "upper_limit":60
        */
        
        QualityControlTest.printTestName();

        Integer[] weatherParameters = {3101, 3102, 3103};
        // Leaf wetness is a special case that cannot fail freeze QC.
        Double[][] data = {
            {0.0, 10.0, 110.0}, // 1st
            {0.0, 10.0, 110.0}, // 2nd
            {0.0, 10.0, 110.0}, // 3rd
            {0.0, 10.0, 110.0}, // 4th
            {0.0, 10.0, 110.0}, // 5th
            {0.0, 10.0, 110.0}, // 6th
            {0.0, 10.0, 110.0}, // 7th
            {0.0, 10.0, 110.0}, // 8th - still valid
        };

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "NONRT");

        Integer[] expResult = {2,2,2};

        assertArrayEquals(expResult,result);
    }

    @Test
    public void testNonRTQCFreezeFails() throws Exception {
        QualityControlTest.printTestName();

        Integer[] weatherParameters = {1001, 1101, 2001, 3001, 3002};
        // 1001: fail - zeroes (only precipitation should allow 0.0 freezing).
        // 1101: fail - random number
        // 2001: fail - precipitation will still fail on values other than 0.0.
        // 3001: fail - minimal failing freeze (not starting from first cell)
        // 3002: success - maximal non-failing freeze
        Double[][] data = {
            {0.0, 60.0, 60.0,  99.0,  99.0}, // 1st - 1001, 1101 and 2001 start freeze.
            {0.0, 60.0, 60.0,  99.0,  99.0}, // 2nd
            {0.0, 60.0, 60.0, 100.0, 100.0}, // 3rd - 3001 and 3002 start freeze.
            {0.0, 60.0, 60.0, 100.0, 100.0}, // 4th
            {0.0, 60.0, 60.0, 100.0, 100.0}, // 5th
            {0.0, 60.0, 60.0, 100.0, 100.0}, // 6th - 1001, 1101 and 2001 fail.
            {0.1, 60.0, 60.0, 100.0, 100.0}, // 7th - 3002 ends: does not fail.
            {0.1, 60.0, 60.0, 100.0,  99.0}, // 8th - 3001 ends: fails.
        };

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "NONRT");

        Integer[] expResult = {64, 64, 64, 64, 2};

        assertArrayEquals(expResult,result);
    }

    /*
    @Test
    public void testNonRTQCFreezeFailsWithTwoHourDataInterval() throws Exception {
        QualityControlTest.printTestName();

        Integer[] weatherParameters = {1001, 1101, 3001, 3002};
        // 1001: fail - all zeroes (only precipitation should allow 0.0 freezing).
        // 1101: fail - all random number
        // 3001: fail - minimal failing freeze
        // 4001: success - maximal non-failing freeze
        Double[][] data = {
            {0.0, 60.0, 100.0, 100.0}, // 1st
            {0.0, 60.0, 100.0, 100.0}, // 2nd - 3002 ends (does not fail) (4h freeze)
            {0.0, 60.0, 100.0,  99.0}, // 3rd - 1001, 1101 and 3001 fail  (6h freeze)
            {0.0, 60.0,  99.0,  99.0}, // 4th
        };

        Integer twoHoursInSeconds = 7200;

        WeatherData testData = QualityControlTest.getWeatherDataForTests(weatherParameters, data);
        testData.setInterval(twoHoursInSeconds);
        Integer[] result = QualityControlTest.getQCResultForTests(testData, "NONRT");

        Integer[] expResult = {64, 64, 64, 2};

        assertArrayEquals(expResult,result);
    }
    */
}
