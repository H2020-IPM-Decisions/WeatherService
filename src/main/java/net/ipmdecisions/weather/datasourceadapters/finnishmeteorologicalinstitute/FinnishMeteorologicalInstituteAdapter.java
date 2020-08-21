/*
 * Copyright (c) 2020 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of IPM Decisions Weather Service.
 * IPM Decisions Weather Service is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * IPM Decisions Weather Service is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with IPM Decisions Weather Service.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.ipmdecisions.weather.datasourceadapters.finnishmeteorologicalinstitute;

import net.ipmdecisions.weather.util.vips.VIPSWeatherObservation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;

/**
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class FinnishMeteorologicalInstituteAdapter {

    public WeatherData getHourlyData(Integer weatherStationId, Instant timeStart, Instant timeEnd, List<Integer> ipmDecisionsParameters, Boolean ignoreErrors)  {
        try
        {
            // The first set of observations, with parameters TM, FF2, UM and RR
            FmiIntermediator intermediator = new FmiIntermediator();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"));

            // The string from the "backend" contains a header with postion data. 
            // separated from the JSON with the string IPMDECISIONS
            String[] obsAsStringWithPositionPrefix = intermediator.getTemporalData_vips(weatherStationId.toString(), formatter.format(timeStart), formatter.format(timeEnd)).split("IPMDECISIONS");
            String[] latLongStr = obsAsStringWithPositionPrefix[0].trim().split("\\s+");
            String obsAsString = obsAsStringWithPositionPrefix[1];
            ObjectMapper mapper = new ObjectMapper();
            List<VIPSWeatherObservation> observations = mapper.readValue(obsAsString, new TypeReference<List<VIPSWeatherObservation>>(){});
            
            // If global radiation has been requested, try to get it (not all stations
            // measure it) and add to collection.
            if(ipmDecisionsParameters.contains(5001))
            {
                FmiOpenDataAccess dA = new FmiOpenDataAccess();
                FmiOpenDataRadiationParser rP = new FmiOpenDataRadiationParser();
                // Radiation data max period is 7 days (168 hours)
                Instant start7DayPeriod = timeStart;
                Instant end7DayPeriod = start7DayPeriod.plus(7, ChronoUnit.DAYS);
                while(start7DayPeriod.isBefore(timeEnd))
                {
                    String radiationDataFromFMIXML = dA.getRadiationData(weatherStationId.toString(), start7DayPeriod, end7DayPeriod);
                    //System.out.println(radiationDataFromFMIXML);
                    if(!radiationDataFromFMIXML.isBlank())
                    {
                        List<VIPSWeatherObservation> newObs = rP.getVIPSWeatherObservations(radiationDataFromFMIXML);
                        if(newObs != null)
                        {
                            observations.addAll(newObs);
                        }
                    }
                    start7DayPeriod = start7DayPeriod.plus(7, ChronoUnit.DAYS);
                    end7DayPeriod = end7DayPeriod.plus(7, ChronoUnit.DAYS);
                }
            }
            
            Integer[] parameters = observations.stream()
                    .filter(obs->ipmDecisionsParameters.contains(Integer.valueOf(obs.getElementMeasurementTypeId())))
                    .map(obs->Integer.valueOf(obs.getElementMeasurementTypeId()))
                    .collect(Collectors.toSet())
                    .toArray(Integer[]::new);
                    
            Map<Integer,Integer> paramCol = new HashMap<>();
            for(int i=0;i<parameters.length;i++)
            {
                paramCol.put(parameters[i], i);
            }
            
            //elementMeasurementTypeIds.toArray(parameters);
            // Convert to IPM Decisions format
            Integer interval = 3600; // Hourly data
            Long rows = 1 + timeStart.until(timeEnd, ChronoUnit.SECONDS) / interval;
            LocationWeatherData ipmData = new LocationWeatherData(
                    Double.valueOf(latLongStr[1]),
                    Double.valueOf(latLongStr[0]),
                    0.0,
                    rows.intValue(),
                    parameters.length
            );
            WeatherData weatherData = new WeatherData();
            weatherData.setInterval(interval);
            weatherData.setTimeStart(timeStart);
            weatherData.setTimeEnd(timeEnd);
            weatherData.setWeatherParameters(parameters);

            observations.stream()
                .filter(obse->ipmDecisionsParameters.contains(Integer.valueOf(obse.getElementMeasurementTypeId())))
                .forEach(obse -> {
                    Long row = timeStart.until(obse.getTimeMeasured().toInstant(), ChronoUnit.SECONDS) / interval;
                    Integer col = paramCol.get(Integer.valueOf(obse.getElementMeasurementTypeId()));
                    ipmData.setValue(row.intValue(), col, obse.getValue());
            });
            weatherData.addLocationWeatherData(ipmData);
            
            // Set the QC - all is 1, as this comes from an official meteorological office
            Integer[] QC = new Integer[weatherData.getWeatherParameters().length];
            for(int i=0;i<QC.length;i++) {
                QC[i] = 1;
            }
            weatherData.setQC(QC);
            return weatherData;
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

}
