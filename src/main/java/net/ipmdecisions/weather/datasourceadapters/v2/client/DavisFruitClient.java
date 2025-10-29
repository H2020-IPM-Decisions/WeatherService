package net.ipmdecisions.weather.datasourceadapters.v2.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotAuthorizedException;
import net.ipmdecisions.weather.datasourceadapters.DavisFruitwebAdapter;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.DavisFruitResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.params.DavisFruitParamModel;
import net.ipmdecisions.weather.datasourceadapters.v2.params.ParamModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@ApplicationScoped
public class DavisFruitClient implements Client {

    @Override
    public Object getData(ParamModel params) {
        var davisParams = (DavisFruitParamModel) params;
        String stationID = davisParams.getStationID();
        String password = davisParams.getPassword();
        Instant startDate = davisParams.getStartDate();
        TimeZone timeZone = davisParams.getTimeZone();

        SimpleDateFormat urlFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat lineFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        urlFormat.setTimeZone(timeZone);
        lineFormat.setTimeZone(timeZone);

        StringBuilder rawResponse = new StringBuilder();

        try {
            URL url = buildUrl(stationID, password, urlFormat.format(Date.from(startDate)));
            System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            validateAuthorization(connection);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    rawResponse.append(line).append('\n');
                }
            }
            return new DavisFruitResponse(rawResponse.toString(), davisParams);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private URL buildUrl(String stationID, String password, String formattedDate) throws Exception {
        return new URL(MessageFormat.format(
                DavisFruitwebAdapter.FRUITWEB_URL_TEMPLATE,
                stationID, password, formattedDate));
    }

    private void validateAuthorization(HttpURLConnection connection) throws Exception {
        int responseCode = connection.getResponseCode();
        if (responseCode == 401 || responseCode == 403) {
            throw new NotAuthorizedException("Access denied by FruitWeb. Please check your credentials");
        }
    }

    @Override
    public boolean supports(String api) {
        return api.equals("DavisFruit");
    }
}
