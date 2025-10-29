package net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel;

import net.ipmdecisions.weather.datasourceadapters.v2.params.MateobotParamModel;

public record MateobotResponse(String data, MateobotParamModel params) {
}
