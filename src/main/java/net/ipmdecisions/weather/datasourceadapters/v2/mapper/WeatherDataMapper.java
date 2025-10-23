package net.ipmdecisions.weather.datasourceadapters.v2.mapper;

import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.MetIrelandResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.YrResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.metireland.MetIrelandResponseMapper;
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
        return new WeatherData();
    }
}
