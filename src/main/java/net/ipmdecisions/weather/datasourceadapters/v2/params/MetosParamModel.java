package net.ipmdecisions.weather.datasourceadapters.v2.params;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.Map;

@ApplicationScoped
public class MetosParamModel implements ParamModel {

    private String stationID;
    private String publicKey;
    private String privateKey;
    private LocalDate startDate;
    private LocalDate endDate;

    @Override
    public boolean supports(String api) {
        return api.equals("metos");
    }

    @Override
    public void initialize(Map<String, Object> params) {
        this.stationID = (String) params.get("stationID");
        this.publicKey = (String) params.get("publicKey");
        this.privateKey = (String) params.get("privateKey");
        this.startDate = (LocalDate) params.get("startDate");
        this.endDate = (LocalDate) params.get("endDate");
    }

    public String getStationID() { return stationID; }
    public String getPublicKey() { return publicKey; }
    public String getPrivateKey() { return privateKey; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
}