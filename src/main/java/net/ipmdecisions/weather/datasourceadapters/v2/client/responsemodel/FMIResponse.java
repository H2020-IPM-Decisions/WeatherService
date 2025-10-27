package net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel;

import net.ipmdecisions.weather.datasourceadapters.v2.params.FMIParamModel;

public record FMIResponse(String data, FMIParamModel params) {
}
