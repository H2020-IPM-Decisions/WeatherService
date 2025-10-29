package net.ipmdecisions.weather.datasourceadapters.v2.params;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.Map;
import java.util.TimeZone;

@ApplicationScoped
public class DavisFruitParamModel implements ParamModel{

    private String stationID;
    private String password;
    private TimeZone timeZone;
    private Instant startDate;
    private Instant endDate;

    @Override
    public boolean supports(String api) {
        return api.equals("DavisFruit");
    }

    @Override
    public void initialize(Map<String, Object> params) {
        this.stationID = (String) params.get("stationID");
        this.password = (String) params.get("password");
        this.timeZone = (TimeZone) params.get("timeZone");
        this.startDate = (Instant) params.get("startDate");
        this.endDate = (Instant) params.get("endDate");
    }

    public String getStationID() {
        return stationID;
    }

    public String getPassword() {
        return password;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }
}
