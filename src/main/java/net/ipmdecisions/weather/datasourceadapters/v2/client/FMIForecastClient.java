package net.ipmdecisions.weather.datasourceadapters.v2.client;

import jakarta.enterprise.context.ApplicationScoped;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.FMIForecastResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.params.FMIForecastParamModel;
import net.ipmdecisions.weather.datasourceadapters.v2.params.ParamModel;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

@ApplicationScoped
public class FMIForecastClient implements Client{

    public Object getData(ParamModel paramModel)
    {
        var params = (FMIForecastParamModel) paramModel;
        double latitude = params.getLatitude();
        double longitude = params.getLongitude();
        try
        {
            String URLTemplate = "http://opendata.fmi.fi/wfs?storedquery_id=fmi::forecast::harmonie::surface::point::multipointcoverage&latlon={0},{1}&request=getFeature&starttime={2}&parameters=Temperature,Humidity,WindSpeedMS,DewPoint,Precipitation1h,radiationglobal";
            // Get today at midnight, GMT time zone
            LocalDateTime todayAtMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            ZoneId UTCId = ZoneId.of("UTC");
            ZonedDateTime UTCTodayAtMidnight = ZonedDateTime.of(todayAtMidnight, UTCId);

            var data = new Scanner(new URL(MessageFormat.format(URLTemplate,
                    latitude, longitude,
                    UTCTodayAtMidnight.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"))
            )).openStream(), "UTF-8").useDelimiter("\\A").next();
            return new FMIForecastResponse(data, params);

        } catch(IOException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean supports(String api) {
        return api.equals("FMI");
    }
}
