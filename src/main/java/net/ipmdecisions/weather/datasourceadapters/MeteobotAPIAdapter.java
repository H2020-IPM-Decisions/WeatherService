/*
 * Copyright (c) 2020 NIBIO <http://www.nibio.no/>. 
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.vips.VIPSWeatherObservation;
import net.ipmdecisions.weather.util.vips.WeatherUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Gets data from the Meteobot API. Read about the API here:
 * 
 * Code from the VIPSWeatherProxy adapted to IPM Decisions
 *
 * @copyright 2020-2024 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Brita Linnestad <brita.linnestad@nibio.no>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class MeteobotAPIAdapter {
    
    private static Logger LOGGER = LoggerFactory.getLogger(MeteobotAPIAdapter.class);

    private final WeatherUtils weatherUtils;

    public final static String METEOS_URL_TEMPLATE = "https://export.meteobot.com/v1/Generic/{0}?id={1,number,#}&startdate={2}&enddate={3}&timeFormat=iso-8601";

    private final static String[][] elementMeasurementTypes = {
        {"airTemperature", "TM", "AVG"},
        {"airPressure", "POM", "AVG"},
        {"airHumidity", "UM", "AVG"},
        {"dewPoint", "DUMMY", "AVG"},
        {"earthHumidity1", "EH1", "AVG"},
        {"earthHumidity2", "EH2", "AVG"},
        {"earthHumidity3", "EH3", "AVG"},
        {"earthTemperature1", "ET1", "AVG"},
        {"earthTemperature2", "ET2", "AVG"},
        {"earthTemperature3", "ET3", "AVG"},
        {"leafWetness1", "BT1", "SUM"},
        {"leafWetness2", "BT2", "SUM"},
        {"precipitation", "RR", "SUM"},
        {"windDirection", "DM2", "AVG"},
        {"windSpeed", "FM2", "AVG"},
        {"solarRadiation", "Q0", "AVG"},
        {"batteryVoltage", "BATTERY", "AVG"},
        {"solarPanelVoltage", "DUMMY", "AVG"},
        {"evapotranspiration", "DUMMY", "AVG"}
    };

    public WeatherData getWeatherData(Integer stationID, String userName, String password, LocalDate startDate, LocalDate endDate) throws ParseWeatherDataException {
        try {
            // First we get the location of the weather station
            String method = "Locate";
            URL meteobotURL = new URL(MessageFormat.format(MeteobotAPIAdapter.METEOS_URL_TEMPLATE, method, stationID, startDate, endDate));
            URLConnection connection = meteobotURL.openConnection();
            String userpass = userName + ":" + password;
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
            connection.setRequestProperty("Authorization", basicAuth);
            BufferedReader in;
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            Double latitude = 0.0;
            Double longitude = 0.0;
            while ((inputLine = in.readLine()) != null) {
                String[] lineParts = inputLine.split(";");
                try
                {
                    Long stationId = Long.valueOf(lineParts[0]);
                    latitude = Double.valueOf(lineParts[2]);
                    longitude = Double.valueOf(lineParts[3]);
                }
                catch(NumberFormatException ex) {}
            }

            // Then get the weather data and convert to IPM Decisions format
            return this.weatherUtils.getWeatherDataFromVIPSWeatherObservations(
                    this.getVIPSWeatherObservations(stationID, userName, password, startDate, endDate),
                    longitude,
                    latitude,
                    0);
        } catch (IOException ex) {
            throw new ParseWeatherDataException(ex.getMessage());
        }
    }

    public List<VIPSWeatherObservation> getVIPSWeatherObservations(Integer stationID, String userName, String password, LocalDate startDate, LocalDate endDate) throws ParseWeatherDataException {
        // Method Index fetches weather data, method Locate returns station's lat/lon coordinates
        String method = "Index";
        List<VIPSWeatherObservation> retVal = new ArrayList<>();

        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        dFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        List<String[]> data = new ArrayList<>();

        String[] headers;
        Map<Integer, Integer> elementOrdering = new HashMap<>();

        //distinquish between Locate and Index
        try {
            URL meteobotURL = new URL(MessageFormat.format(MeteobotAPIAdapter.METEOS_URL_TEMPLATE, method, stationID, startDate, endDate));

            URLConnection connection = meteobotURL.openConnection();
            String userpass = userName + ":" + password;
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
            connection.setRequestProperty("Authorization", basicAuth);

            BufferedReader in;
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                Date testTimestamp = null;
                String[] lineData = inputLine.split(";");

                // Skip empty lines
                if (lineData.length <= 1) {
                    continue;
                }

                // Check for valid start of line
                try {
                    testTimestamp = dFormat.parse(lineData[1]);
                    data.add(lineData);
                } catch (ParseException ex) {
                }

                // Is this the heading line?
                // Then we parse it to set the ordering of elements
                if (lineData[1].equals("time")) {
                    headers = lineData;
                    // ID, date and time should always be the three first
                    for (int i = 2; i < lineData.length; i++) {
                        for (int j = 0; j < elementMeasurementTypes.length; j++) {
                            if (elementMeasurementTypes[j][0].equals(lineData[i])) {
                                elementOrdering.put(i, j);
                            }
                        }
                    }
                }
            }
            in.close();
        } catch (IOException ex) {
            throw new ParseWeatherDataException(ex.getMessage());
        }

        Date timestamp = null;

        for (String[] lineData : data) {

            try {
                timestamp = dFormat.parse(lineData[1]);
            } catch (ParseException ex) {
                throw new ParseWeatherDataException("Error with time stamp in weather data from Meteobot station: " + ex.getMessage());
            }

            try {
                if (method.equals("Index")) {
                    // If minute != 00 we skip the line
                    if (!lineData[1].split(":")[1].equals("00")) {
                        continue;
                    }

                    for (Integer i = 2; i < lineData.length; i++) {
                        Integer elementMeasurementTypeIndex = elementOrdering.get(i);

                        // This means there is an element type we don't collect
                        if (elementMeasurementTypeIndex == null || lineData[i].isEmpty()) {
                            continue;
                        }

                        Double value = Double.valueOf(lineData[i]);

                        VIPSWeatherObservation obs = new VIPSWeatherObservation();
                        obs.setTimeMeasured(timestamp);
                        obs.setLogIntervalId(VIPSWeatherObservation.LOG_INTERVAL_ID_1H);
                        obs.setElementMeasurementTypeId(MeteobotAPIAdapter.elementMeasurementTypes[elementMeasurementTypeIndex][1]);
                        obs.setValue(value);
                        retVal.add(obs);
                    }
                } else if (method.equals("Locate")) // Not in use in our code for the time being
                {
                    for (Integer i = 2; i < lineData.length; i++) {
                        if (lineData[i].isEmpty()) {
                            continue;
                        }
                        Double value = Double.valueOf(lineData[i]);
                    }
                }
            } catch (Exception ex) {
                throw new ParseWeatherDataException("Error, invalid method: " + ex.getMessage());
            }
        }
        return retVal;
    }

    public MeteobotAPIAdapter() {
        this.weatherUtils = new WeatherUtils();
    }
}
