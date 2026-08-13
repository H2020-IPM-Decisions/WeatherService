package net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel;

import net.ipmdecisions.weather.datasourceadapters.v2.params.SLUParamModel;

public record SLUResponse(String data, SLUParamModel params) {
}
