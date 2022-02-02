package net.ipmdecisions.weather.amalgamation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.ipmdecisions.weather.controller.AmalgamationBean;
import net.ipmdecisions.weather.controller.MetaDataBean;
import net.ipmdecisions.weather.controller.WeatherDataSourceBean;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.LocationWeatherDataException;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.FileUtils;

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
}
