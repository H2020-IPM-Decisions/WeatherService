package net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel;

import net.ipmdecisions.weather.datasourceadapters.v2.params.FMIForecastParamModel;

public record FMIForecastResponse(String data, FMIForecastParamModel params) {
}
