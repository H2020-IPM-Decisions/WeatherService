package net.ipmdecisions.weather.amalgamation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.LocationWeatherDataException;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.FileUtils;

public class InterpolationTest {

	public InterpolationTest() {
		
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
    public void testInterpolate() throws Exception{
    	System.out.println("testInterpolate");
    	FileUtils fileUtils = new FileUtils();
    	String weatherDataJson = fileUtils.getStringFromFileInApp("/lmt_amalgamation_testdata.json");
    	ObjectMapper oMapper = new ObjectMapper();
    	WeatherData testData = oMapper.readValue(weatherDataJson, WeatherData.class);
    	/*testData.getLocationWeatherData().forEach(lwd->{
    		Integer index = testData.getParameterIndex(1002);
    		for(int i=0;i<lwd.getData().length;i++)
    		{
    			System.out.println("Temp #" + i + "=" + lwd.getData()[i][index]);
    		}
    	});*/
    	Interpolation instance = new Interpolation();
    	Set<Integer> paramsToInterpolate = new HashSet<>(Arrays.asList(1002));
    	WeatherData result = instance.interpolate(testData, paramsToInterpolate, 4);
    	// Can't return null
    	assertNotNull(result);
    	assertNotEquals(result.getLocationWeatherData().size(), 0);
    	// Check that the temperature arrays have no nulls
    	
    	result.getLocationWeatherData().forEach(lwd->{
    		try
        	{
	    		Double[] temps = lwd.getColumn(result.getParameterIndex(1002));
	    		if(temps.length == 0)
	    		{
	    			fail("Where did all the temps go?? Array is empty");
	    		}
	    		for(int i=0; i < temps.length; i++)
	    		{
	    			
	    			if(temps[i] == null)
	    			{
	    				System.out.println(i + "=" + temps[i]);
	    				fail("Found a null temp value");
	    			}
	    		}
        	} 
    		catch(LocationWeatherDataException ex)
        	{
        		fail(ex.getMessage());
        	}
    	});
    	
    	// Check for QC updates???
    	
    	// The result data set should contain empties when not tolerating the amount of contiguously  missing values
    	// The test data set has at least one hole with at least four missing values
    	// Have to reread the data from json
    	testData = oMapper.readValue(weatherDataJson, WeatherData.class);
    	WeatherData result2 = instance.interpolate(testData, paramsToInterpolate, 2);
    	Integer numberOfNullValuesFound = result2.getLocationWeatherData().stream().mapToInt(lwd->{
	    		try
	        	{
	    			Double[] temps = lwd.getColumn(result2.getParameterIndex(1002));
		    		if(temps.length == 0)
		    		{
		    			fail("Where did all the temps go?? Array is empty");
		    		}

		    		int numberOfNullsFound = 0;
		    		for(int i=0; i < temps.length; i++)
		    		{
		    			//System.out.println(temps[i]);
		    			if(temps[i] == null)
		    			{
		    				//System.out.println("Found a null, good, good!");
		    				numberOfNullsFound++;
		    			}
		    		}
		    		return numberOfNullsFound;
	        	}
	    		catch(LocationWeatherDataException ex)
	        	{
	        		fail(ex.getMessage());
	        	}
				return 0;
	    	}
    	).sum();
    	assertNotEquals(0, numberOfNullValuesFound);
    }
}
