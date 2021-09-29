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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import com.webcohesion.enunciate.metadata.rs.TypeHint;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.ipmdecisions.weather.entity.QCType;
import net.ipmdecisions.weather.entity.WeatherParameter;
import net.ipmdecisions.weather.util.SchemaProvider;
import net.ipmdecisions.weather.util.SchemaUtils;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.spi.HttpRequest;

/**
 * This service provides information (a <a href="https://json-schema.org/" target="new">Json schema</a>) about the data structure of weather data in 
 * the IPM Decisions platform. It also provides a validation service for weather data,
 * ensuring that the data is in compliance with the schema.
 * 
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@Path("rest")
public class MetaDataService {
    @Context
    private HttpRequest httpRequest;
    @Context
    private HttpServletRequest httpServletRequest;
    
    private final JsonSchemaGenerator schemaGen;
    
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
            JsonSchemaConfig.nullableJsonSchemaDraft4().useOneOfForOption(), 
            JsonSchemaConfig.nullableJsonSchemaDraft4().useOneOfForNullables(), 
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

    
    /**
     * Get the schema that describes the IPM Decision platform's format for 
     * exchange of weather data
     * @return The weather data <a href="https://json-schema.org/" target="new">Json schema</a>
     */
    @GET
    @Path("schema/weatherdata")
    @GZIP
    @Produces("application/json;charset=UTF-8")
    public Response getWeatherDataSchema()
    {
        try
        {
            return Response.ok().entity(SchemaProvider.getWeatherDataSchema()).build();
        }
        catch(IOException ex) {
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
    
    
    
    /**
     * Validates the posted weather data against the <a href="https://json-schema.org/" target="new">Json schema</a>
     * @param weatherData The weather data to validate
     * @return @return <code>{"isValid":"true"}</code> if the data is valid, <code>{"isValid":"false"}</code> otherwise
     * @responseExample application/json {"isValid":"true"}
     */
    @POST
    @Path("schema/weatherdata/validate")
    @GZIP
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateWeatherData(JsonNode weatherData)
    {
        try
        {
            
            JsonNode schema = SchemaProvider.getWeatherDataSchema();
            SchemaUtils sUtils = new SchemaUtils();
            // If we don't create a string from the schema, it will always pass with a well formed but non conforming JSON document
            // Don't ask me why!!!
            boolean isValid = sUtils.isJsonValid(schema.toString(), weatherData); 
            return Response.ok().entity(Map.of("isValid", isValid)).build();
        }
        catch(ProcessingException | IOException ex)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
    
    /**
     * Get a list of all the weather parameters defined in the platform
     * @return A list of all the weather parameters defined in the platform
     */
    @GET
    @Path("parameter")
    @Produces(MediaType.APPLICATION_JSON)
    @TypeHint(WeatherParameter[].class)
    public Response listWeatherParameters()
    {
        try
        {
            BufferedInputStream inputStream = new BufferedInputStream(this.getClass().getResourceAsStream("/weather_parameters_draft_v2.yaml"));
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map prelimResult = mapper.readValue(inputStream, HashMap.class);
            List<Map> parameters = (List<Map>) prelimResult.get("parameters");
            List<WeatherParameter> retVal = new ArrayList<>();
            parameters.forEach((pre) -> {
                retVal.add(mapper.convertValue(pre, new TypeReference<WeatherParameter>(){}));
            });
           
            return Response.ok().entity(retVal).build();
        }
        catch(IOException ex)
        {
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
    
    /**
     * 
     * @return The list of QC codes
     */
    @GET
    @Path("qc")
    @Produces(MediaType.APPLICATION_JSON)
    @TypeHint(QCType[].class)
    public Response listQCCodes()
    {
        try
        {
            BufferedInputStream inputStream = new BufferedInputStream(this.getClass().getResourceAsStream("/qc_tests.yaml"));
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            List prelimResult = mapper.readValue(inputStream, ArrayList.class);
            List<QCType> retVal = new ArrayList<>();
            prelimResult.forEach((pre) -> {
                retVal.add(mapper.convertValue(pre, new TypeReference<QCType>(){}));
            });
           
            return Response.ok().entity(retVal).build();
        }
        catch(IOException ex)
        {
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
}
