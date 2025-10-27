package net.ipmdecisions.weather.datasourceadapters.v2.mapper;

import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.FMIResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.MetIrelandResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.YrResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.fmi.FMIMapper;
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
        if(data instanceof FMIResponse) {
            return FMIMapper.toWeatherData((FMIResponse) data);
        }
        return new WeatherData();
    }
}
