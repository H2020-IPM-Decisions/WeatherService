package net.ipmdecisions.weather.datasourceadapters.v2.service;

import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.ipmdecisions.weather.datasourceadapters.v2.client.Client;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.WeatherDataMapper;
import net.ipmdecisions.weather.datasourceadapters.v2.params.ParamModel;
import net.ipmdecisions.weather.entity.WeatherData;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class WeatherDataService {

    private final List<Client> clients;
    private final List<ParamModel> paramModels;

    @Inject
    public WeatherDataService(@All List<Client> clients, @All List<ParamModel> paramModels) {
        this.clients = clients;
        this.paramModels = paramModels;
    }

    public WeatherData getWeatherData(String api, Map<String, Object> params) {
        var paramModel = getParamModel(api);
        var client = getClient(api);
        paramModel.initialize(params);
        var data = client.getData(paramModel);
        return WeatherDataMapper.mapWeatherData(data);
    }

    private Client getClient(String api) {
        return clients.stream()
                .filter(c -> c.supports(api))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No client found for api: " + api));
    }

    private ParamModel getParamModel(String api) {
        return paramModels.stream()
                .filter(c -> c.supports(api))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No param model found for api: " + api));
    }
}