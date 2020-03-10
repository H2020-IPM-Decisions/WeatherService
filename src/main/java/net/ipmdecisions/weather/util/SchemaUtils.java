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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;




/**
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class SchemaUtils {
    
    public static final String JSON_V4_SCHEMA_IDENTIFIER = "http://json-schema.org/draft-04/schema#";
    public static final String JSON_SCHEMA_IDENTIFIER_ELEMENT = "$schema";
    
    public Boolean isJsonValid(URL schemaURL, JsonNode jsonNode) throws IOException, ProcessingException
    {
        JsonSchema schemaNode = this.getSchemaNode(JsonLoader.fromURL(schemaURL));
        return this.isJsonValid(schemaNode, jsonNode);
    }
    
    public Boolean isJsonValid(String schema, JsonNode jsonNode) throws IOException, ProcessingException
    {
        JsonSchema schemaNode = this.getSchemaNode(JsonLoader.fromString(schema));
        return this.isJsonValid(schemaNode, jsonNode);
    }
    
    public Boolean isJsonValid(JsonSchema schemaNode, JsonNode jsonNode) throws ProcessingException
    {
        ProcessingReport report = schemaNode.validate(jsonNode);
        return report.isSuccess();
    }
    
    public Boolean isJsonValid(JsonNode schemaNode, JsonNode jsonNode) throws ProcessingException
    {
        JsonSchema s = this.getSchemaNode(jsonNode);
        return this.isJsonValid(s, jsonNode);
    }
    
    /**
     * Create a JsonSchema from the Json structure
     * @param jsonNode
     * @return
     * @throws ProcessingException 
     */
    private JsonSchema getSchemaNode(JsonNode jsonNode) throws ProcessingException
    {
        JsonNode schemaIdentifier = jsonNode.get(SchemaUtils.JSON_SCHEMA_IDENTIFIER_ELEMENT);
        if(schemaIdentifier == null)
        {
            ((ObjectNode)jsonNode).put(SchemaUtils.JSON_SCHEMA_IDENTIFIER_ELEMENT, SchemaUtils.JSON_V4_SCHEMA_IDENTIFIER);
            
        }
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        return factory.getJsonSchema(jsonNode);
    }
    
    public JsonNode getJsonFromInputStream(InputStream inputStream) throws IOException
    {
        
            JsonFactory f = new MappingJsonFactory();
            JsonParser jp = f.createParser(inputStream);
            JsonNode all = jp.readValueAsTree();
            return all;
    }
}
