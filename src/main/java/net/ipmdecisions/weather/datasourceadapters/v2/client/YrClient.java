package net.ipmdecisions.weather.datasourceadapters.v2.client;

import net.ipmdecisions.weather.datasourceadapters.YrWeatherForecastAdapter;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.YrResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.params.ParamModel;
import net.ipmdecisions.weather.datasourceadapters.v2.params.YrParamModel;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import org.w3c.dom.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

@ApplicationScoped
public class YrClient implements Client{

    private final static String YR_API_URL = "https://api.met.no/weatherapi/locationforecast/2.0/classic?lat=%f&lon=%f&altitude=%d";

    @Override
    public Object getData(ParamModel params) {
        var paramModel = (YrParamModel) params;
        var latitude = paramModel.getLatitude();
        var longitude = paramModel.getLongitude();
        var altitude = paramModel.getAltitude();
        URL yrURL;

        try {
            yrURL = new URL(String.format(Locale.US,
                    YR_API_URL,
                    latitude,
                    longitude,
                    altitude.intValue())
            );
            URLConnection connection = yrURL.openConnection();
            connection.setRequestProperty("User-Agent", "net.ipmdecisions.weatherapi/BETA-07 IPMDecisions@adas.co.uk");
            connection.connect();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(connection.getInputStream());
            return new YrResponse(doc, paramModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean supports(String api) {
        return api.equals("yr");
    }
}
