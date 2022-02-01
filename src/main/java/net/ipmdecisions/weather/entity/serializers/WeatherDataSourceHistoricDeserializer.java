package net.ipmdecisions.weather.entity.serializers;

import java.io.IOException;
import java.time.LocalDate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ipmdecisions.weather.entity.WeatherDataSource.Temporal.Historic;

public class WeatherDataSourceHistoricDeserializer extends JsonDeserializer<Historic>{

	@Override
	public Historic deserialize(JsonParser jsonParser, DeserializationContext dContext)
			throws IOException, JsonProcessingException {
		ObjectMapper oM = new ObjectMapper();
		JsonNode node = (JsonNode) oM.readTree(jsonParser);
		
		Historic historic = new Historic();
		historic.setStart(this.getLocalDate(node.get("start")));
		historic.setEnd(this.getLocalDate(node.get("end")));
		
		return historic;
	}
	
	private LocalDate getLocalDate(JsonNode property)
	{
		if(property == null || property.isNull())
		{
			return null;
		}
		String txtVal = property.asText();
		if(txtVal.contains("{CURRENT_YEAR}"))
		{
			txtVal = txtVal.replace("{CURRENT_YEAR}", String.valueOf(LocalDate.now().getYear()));
		}
		return LocalDate.parse(txtVal);
	}

}
