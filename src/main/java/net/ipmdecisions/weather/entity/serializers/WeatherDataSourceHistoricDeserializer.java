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
		if(txtVal.contains("CURRENT_YEAR"))
		{
			// Check if there is arithmetic involved
			String expression = txtVal.substring(txtVal.indexOf("{")+1,txtVal.indexOf("}"));
			String arithmetic = expression.contains("+") ? "+" : expression.contains("-") ? "-" : "";
			Integer number = !arithmetic.isBlank() ? Integer.valueOf(expression.substring(expression.indexOf(arithmetic)).trim()) : 0;
			
			
			txtVal = txtVal.replace(txtVal.substring(txtVal.indexOf("{"),txtVal.indexOf("}")+1), String.valueOf(LocalDate.now().getYear() + number));
		}
		return LocalDate.parse(txtVal);
	}

}
