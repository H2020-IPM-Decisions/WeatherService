package net.ipmdecisions.weather.amalgamation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ipmdecisions.weather.controller.AmalgamationBean;
import net.ipmdecisions.weather.controller.MetaDataBean;
import net.ipmdecisions.weather.controller.WeatherDataSourceBean;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.LocationWeatherDataException;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.FileUtils;
import net.ipmdecisions.weather.util.WeatherDataUtil;

public class AmalgamationBeanTest {

	public AmalgamationBeanTest() {
		
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
    public void testAggregate() throws Exception{
    	System.out.println("testAggregate");
    	FileUtils fileUtils = new FileUtils();
    	String weatherDataJson = fileUtils.getStringFromFileInApp("/yr_weatherdata_correct.json");
    	ObjectMapper oMapper = new ObjectMapper();
    	oMapper.registerModule(new JavaTimeModule());
    	WeatherData testData = oMapper.readValue(weatherDataJson, WeatherData.class);
    	/*testData.getLocationWeatherData().forEach(lwd->{
    		Integer index = testData.getParameterIndex(1002);
    		for(int i=0;i<lwd.getData().length;i++)
    		{
    			System.out.println("Temp #" + i + "=" + lwd.getData()[i][index]);
    		}
    	});*/
    	MockAmalgamationBean instance = new MockAmalgamationBean();
    	instance.setMetaDataBean(new MetaDataBean());
    	instance.setWeatherDataSourceBean(new WeatherDataSourceBean());
    	WeatherData result = instance.aggregate(testData, 86400, ZoneId.of("Europe/Oslo"));
    	//System.out.println(oMapper.writeValueAsString(result));
    	assertEquals(Integer.valueOf(9), Integer.valueOf(result.getLocationWeatherData().get(0).getLength()));
    }
    
    private class MockAmalgamationBean extends AmalgamationBean {
    	public void setWeatherDataSourceBean(WeatherDataSourceBean bean)
    	{
    		this.weatherDataSourceBean = bean;
    	}
    	public void setMetaDataBean(MetaDataBean bean)
    	{
    		this.metaDataBean = bean;
    	}    
    }
    
    @Test
    public void testGetFusionedWeatherData() throws Exception{
        System.out.println("testGetFusionedWeatherData");
        
        FileUtils fileUtils = new FileUtils();
        ObjectMapper oMapper = new ObjectMapper();
    	oMapper.registerModule(new JavaTimeModule());
        
        // Testing hourly data
    	String weatherDataJson = fileUtils.getStringFromFileInApp("/fusionTestData_1.json");
    	WeatherData testData_1 = oMapper.readValue(weatherDataJson, WeatherData.class);
        
        weatherDataJson = fileUtils.getStringFromFileInApp("/fusionTestData_2.json");
        WeatherData testData_2 = oMapper.readValue(weatherDataJson, WeatherData.class);
        
        ZoneId tzForLocation = ZoneId.of("Europe/Berlin");
        
        MockAmalgamationBean instance = new MockAmalgamationBean();
    	instance.setMetaDataBean(new MetaDataBean());
    	instance.setWeatherDataSourceBean(new WeatherDataSourceBean());
        WeatherData result = instance.getFusionedWeatherData(List.of(testData_2, testData_1), 
                LocalDate.of(2024, Month.JANUARY, 20).atStartOfDay(tzForLocation).toInstant(), 
                ZonedDateTime.of(LocalDateTime.of(2024, Month.JANUARY, 23,23,0), tzForLocation).toInstant(),
                WeatherDataUtil.INTERVAL_HOURLY, 
                tzForLocation
        );
        
        Integer expectedLength = 96; // 4 days, 24 hours
        
        assertEquals(expectedLength, result.getLocationWeatherData().get(0).getLength());
                
        // Testing daily data
        weatherDataJson = fileUtils.getStringFromFileInApp("/fusionTestData_3.json");
        WeatherData testData_3 = oMapper.readValue(weatherDataJson, WeatherData.class);
        weatherDataJson = fileUtils.getStringFromFileInApp("/fusionTestData_4.json");
        WeatherData testData_4 = oMapper.readValue(weatherDataJson, WeatherData.class);
        result = instance.getFusionedWeatherData(List.of(testData_4, testData_3), 
                LocalDate.of(2024, Month.JANUARY, 20).atStartOfDay(tzForLocation).toInstant(), 
                ZonedDateTime.of(LocalDateTime.of(2024, Month.JANUARY, 23,0,0), tzForLocation).toInstant(),
                WeatherDataUtil.INTERVAL_DAILY, 
                tzForLocation
        );
        
        expectedLength = 4; // 4 days
        assertEquals(expectedLength, result.getLocationWeatherData().get(0).getLength());
        
        // Test with one WeatherData with values and the other without
        WeatherData emptyData = new WeatherData();
        result = instance.getFusionedWeatherData(List.of(testData_4, emptyData), 
                LocalDate.of(2024, Month.JANUARY, 20).atStartOfDay(tzForLocation).toInstant(), 
                ZonedDateTime.of(LocalDateTime.of(2024, Month.JANUARY, 23,0,0), tzForLocation).toInstant(),
                WeatherDataUtil.INTERVAL_DAILY, 
                tzForLocation
        );
        
        
        expectedLength = 4; // 4 days
        assertEquals(expectedLength, result.getLocationWeatherData().get(0).getLength());
        //System.out.println(oMapper.writeValueAsString(result));
    }
}
