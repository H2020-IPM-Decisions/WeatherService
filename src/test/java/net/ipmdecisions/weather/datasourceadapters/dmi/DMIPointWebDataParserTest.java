/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ipmdecisions.weather.datasourceadapters.dmi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.xml.datatype.DatatypeConfigurationException;
import net.ipmdecisions.weather.datasourceadapters.dmi.generated.WeatherInterval;
import net.ipmdecisions.weather.entity.WeatherData;
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
public class DMIPointWebDataParserTest {
    
    public DMIPointWebDataParserTest() {
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
     * Test of getData method, of class DMIPointWebDataParser.
     */
    @Test
    public void testGetData() {
        System.out.println("getData");
        Double longitude = 9.583;
        Double latitude = 56.488;
        
        Date dateFrom = Date.from(ZonedDateTime.parse("2021-10-06T00:00:00+02:00").toInstant());
        Date dateTo = Date.from(ZonedDateTime.parse("2021-10-16T00:00:00+02:00").toInstant());
        DMIPointWebDataParser instance = new DMIPointWebDataParser();
        try
        {
            WeatherData result = instance.getData(longitude, latitude, dateFrom, dateTo, 3600);
            assertNotNull(result);
            /*
            ObjectMapper om = new ObjectMapper();
            JavaTimeModule javaTimeModule =  new JavaTimeModule();
            om.registerModule(javaTimeModule);
            om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'TEST'HH:mm:ssXXX"));
            try
            {
                System.out.println(om.writeValueAsString(result));
            }
            catch(JsonProcessingException ex)
            {
                fail(ex.getMessage());
            }
            */
        }
        catch(DatatypeConfigurationException ex)
        {
            fail(ex.getMessage());
        }
        
    }
    
}
