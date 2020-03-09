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

package net.ipmdecisions.weather.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import net.ipmdecisions.weather.entity.WeatherData;
import org.jboss.resteasy.annotations.GZIP;

/**
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@Path("rest/schema")
public class MetaDataService {
    private JsonSchemaGenerator schemaGen;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public MetaDataService()
    {
        super();
        // Documentation found here: https://github.com/mbknor/mbknor-jackson-jsonSchema
        
        Map<String,String> customFormatMapping = new HashMap<>(); 
        customFormatMapping.put(Instant.class.getName(), "date-time");
        Map<Class<?>,Class<?>> customClassMapping = new HashMap<>();
        customClassMapping.put(Instant.class, String.class);    
        JsonSchemaConfig config = JsonSchemaConfig.create(
            JsonSchemaConfig.vanillaJsonSchemaDraft4().autoGenerateTitleForProperties(), 
            Optional.empty(), 
            JsonSchemaConfig.vanillaJsonSchemaDraft4().useOneOfForOption(), 
            JsonSchemaConfig.vanillaJsonSchemaDraft4().useOneOfForNullables(), 
            JsonSchemaConfig.vanillaJsonSchemaDraft4().usePropertyOrdering(), 
            JsonSchemaConfig.vanillaJsonSchemaDraft4().hidePolymorphismTypeProperty(), 
            JsonSchemaConfig.vanillaJsonSchemaDraft4().disableWarnings(), 
            JsonSchemaConfig.vanillaJsonSchemaDraft4().useMinLengthForNotNull(), 
            JsonSchemaConfig.vanillaJsonSchemaDraft4().useTypeIdForDefinitionName(), 
            customFormatMapping, 
            JsonSchemaConfig.vanillaJsonSchemaDraft4().useMultipleEditorSelectViaProperty(), 
            new HashSet<>(), 
            customClassMapping, 
            new HashMap<>()
        );
        schemaGen = new JsonSchemaGenerator(objectMapper,config);
    }

    
    @GET
    @Path("weatherdata")
    @GZIP
    @Produces("application/json;charset=UTF-8")
    public Response getWeatherDataSchema()
    {

            JsonNode schema = schemaGen.generateJsonSchema(WeatherData.class);
            return Response.ok().entity(schema).build();
    }
}
