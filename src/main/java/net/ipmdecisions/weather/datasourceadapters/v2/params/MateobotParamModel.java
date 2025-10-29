package net.ipmdecisions.weather.datasourceadapters.v2.params;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.Map;

@ApplicationScoped
public class MateobotParamModel implements ParamModel{

    private Integer stationID;
    private String userName;
    private String password;
    private LocalDate startDate;
    private LocalDate endDate;
    private double latitude;
    private double longitude;

    @Override
    public boolean supports(String api) {
        return api.equals("mateobot");
    }

    @Override
    public void initialize(Map<String, Object> params) {
        this.stationID = (Integer) params.get("stationID");
        this.userName = (String) params.get("userName");
        this.password = (String) params.get("password");
        this.startDate = (LocalDate) params.get("startDate");
        this.endDate = (LocalDate) params.get("endDate");
    }

    public Integer getStationID() {
        return stationID;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


}
