package net.ipmdecisions.weather.datasourceadapters.v2.params;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;

@ApplicationScoped
public class FMIParamModel implements ParamModel{

    Double longitude;
    Double latitude;

    @Override
    public boolean supports(String api) {
        return api.equals("FMI");
    }

    @Override
    public void initialize(Map<String, Object> params) {
        this.longitude = (Double) params.get("longitude");
        this.latitude = (Double) params.get("latitude");
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }
}
