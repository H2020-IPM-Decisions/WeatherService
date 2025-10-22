package net.ipmdecisions.weather.datasourceadapters.v2.client;

import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.MetIrelandResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.params.MetirelandParamModel;
import net.ipmdecisions.weather.datasourceadapters.v2.params.ParamModel;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import org.xml.sax.SAXException;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

@ApplicationScoped
public class MetIrelandClient implements Client{

    private final static String IRELAND_API_URL = "http://openaccess.pf.api.met.ie/metno-wdb2ts/locationforecast?lat=%f&long=%f";

    @Override
    public Object getData(ParamModel params) {
        var paramModel = (MetirelandParamModel) params;
        Double latitude = paramModel.getLatitude();
        Double longitude = paramModel.getLongitude();
        Double altitude = paramModel.getAltitude();
        URL irelandURL;
        try {
            irelandURL = new URL(String.format(Locale.US,
                    IRELAND_API_URL,
                    latitude,
                    longitude,
                    altitude.intValue()) // Need to do this in order to avoid formatting the int 2000 to "2,000"
            );
            URLConnection connection = irelandURL.openConnection();
            connection.setRequestProperty("User-Agent", "net.ipmdecisions.weatherapi/BETA-07 IPMDecisions@adas.co.uk");
            connection.connect();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return new MetIrelandResponse(db.parse(connection.getInputStream()), paramModel);

        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean supports(String api) {
        return api.equals("meteireann");
    }
}
