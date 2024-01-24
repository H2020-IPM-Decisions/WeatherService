/*
 * Copyright (c) 2024 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of IPMDecisionsWeatherService.
 * IPMDecisionsWeatherService is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * IPMDecisionsWeatherService is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with IPMDecisionsWeatherService.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package net.ipmdecisions.weather.datasourceadapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.openmeteo.sdk.Aggregation;
import com.openmeteo.sdk.Variable;
import com.openmeteo.sdk.VariableWithValues;
import com.openmeteo.sdk.VariablesSearch;
import com.openmeteo.sdk.VariablesWithTime;
import com.openmeteo.sdk.WeatherApiResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;
import java.util.stream.Collectors;
import net.ipmdecisions.weather.amalgamation.WeatherDataAggregationException;
import net.ipmdecisions.weather.controller.AmalgamationBean;
import net.ipmdecisions.weather.controller.MetaDataBean;
import net.ipmdecisions.weather.controller.WeatherDataSourceBean;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.WeatherDataUtil;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collects data from https://open-meteo.com/. Collates data (if necessary) from historic and forecast endpoints, and aggregates
 * values e.g. temp values to daily. Some daily parameters are provided directly from Open-Meteo
 * Read the documentation here: https://open-meteo.com/en/docs 
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class OpenMeteoAdapter {
    
    private static Logger LOGGER = LoggerFactory.getLogger(OpenMeteoAdapter.class);
    
    private final AmalgamationBean amalgamationBean;
    
    // E.g. "https://archive-api.open-meteo.com/v1/archive?latitude=52.52&longitude=13.41&start_date=2024-01-02&end_date=2024-01-16&hourly=temperature_2m,relative_humidity_2m,rain,wind_speed_10m";
    private String HISTORIC_ENDPOINT_TPL = "https://archive-api.open-meteo.com/v1/archive?format=flatbuffers&latitude=%1$s&longitude=%2$s&start_date=%3$s&end_date=%4$s&%5$s&timezone=%6$s";
    private String FORECAST_ENDPOINT_TPL = "https://api.open-meteo.com/v1/forecast?format=flatbuffers&past_days=2&latitude=%1$s&longitude=%2$s&%3$s&timezone=%4$s";
    OkHttpClient httpClient = new OkHttpClient();
    // Historic data goes up until 2 days before today. So e.g. on the day 2024-01-18, data is provided until UTC 2024-01-16 23:00
    
    public OpenMeteoAdapter(){
        this.amalgamationBean = new AmalgamationBean( new WeatherDataSourceBean(), new MetaDataBean());
    }
    
    private class OpenMeteoParameter {
        
        public OpenMeteoParameter(Integer variable)
        {
            this.variable = variable;
        }
        
        public OpenMeteoParameter(Integer variable, Integer altitude)
        {
            this(variable);
            this.altitude = altitude;
        }
        
        public OpenMeteoParameter(Integer variable, Integer altitude, Integer aggregation)
        {
            this(variable,altitude);
            this.aggregation = aggregation;
        }
        
        private Integer variable;
        private Integer altitude;
        private Integer aggregation;

        /**
         * @return the variable
         */
        public Integer getVariable() {
            return variable;
        }

        /**
         * @param variable the variable to set
         */
        public void setVariable(Integer variable) {
            this.variable = variable;
        }

        /**
         * @return the altitude
         */
        public Integer getAltitude() {
            return altitude;
        }

        /**
         * @param altitude the altitude to set
         */
        public void setAltitude(Integer altitude) {
            this.altitude = altitude;
        }

        /**
         * @return the aggregation
         */
        public Integer getAggregation() {
            return aggregation;
        }

        /**
         * @param aggregation the aggregation to set
         */
        public void setAggregation(Integer aggregation) {
            this.aggregation = aggregation;
        }
    }
    // Parameter mapping
    private final Map<Integer, String> ipmToOpenMeteoRequestHourly = Map.ofEntries(
            entry(1001, "temperature_2m"), // Only hourly
            entry(1002, "temperature_2m"), // Only hourly
            entry(1901, "dew_point_2m"), // Only hourly
            entry(2001, "precipitation"), // Hourly. Daily = precipitation_sum
            entry(3001, "relative_humidity_2m"),// Only hourly
            entry(3002, "relative_humidity_2m"),// Only hourly
            entry(4002, "wind_speed_10m"),// Only hourly
            entry(4003, "wind_speed_10m"), // Only hourly
            entry(4012, "wind_speed_10m"), // Only hourly
            entry(4013, "wind_speed_10m"), // Only hourly
            entry(1111, "soil_temperature_6cm"), // Only hourly
            entry(1112, "soil_temperature_6cm"), // Only hourly
            entry(5001, "shortwave_radiation") // Hourly. Daily = shortwave_radiation_sum
    ); 
    
    private final Map<Integer, String> ipmToOpenMeteoRequestDaily = Map.ofEntries(
            entry(1003, "temperature_2m_min"), // Only daily
            entry(1004, "temperature_2m_max"), // Only daily
            entry(2001, "precipitation_sum"), // Daily. Hourly = precipitatio
            entry(5001, "shortwave_radiation_sum") // Daily. Hourly = shortwave_radiation_sum
    );
    
    
    private final Map<Integer, OpenMeteoParameter> ipmToOpenMeteoResponseHourly = Map.ofEntries(
            entry(1001, new OpenMeteoParameter(Variable.temperature, 2)), 
            entry(1002, new OpenMeteoParameter(Variable.temperature, 2)),
            entry(1901, new OpenMeteoParameter(Variable.dew_point,2)),
            entry(2001, new OpenMeteoParameter(Variable.precipitation)),
            entry(3001, new OpenMeteoParameter(Variable.relative_humidity,2)),
            entry(3002, new OpenMeteoParameter(Variable.relative_humidity,2)),
            entry(4002, new OpenMeteoParameter(Variable.wind_speed,10)),
            entry(4003, new OpenMeteoParameter(Variable.wind_speed,10)),
            entry(4012, new OpenMeteoParameter(Variable.wind_speed,10)),
            entry(4013, new OpenMeteoParameter(Variable.wind_speed,10)),
            entry(1111, new OpenMeteoParameter(Variable.soil_temperature,6)),
            entry(1112, new OpenMeteoParameter(Variable.soil_temperature,6)),
            entry(5001, new OpenMeteoParameter(Variable.shortwave_radiation))

    );
    
    private final Map<Integer, OpenMeteoParameter> ipmToOpenMeteoResponseDaily = Map.ofEntries(
            entry(1003, new OpenMeteoParameter(Variable.temperature, 2, Aggregation.minimum)),
            entry(1004, new OpenMeteoParameter(Variable.temperature, 2, Aggregation.maximum)),
            entry(2001, new OpenMeteoParameter(Variable.precipitation)),
            entry(5001, new OpenMeteoParameter(Variable.shortwave_radiation))
    );
    
    
    public String getParametersURLString(List<Integer> parameters, Integer interval)
    {
        if(interval.equals(WeatherDataUtil.INTERVAL_HOURLY))
        {
            return "hourly=" + String.join(",", parameters.stream()
                    .filter(p->this.ipmToOpenMeteoRequestHourly.containsKey(p))
                    .map(p->this.ipmToOpenMeteoRequestHourly.get(p)).collect(Collectors.toList()));
        }
        // If daily, we must mix and match parameters
        if(interval.equals(WeatherDataUtil.INTERVAL_DAILY))
        {
            // First, get the ones served as daily parameters
            String dailyStr = "daily=" + String.join(",", parameters.stream()
                    .filter(p->this.ipmToOpenMeteoRequestDaily.containsKey(p))
                    .map(p->this.ipmToOpenMeteoRequestDaily.get(p))
                    .collect(Collectors.toList()));
            // Then add hourly ones that daily doesn't contain - that need to be aggregated after retrieval
            String hourlyStr = "hourly=" + String.join(",", parameters.stream()
                    .filter(p->!this.ipmToOpenMeteoRequestDaily.containsKey(p))
                    .map(p->this.ipmToOpenMeteoRequestHourly.get(p))
                    .collect(Collectors.toList()));
            return dailyStr + "&" + hourlyStr;
        }
        return null;
    }
    
    public WeatherData getData(Double longitude, Double latitude, ZoneId tzForLocation, Instant timeStart, Instant timeEnd, Integer interval, List<Integer> parameters)
    {
        // Remove any parameters that Open-Meteo does not provide
        parameters = new ArrayList(parameters.stream().filter(p->
                this.ipmToOpenMeteoRequestDaily.containsKey(p) || this.ipmToOpenMeteoRequestHourly.containsKey(p)
            ).collect(Collectors.toList()));
        // Input check: If the interval is hourly, we cannot provide max and min temperatures
        if(interval.equals(WeatherDataUtil.INTERVAL_HOURLY) && (parameters.contains(1003) || parameters.contains(1004)))
        {
            parameters = new ArrayList(parameters.stream().filter(p->(!p.equals(1003) && ! p.equals(1004))).collect(Collectors.toList()));
        }
        
        // Analyze period - do we need historic + forecast or just one of them?
        // What is two days before today?
        Instant twoDaysBeforeToday = Instant.now().atZone(tzForLocation).toLocalDate().minusDays(2).atStartOfDay(tzForLocation).toInstant();
        LOGGER.debug("two days before today=" + twoDaysBeforeToday);
        Boolean needForecasts = timeEnd.isAfter(twoDaysBeforeToday);
        Boolean needHistoric = timeStart.isBefore(twoDaysBeforeToday);
        String encodedTimeZone;
        try
        {
            encodedTimeZone = URLEncoder.encode(tzForLocation.toString(), StandardCharsets.UTF_8.toString());
        }
        catch(UnsupportedEncodingException ex)
        {
            encodedTimeZone = "UTC"; // Fallback
        }
        // Pull data from source(s), combine if need be. Historic is considered better than forecast
        // Source inspired by: https://github.com/open-meteo/sdk/tree/main/java
        String historicURL = needHistoric ? String.format(this.HISTORIC_ENDPOINT_TPL,
                latitude, longitude,
                timeStart.atZone(tzForLocation).toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                needForecasts ? 
                    Instant.now().atZone(tzForLocation).toLocalDate().atStartOfDay(tzForLocation).format(DateTimeFormatter.ISO_LOCAL_DATE)
                    : timeEnd.atZone(tzForLocation).toLocalDate().atStartOfDay(tzForLocation).format(DateTimeFormatter.ISO_LOCAL_DATE)
                ,
                this.getParametersURLString(parameters, interval),
                encodedTimeZone
            ) 
            : null;
        //System.out.println(historicURL);
        String forecastURL = needForecasts ? String.format(this.FORECAST_ENDPOINT_TPL,
                latitude, longitude,
                this.getParametersURLString(parameters, interval),
                encodedTimeZone
            )
            : null;
        
        //System.out.println(forecastURL);
        try
        {
            WeatherApiResponse historicResponse = needHistoric ? this.getWeatherApiResponse(historicURL) : null;
            WeatherApiResponse forecastResponse = needForecasts ? this.getWeatherApiResponse(forecastURL) : null;
            
            WeatherData historicData = needHistoric ? getWeatherDataFromWeatherApiResponse(historicResponse, latitude, longitude, parameters, interval, tzForLocation) : null;
            WeatherData forecastData = needForecasts ? getWeatherDataFromWeatherApiResponse(forecastResponse, latitude, longitude, parameters, interval, tzForLocation) : null;
            
            LOGGER.debug("historicData is " + (historicData==null ? "" : "not ") + "null, forecastdata is " + (forecastData==null ? "" : "not ") + "null");
            
            // Make sure hourly timeEnd stretches until timeEnd-day at 23H
            if(interval.equals(WeatherDataUtil.INTERVAL_HOURLY))
            {
                timeEnd = timeEnd.atZone(tzForLocation).toLocalDateTime().withHour(23).atZone(tzForLocation).toInstant();
            }
            
            /*
            System.out.println("Fusioning (maybe) historic and forecast with timeStart = " + timeStart + ", timeEnd=" + timeEnd);
            ObjectMapper objectMapper = new ObjectMapper();
            JavaTimeModule javaTimeModule =  new JavaTimeModule();
            objectMapper.registerModule(javaTimeModule);
            //System.out.println(objectMapper.writeValueAsString(historicData));
            //System.out.println(objectMapper.writeValueAsString(forecastData));
            */
            
            // As this.amalgamationBean.getFusionedWeatherData(...) performs time
            // capping, we use it to ensure correct length of data when only
            // using forecast data
            List<WeatherData> weatherData = historicData != null && forecastData != null ?
                    List.of(forecastData, historicData)
                    :historicData != null ? List.of(historicData, new WeatherData())
                    : List.of(forecastData, new WeatherData());
            
            return this.amalgamationBean.getFusionedWeatherData(weatherData, timeStart, timeEnd, interval, tzForLocation);
                   
        }
        catch(IOException | WeatherDataAggregationException ex)
        {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    private WeatherApiResponse getWeatherApiResponse(String url) throws IOException
    {
        LOGGER.debug(url);
        Request request = new Request.Builder()
                .url(url).method("GET",null)
                .build();
        
        Call call = this.httpClient.newCall(request);
        Response response = call.execute();
        if(response.isSuccessful())
        {
            byte[] responseIN = response.body().bytes();
            ByteBuffer buffer = ByteBuffer.wrap(responseIN).order(ByteOrder.LITTLE_ENDIAN);
            WeatherApiResponse mApiResponse = WeatherApiResponse.getRootAsWeatherApiResponse((ByteBuffer) buffer.position(4));
            buffer.clear();
            return mApiResponse;
        }
        LOGGER.debug(response.message());
        return null;
    }

    private WeatherData getWeatherDataFromWeatherApiResponse(WeatherApiResponse ApiResponse, Double latitude, Double longitude, List<Integer> immutableParameters, Integer interval, ZoneId tzForLocation) throws WeatherDataAggregationException, IOException {
        // Need to ensure that the list can be modified
        ArrayList<Integer> parameters = new ArrayList<>(immutableParameters);
        if(interval.equals(WeatherDataUtil.INTERVAL_HOURLY))
        {
            WeatherData hourlyData = new WeatherData();
            hourlyData.setInterval(interval);
            hourlyData.setWeatherParameters(parameters.toArray(Integer[]::new));
            VariablesWithTime variablesWithTime =  ApiResponse.hourly();
            hourlyData.setTimeStart(Instant.ofEpochSecond(variablesWithTime.time()));
            hourlyData.setTimeEnd(Instant.ofEpochSecond(variablesWithTime.timeEnd() - WeatherDataUtil.INTERVAL_HOURLY));
            //System.out.println("INTERVAL IS HOURLY: hourlyData.timeStart= " + hourlyData.getTimeStart() + ", hourlyData.timeEnd=" + hourlyData.getTimeEnd());
            List<VariableWithValues> paramValues = new ArrayList<>();
            for(Integer parameter:parameters)
            {
                paramValues.add(this.getVariableWithValues(variablesWithTime, parameter, interval));
            }

            Double[][] data = new Double[paramValues.get(0).valuesLength()][paramValues.size()];
            int column = 0;
            for(VariableWithValues paramData: paramValues)
            {
                for(int row=0;row<paramData.valuesLength();row++)
                {
                    data[row][column] = Double.valueOf(paramData.values(row));
                }
                column++;
            }

            LocationWeatherData lwd = new LocationWeatherData(longitude, latitude, 0.0, paramValues.get(0).valuesLength(),paramValues.size());
            lwd.setData(data);
            hourlyData.addLocationWeatherData(lwd);
            return hourlyData;
        }
        
        // If interval is daily, we may need to combine aggregated hourly values and "pure" daily values
        if(interval.equals(WeatherDataUtil.INTERVAL_DAILY))
        {
            WeatherData aggregatedHourlyData = null;
            List<Integer> hourlyParamsToCollect = parameters.stream().filter(p->this.ipmToOpenMeteoResponseDaily.get(p)==null).collect(Collectors.toList());
            if(hourlyParamsToCollect != null && !hourlyParamsToCollect.isEmpty())
            {
                WeatherData hourlyData = new WeatherData();
                hourlyData.setInterval(WeatherDataUtil.INTERVAL_HOURLY);
                hourlyData.setWeatherParameters(hourlyParamsToCollect.toArray(Integer[]::new));
                VariablesWithTime hourlyVariablesWithTime =  ApiResponse.hourly();
                hourlyData.setTimeStart(Instant.ofEpochSecond(hourlyVariablesWithTime.time())); 
                hourlyData.setTimeEnd(Instant.ofEpochSecond(hourlyVariablesWithTime.timeEnd() - WeatherDataUtil.INTERVAL_HOURLY)); 
                //System.out.println("INTERVAL IS DAILY: hourlyData.timeStart= " + hourlyData.getTimeStart() + ", hourlyData.timeEnd=" + hourlyData.getTimeEnd());
                List<VariableWithValues> paramValues = new ArrayList<>();
                for(Integer parameter:hourlyParamsToCollect)
                {
                    paramValues.add(this.getVariableWithValues(hourlyVariablesWithTime, parameter, WeatherDataUtil.INTERVAL_HOURLY));
                }

                Double[][] data = new Double[paramValues.get(0).valuesLength()][paramValues.size()];
                int column = 0;
                for(VariableWithValues paramData: paramValues)
                {
                    for(int row=0;row<paramData.valuesLength();row++)
                    {
                        data[row][column] = Double.valueOf(paramData.values(row));
                    }
                    column++;
                }

                LocationWeatherData lwd = new LocationWeatherData(longitude, latitude, 0.0, paramValues.get(0).valuesLength(),paramValues.size());
                lwd.setData(data);
                hourlyData.addLocationWeatherData(lwd);
                
                aggregatedHourlyData = this.amalgamationBean.aggregate(hourlyData, WeatherDataUtil.INTERVAL_DAILY, tzForLocation);
            }
            // If there are specific daily params like max/min temp, precipitation sum or solar radiation sum to be collected
            WeatherData dailyData = null;
            if(hourlyParamsToCollect == null || hourlyParamsToCollect.size() != parameters.size())
            {
                parameters.removeAll(hourlyParamsToCollect);
                dailyData = new WeatherData();
                dailyData.setInterval(WeatherDataUtil.INTERVAL_DAILY);
                dailyData.setWeatherParameters(parameters.toArray(Integer[]::new));
                VariablesWithTime dailyVariablesWithTime = ApiResponse.daily();
                dailyData.setTimeStart(Instant.ofEpochSecond(dailyVariablesWithTime.time()));
                dailyData.setTimeEnd(Instant.ofEpochSecond(dailyVariablesWithTime.timeEnd() - WeatherDataUtil.INTERVAL_DAILY));
                //System.out.println("INTERVAL IS DAILY:dailyData.getTimeStart() = " + dailyData.getTimeStart() + ", dailyData.timeEnd=" + dailyData.getTimeEnd());
                List<VariableWithValues> paramValues = new ArrayList<>();
                for(Integer parameter:parameters)
                {
                    paramValues.add(this.getVariableWithValues(dailyVariablesWithTime, parameter, WeatherDataUtil.INTERVAL_DAILY));
                }
                Double[][] data = new Double[paramValues.get(0).valuesLength()][paramValues.size()];
                int column = 0;
                for(VariableWithValues paramData: paramValues)
                {
                    for(int row=0;row<paramData.valuesLength();row++)
                    {
                        data[row][column] = Double.valueOf(paramData.values(row));
                    }
                    column++;
                }

                LocationWeatherData lwd = new LocationWeatherData(longitude, latitude, 0.0, paramValues.get(0).valuesLength(),paramValues.size());
                lwd.setData(data);
                dailyData.addLocationWeatherData(lwd);
                
            }
            if(dailyData != null && aggregatedHourlyData != null) 
            {
                Instant timeStart = dailyData.getTimeStart().compareTo(aggregatedHourlyData.getTimeStart()) < 0 ? dailyData.getTimeStart() : aggregatedHourlyData.getTimeStart();
                Instant timeEnd = dailyData.getTimeEnd().compareTo(aggregatedHourlyData.getTimeEnd()) > 0 ? dailyData.getTimeEnd() : aggregatedHourlyData.getTimeEnd();
                //System.out.println("Fusioning (aggregated) daily and hourly with timestart=" + timeStart +", timeend=" + timeEnd);
                return this.amalgamationBean.getFusionedWeatherData(List.of(aggregatedHourlyData,dailyData), timeStart, timeEnd, interval, tzForLocation);
            }
            else
            {
                return dailyData != null ? dailyData : aggregatedHourlyData;
            }
        }
        return null;
        
    }

    
    /**
     * Mapping IPMD and Open-Meteo parameters
     * @param variablesWithTime
     * @param parameter
     * @return Array of the requested parameter
     */
    private VariableWithValues getVariableWithValues(VariablesWithTime variablesWithTime, Integer parameter, Integer interval)
    {
        Map<Integer, OpenMeteoParameter> mapping = interval.equals(WeatherDataUtil.INTERVAL_HOURLY) ? this.ipmToOpenMeteoResponseHourly : this.ipmToOpenMeteoResponseDaily;

        if(mapping.containsKey(parameter))
        {
            OpenMeteoParameter param = mapping.get(parameter);
            VariablesSearch search = new VariablesSearch(variablesWithTime).variable(param.getVariable());
            if(param.getAltitude() != null)
            {
                search = search.altitude(param.getAltitude());
            }
            if(param.getAggregation() != null)
            {
                search = search.aggregation(param.getAggregation());
            }
            return search.first();
        }
        
        
        return null;
    }
}
