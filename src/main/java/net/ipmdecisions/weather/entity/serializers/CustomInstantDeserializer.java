package net.ipmdecisions.weather.entity.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
public class CustomInstantDeserializer extends JsonDeserializer<Instant>{


	/**
	 * Jackson's jsr310 module can't deserialize to Instant. So we have to implement this ourselves
	 */
	@Override
	public Instant deserialize(JsonParser arg0, DeserializationContext arg1)
			throws IOException {
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
			Date d = sdf.parse(arg0.getText());
			return d.toInstant();
		}
		catch(ParseException ex)
		{
			try
			{
				// Check if this is an epoch number
				Long possibleEpochSecond = ((Double)Double.parseDouble(arg0.getText())).longValue();
				return Instant.ofEpochSecond(possibleEpochSecond);
			}
			catch(NumberFormatException ex2)
			{
				throw new IOException(ex.getMessage());				
			}
			
		}
	}
	
}
