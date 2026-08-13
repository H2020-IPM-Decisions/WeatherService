package net.ipmdecisions.weather.datasourceadapters.v2.mapper;

import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.*;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.davisfruit.DavisFruitMapperResponseMapper;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.fmiforecast.FMIForecastMapper;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.mateobot.MateobotResponseMapper;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.metireland.MetIrelandResponseMapper;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.metos.MetosResponseMapper;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.slu.SLUResponseMapper;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.yr.YrResponseMapper;
import net.ipmdecisions.weather.entity.WeatherData;

public class WeatherDataMapper {
    public static WeatherData mapWeatherData(Object data) {
        if(data instanceof MetIrelandResponse) {
            return MetIrelandResponseMapper.toWeatherData((MetIrelandResponse) data);
        }
        if(data instanceof YrResponse) {
            return YrResponseMapper.toWeatherData((YrResponse) data);
        }
        if(data instanceof FMIForecastResponse) {
            return FMIForecastMapper.toWeatherData((FMIForecastResponse) data);
        }
        if(data instanceof SLUResponse) {
            return SLUResponseMapper.toWeatherData((SLUResponse) data);
        }
        if(data instanceof DavisFruitResponse) {
            return DavisFruitMapperResponseMapper.toWeatherData((DavisFruitResponse) data);
        }
        if (data instanceof MateobotResponse) {
            return MateobotResponseMapper.toWeatherData((MateobotResponse) data);
        }
        if (data instanceof MetosResponse) {
            return MetosResponseMapper.toWeatherData((MetosResponse) data);
        }
        return new WeatherData();
    }
}
