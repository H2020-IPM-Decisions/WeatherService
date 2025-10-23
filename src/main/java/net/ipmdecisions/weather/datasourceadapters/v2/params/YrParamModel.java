package net.ipmdecisions.weather.datasourceadapters.v2.params;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class YrParamModel implements  ParamModel {
    Double longitude;
    Double latitude;
    Double altitude;

    @Override
    public boolean supports(String api) {
        return api.equals("yr");
    }

    @Override
    public void initialize(Map<String, Object> params) {
        this.longitude = (Double) params.get("longitude");
        this.latitude = (Double) params.get("latitude");
        this.altitude = (Double) params.get("altitude");
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getAltitude() {
        return altitude;
    }
}
