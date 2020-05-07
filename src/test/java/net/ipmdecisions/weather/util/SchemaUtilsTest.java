/*
 * Copyright (c) 2020 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of IPMDecisionsWeatherService.
 * IPMDecisionsWeatherService is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * IPMDecisionsWeatherService is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with IPMDecisionsWeatherService.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package net.ipmdecisions.weather.util;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.BufferedInputStream;
import java.net.URL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class SchemaUtilsTest {
    
    public SchemaUtilsTest() {
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
     * Test of isJsonValid method, of class SchemaUtils.
     */
    //@Test
    /*
    public void testIsJsonValid_URL_JsonNode() throws Exception {
        System.out.println("isJsonValid");
        URL schemaURL = new URL("https://ipmdecisions.nibio.no/WeatherService/rest/schema/weatherdata");
        
        // Try first with assumed correct data
        BufferedInputStream inputStream = new BufferedInputStream(this.getClass().getResourceAsStream("/yr_weatherdata_correct.json"));
        SchemaUtils instance = new SchemaUtils();
        JsonNode jsonNode = instance.getJsonFromInputStream(inputStream);
        Boolean expResult = true;
        Boolean result = instance.isJsonValid(schemaURL, jsonNode);
        assertEquals(expResult, result);
        
        // Then with non conforming data
        inputStream = new BufferedInputStream(this.getClass().getResourceAsStream("/yr_weatherdata_wrong.json"));
        jsonNode = instance.getJsonFromInputStream(inputStream);
        expResult = false;
        result = instance.isJsonValid(schemaURL, jsonNode);
        assertEquals(expResult, result);
        
    }*/

}
