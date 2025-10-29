package net.ipmdecisions.weather.datasourceadapters.v2.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotAuthorizedException;
import net.ipmdecisions.weather.datasourceadapters.MeteobotAPIAdapter;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.MateobotResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.params.MateobotParamModel;
import net.ipmdecisions.weather.datasourceadapters.v2.params.ParamModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;

@ApplicationScoped
public class MareobotClient implements Client {
    @Override
    public Object getData(ParamModel params) {
        setLocationParams((MateobotParamModel) params);
        var indexData = getIndex((MateobotParamModel) params);
        return new MateobotResponse(indexData, (MateobotParamModel) params);
    }

    @Override
    public boolean supports(String api) {
        return api.equals("mateobot");
    }

    private MateobotResponse setLocationParams(MateobotParamModel meteobotParams) {
        var stationID = meteobotParams.getStationID();
        var userName = meteobotParams.getUserName();
        var password = meteobotParams.getPassword();
        var startDate = meteobotParams.getStartDate();
        var endDate = meteobotParams.getEndDate();
        double latitude;
        double longitude;
        String method = "Locate";
        try {
            URL meteobotURL = new URL(MessageFormat.format(MeteobotAPIAdapter.METEOS_URL_TEMPLATE, method, stationID, startDate, endDate));
            HttpURLConnection connection = (HttpURLConnection) meteobotURL.openConnection();
            String userpass = userName + ":" + password;
            String basicAuth = "Basic " + jakarta.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
            connection.setRequestProperty("Authorization", basicAuth);
            StringBuilder rawResponse = new StringBuilder();

            int responseCode = connection.getResponseCode();
            if (responseCode == 401) {
                throw new NotAuthorizedException("Access denied by MeteoBot. Please check your credentials");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                rawResponse.append(line).append('\n');
                String[] lineParts = line.split(";");
                if (!isDouble(lineParts[2]) || !isDouble(lineParts[3])) {
                    continue;
                }
                latitude = Double.parseDouble(lineParts[2]);
                longitude = Double.parseDouble(lineParts[3]);
                meteobotParams.setLatitude(latitude);
                meteobotParams.setLongitude(longitude);
            }
            return new MateobotResponse(rawResponse.toString(), meteobotParams);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getIndex(MateobotParamModel meteobotParams) {
        var stationID = meteobotParams.getStationID();
        var userName = meteobotParams.getUserName();
        var password = meteobotParams.getPassword();
        var startDate = meteobotParams.getStartDate();
        var endDate = meteobotParams.getEndDate();
        String method = "Index";
        try {
            URL meteobotURL = new URL(MessageFormat.format(MeteobotAPIAdapter.METEOS_URL_TEMPLATE, method, stationID, startDate, endDate));
            HttpURLConnection connection = (HttpURLConnection) meteobotURL.openConnection();
            String userpass = userName + ":" + password;
            String basicAuth = "Basic " + jakarta.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
            connection.setRequestProperty("Authorization", basicAuth);
            StringBuilder rawResponse = new StringBuilder();

            int responseCode = connection.getResponseCode();
            if (responseCode == 401) {
                throw new NotAuthorizedException("Access denied by MeteoBot. Please check your credentials");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                rawResponse.append(line).append('\n');
            }
            return rawResponse.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
