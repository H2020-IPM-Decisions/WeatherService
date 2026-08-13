package net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel;

import net.ipmdecisions.weather.datasourceadapters.v2.params.MetosParamModel;

public record MetosResponse(String stationInfoJson, String dataJson, MetosParamModel params) {
}