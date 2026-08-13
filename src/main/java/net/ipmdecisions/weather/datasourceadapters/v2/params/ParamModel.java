package net.ipmdecisions.weather.datasourceadapters.v2.params;

import java.util.Map;

public interface ParamModel {
    boolean supports(String api);
    void initialize(Map<String, Object> params);
}
