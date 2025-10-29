package net.ipmdecisions.weather.datasourceadapters.v2.client;

import jakarta.enterprise.context.ApplicationScoped;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.SLUResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.params.ParamModel;
import net.ipmdecisions.weather.datasourceadapters.v2.params.SLUParamModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;

import static net.ipmdecisions.weather.datasourceadapters.v2.mapper.common.ParameterMapper.IPM_TO_VIPS;

@ApplicationScoped
public class SLUClient implements Client {

    private static final String CREDENTIALS_PARAMSTRING =
            System.getProperty("net.ipmdecisions.weatherservice.SLU_LANTMET_ADAPTER_CREDENTIALS_PARAMSTRING", "");

    private static final String SLU_API_URL_TEMPLATE = "https://www.ffe.slu.se/lm/json/LantmetDWL.cfm"
            + "?centerWGS84n=%s"
            + "&centerWGS84e=%s"
            + "&outputType=JSON"
            + "&inputType=GRID"
            + "&logIntervalId=%d"
            + "&startDate=%s"
            + "&endDate=%s"
            + "&elementMeasurementTypeList=%s"
            + "&nDegrees=0&eDegrees=0"
            + "%s";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ZoneId SWEDISH_NORMAL_TIME = ZoneId.of("GMT+1");

    @Override
    public Object getData(ParamModel paramModel) {
        SLUParamModel params = (SLUParamModel) paramModel;

        try {
            URL url = buildUrl(params);
            HttpURLConnection connection = openFollowingRedirects(url);
            try (BufferedReader data =
                         new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = data.readLine()) != null) {
                        responseBuilder.append(line);
                }
                return new SLUResponse(responseBuilder.toString(), params);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URL buildUrl(SLUParamModel params) throws IOException {
        int logIntervalId = (params.getInterval() == 3600 ? 1 : 2);
        String start = params.getTimeStart().atZone(SWEDISH_NORMAL_TIME).format(DATE_FORMAT);
        String end = params.getTimeEnd().atZone(SWEDISH_NORMAL_TIME).format(DATE_FORMAT);
        String elementList = params.getParameters().stream()
                .map(this::getVIPSParameterId)
                .filter(id -> id != null)
                .collect(Collectors.joining(","));
        Long sluLongitude = Math.round(params.getLongitude() * 1000);
        Long sluLatitude = Math.round(params.getLatitude() * 1000);

        String urlString = String.format(
                Locale.US,
                SLU_API_URL_TEMPLATE,
                sluLatitude,
                sluLongitude,
                logIntervalId,
                start,
                end,
                elementList,
                CREDENTIALS_PARAMSTRING
        );
        return new URL(urlString);
    }

    private HttpURLConnection openFollowingRedirects(URL initialUrl) throws IOException {
        URL current = initialUrl;
        for (int i = 0; i < 5; i++) {
            HttpURLConnection conn = (HttpURLConnection) current.openConnection();
            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP) {
                String location = conn.getHeaderField("Location");
                conn.disconnect();
                current = new URL(location);
                continue;
            }
            return conn;
        }
        return (HttpURLConnection) current.openConnection();
    }

    @Override
    public boolean supports(String api) {
        return "SLU".equals(api);
    }

    public String getVIPSParameterId(Integer ipmParameterId) {
        return IPM_TO_VIPS.get(ipmParameterId);
    }
}
