package net.ipmdecisions.weather.datasourceadapters.v2.params;


import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class SLUParamModel implements ParamModel {
    Double longitude;
    Double latitude;
    Instant timeStart;
    Instant timeEnd;
    Integer interval;
    List<Integer> parameters;

    @Override
    public boolean supports(String api) {
        return api.equals("SLU");
    }

    @Override
    public void initialize(Map<String, Object> params) {
        this.longitude = (Double) params.get("longitude");
        this.latitude = (Double) params.get("latitude");
        this.timeStart = (Instant) params.get("timeStart");
        this.timeEnd = (Instant) params.get("timeEnd");
        this.interval = (Integer) params.get("interval");
        this.parameters = (List<Integer>) params.get("parameters");
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Instant getTimeStart() {
        return timeStart;
    }

    public Instant getTimeEnd() {
        return timeEnd;
    }

    public Integer getInterval() {
        return interval;
    }

    public List<Integer> getParameters() {
        return parameters;
    }
}
