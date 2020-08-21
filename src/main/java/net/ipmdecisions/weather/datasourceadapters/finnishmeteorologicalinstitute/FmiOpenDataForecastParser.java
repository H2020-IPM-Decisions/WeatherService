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

import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.ipmdecisions.weather.util.vips.VIPSWeatherObservation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Converts FMI XML string to VIPS type Weather observations
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class FmiOpenDataForecastParser {
    public List<VIPSWeatherObservation> getVIPSWeatherObservations(String FMIXMLString){
        List<VIPSWeatherObservation> retVal = new ArrayList<>();
        try
        {
            // Build the DOM structure from XML string
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(FMIXMLString));
            Document doc = dBuilder.parse(is);
            
            // The included parameters are given in correct order in these elements
            // As per the Java documentation the elements are arranged in the document's order
            // https://docs.oracle.com/en/java/javase/11/docs/api/java.xml/org/w3c/dom/Document.html#getElementsByTagName(java.lang.String)
            NodeList parameterSet = doc.getElementsByTagName("swe:field");
            // The epoch timestamps (and positions) are given as lines in this element
            String[] positionsAndTimeStamps = doc.getElementsByTagName("gmlcov:positions").item(0).getTextContent().split("\\r?\\n");
            // The corresponding values are given as columns in rows(lines) in this element
            String[] valueLines = doc.getElementsByTagName("gml:doubleOrNilReasonTupleList").item(0).getTextContent().split("\\r?\\n");
            
            // Run through each timestamp
            for(int i=0;i<positionsAndTimeStamps.length;i++)
            {
                if(positionsAndTimeStamps[i].trim().isBlank() || valueLines[i].contains("NaN"))
                {
                    continue;
                }
                
                                
                String[] rawValues = valueLines[i].trim().split("\\s+");
                // Iterate the parameters, create new WeatherObservation for each of them
                for(int j=0; j<parameterSet.getLength();j++)
                {
                    VIPSWeatherObservation o = new VIPSWeatherObservation();
                    Element e = (Element) parameterSet.item(j );
                    String paramName = e.getAttribute("name");
                    switch(paramName){
                        case "Temperature":
                            o.setElementMeasurementTypeId("1002");
                            break;
                        case "Humidity":
                            o.setElementMeasurementTypeId("3002");
                            break;
                        case "WindSpeedMS":
                            o.setElementMeasurementTypeId("4002");
                            break;
                        case "DewPoint":
                            o.setElementMeasurementTypeId("1901");
                            break;
                        case "Precipitation1h":
                            o.setElementMeasurementTypeId("2001");
                            break;
                        case "radiationglobal":
                            o.setElementMeasurementTypeId("5001");
                            break;
                        default:
                            continue;
                    }

                    o.setValue(Double.valueOf(rawValues[j]));
                    o.setLogIntervalId(VIPSWeatherObservation.LOG_INTERVAL_ID_1H);
                    o.setTimeMeasured(Date.from(Instant.ofEpochSecond(Long.valueOf(positionsAndTimeStamps[i].trim().split("\\s+")[2]))));
                    retVal.add(o);
                }
                
            }
            
        }
        catch(SAXException | IOException | ParserConfigurationException ex)
        {
            ex.printStackTrace();
            return null;
        }
        return retVal;
    }
}
