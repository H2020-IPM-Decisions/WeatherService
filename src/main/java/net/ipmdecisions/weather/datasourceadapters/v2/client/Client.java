package net.ipmdecisions.weather.datasourceadapters.v2.client;

import net.ipmdecisions.weather.datasourceadapters.v2.params.ParamModel;

public interface Client {
    Object getData(ParamModel params);
    boolean supports(String api);

}
