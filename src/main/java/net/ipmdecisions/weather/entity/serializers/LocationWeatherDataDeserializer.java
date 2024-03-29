package net.ipmdecisions.weather.entity.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ipmdecisions.weather.entity.LocationWeatherData;

import java.io.IOException;

/**
 * (c) 2021 NIBIO
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 *
 */
public class LocationWeatherDataDeserializer extends JsonDeserializer<LocationWeatherData>{


	
	@Override
	public LocationWeatherData deserialize(JsonParser jsonParser, DeserializationContext arg1)
			throws IOException {

			ObjectMapper oM = new ObjectMapper();
			JsonNode node = (JsonNode) oM.readTree(jsonParser);
			Double longitude = node.get("longitude").asDouble();
			Double latitude = node.get("latitude").asDouble();
			Double altitude = node.get("altitude").asDouble();
			JsonNode dataNode = node.get("data");
			Double[][] data = new Double[dataNode.size()][dataNode.get(0).size()];
			int i = 0;
			int j = 0;
			for(JsonNode row:dataNode)
			{
				for(JsonNode col:row)
				{
					data[i][j++] = col.asText().toLowerCase().equals("null")  ? null: col.asDouble();
				}
				j = 0;
				i++;
			}
			
			LocationWeatherData retVal = new LocationWeatherData(longitude,latitude,altitude, data.length, data[0].length);
			retVal.setData(data);
			
			// QC is optional
			if(node.get("qc") != null)
			{
				Integer[] qc = new Integer[node.get("qc").size()];
				i = 0;
				for(JsonNode qcNode:node.get("qc"))
				{
					qc[i++] = qcNode.asInt();
				}
				retVal.setQC(qc);
			}
			return retVal;
	}
	
}
