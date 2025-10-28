package net.ipmdecisions.weather.datasourceadapters.v2.mapper;

import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.FMIForecastResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.MetIrelandResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.SLUResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.YrResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.fmiforecast.FMIForecastMapper;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.metireland.MetIrelandResponseMapper;
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
        return new WeatherData();
    }
}
