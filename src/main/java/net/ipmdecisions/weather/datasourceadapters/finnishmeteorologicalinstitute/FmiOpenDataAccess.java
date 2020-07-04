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

import java.io.*;
import java.net.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * @author Markku Koistinen <markku.koistinen@luke.fi>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class FmiOpenDataAccess {
    
    public FmiOpenDataAccess() {
        
    }
    
    //"http://data.fmi.fi/fmi-apikey/insert-your-apikey-here/wfs?request=getFeature&storedquery_id=fmi::forecast::hirlam::surface::point::multipointcoverage&place=[PLACE_NAME]&endtime=yyyy-MM-ddTHH:mm:ssZ"
    public String get24hrsForecast() {
        String response = "";
        
        DateTimeFunctions dateTime = new DateTimeFunctions();
        String fmiDateTime = dateTime.getNowPlusDays("yyyy-MM-dd HH:mm:ss", "UTC", 1);
        fmiDateTime = fmiDateTime.replace(" ", "T");
        fmiDateTime += "Z";
        //System.out.println(fmiDateTime);
        //String url = "http://data.fmi.fi/fmi-apikey/49804794-1f38-46f0-adad-bb3d1aae5ffa/wfs?request=getFeature&storedquery_id=fmi::forecast::hirlam::surface::point::multipointcoverage&place=Vihti&endtime" + fmiDateTime;
        String url = "http://opendata.fmi.fi/wfs?request=getFeature&storedquery_id=fmi::forecast::hirlam::surface::point::multipointcoverage&place=Vihti&endtime" + fmiDateTime;
        response = restGet(url);
        return response;
    }
    
    /**
     * Returns the data from FMI as VIPS Json Data,
     * BUT with position info for the site as a prefix
     * @param siteID
     * @param startDateTime
     * @param endDateTime
     * @return 
     */
    public String getTemporalData_prototype(String siteID, String startDateTime, String endDateTime) {
        String response = "";
        String url;
        DateTimeFunctions dateTimeFunctions = new DateTimeFunctions();
        startDateTime = dateTimeFunctions.getPreviousOrNextFullTenMinutes(startDateTime);
        endDateTime = dateTimeFunctions.getPreviousOrNextFullTenMinutes(endDateTime);
        String temporalFactor = startDateTime.substring(14, 16);
        //DateTime difference in days
        long dateTimeDiffInDays;
        dateTimeDiffInDays = dateTimeFunctions.getDateTimeDiffInDays_UTC(startDateTime, endDateTime);
        //DateTime difference in minutes
        long dateTimeDiffInMinutes;
        dateTimeDiffInMinutes = dateTimeFunctions.getDateTimeDiffInMinutes_UTC(startDateTime, endDateTime);        
        
        //System.out.println("Days: " + dateTimeDiffInDays);
        //System.out.println("Minutes: " + dateTimeDiffInMinutes);
        
        //Iterate if time difference is more than 7 days
        String newEndDateTime;
        double differenceFactor;
        int iterationCount;
        String startDateISO;
        String endDateISO;
        String fmiResponse;
        FmiOpenDataParser fmiParser = new FmiOpenDataParser();
        if (dateTimeDiffInMinutes > 10080) {
            differenceFactor = (double)dateTimeDiffInMinutes / 10080;
            //System.out.println(differenceFactor);
             iterationCount = (int)Math.ceil(differenceFactor);
            //System.out.println(iterationCount + " iterations");
            for (int i=0; i<iterationCount-1; i++) {
                startDateISO = startDateTime.replace(" ", "T");
                startDateISO += "Z";
                newEndDateTime = dateTimeFunctions.getDatePlusDays_UTC("yyyy-MM-dd HH:mm:ss", "UTC", startDateTime, 7);
                endDateISO = newEndDateTime.replace(" ", "T");
                endDateISO += "Z";
                //url = "http://data.fmi.fi/fmi-apikey/49804794-1f38-46f0-adad-bb3d1aae5ffa/wfs?request=getFeature&storedquery_id=fmi::observations::weather::multipointcoverage&fmisid=" + siteID + "&maxlocations=1&starttime=" + startDateISO + "&endtime=" + endDateISO;
                url = "http://opendata.fmi.fi/wfs?request=getFeature&storedquery_id=fmi::observations::weather::multipointcoverage&fmisid=" + siteID + "&maxlocations=1&starttime=" + startDateISO + "&endtime=" + endDateISO;
                startDateTime = newEndDateTime;
                fmiResponse = restGet(url);
                response += fmiParser.getAsJSON_prototype(fmiResponse, temporalFactor);
            }
        }
        
        //System.out.println("Iteration done. Last query initialized...");
        startDateISO = startDateTime.replace(" ", "T");
        startDateISO += "Z";
        endDateISO = endDateTime.replace(" ", "T");
        endDateISO += "Z";
        //url = "http://data.fmi.fi/fmi-apikey/49804794-1f38-46f0-adad-bb3d1aae5ffa/wfs?request=getFeature&storedquery_id=fmi::observations::weather::multipointcoverage&fmisid=" + siteID + "&maxlocations=1&starttime=" + startDateISO + "&endtime=" + endDateISO;
        url = "http://opendata.fmi.fi/wfs?request=getFeature&storedquery_id=fmi::observations::weather::multipointcoverage&fmisid=" + siteID + "&maxlocations=1&starttime=" + startDateISO + "&endtime=" + endDateISO;
        fmiResponse = restGet(url);
        response += fmiParser.getAsJSON_prototype(fmiResponse, temporalFactor);
        response = response.replace("][", ", ");
        
        // Add positional data as header
        String stationPositionData = this.getStationPositionData(fmiResponse);
        //System.out.println(stationPositionData + "IPMDECISIONS" + response);
        return stationPositionData + "IPMDECISIONS" + response;
    }
    
    public String getStationPositionData(String FMIXML)
    {
        try
        {
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(FMIXML));
            Document doc = dBuilder.parse(is);
            return doc.getElementsByTagName("gml:pos").item(0).getTextContent();
        }catch(ParserConfigurationException | IOException | SAXException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    
    public String getTemporalData(String siteID, String startDateTime, String endDateTime) {
        String response = "";
        String url;
        DateTimeFunctions dateTimeFunctions = new DateTimeFunctions();
        startDateTime = dateTimeFunctions.getPreviousOrNextFullTenMinutes(startDateTime);
        endDateTime = dateTimeFunctions.getPreviousOrNextFullTenMinutes(endDateTime);
        String temporalFactor = startDateTime.substring(14, 16);
        //DateTime difference in days
        long dateTimeDiffInDays;
        dateTimeDiffInDays = dateTimeFunctions.getDateTimeDiffInDays_UTC(startDateTime, endDateTime);
        //DateTime difference in minutes
        long dateTimeDiffInMinutes;
        dateTimeDiffInMinutes = dateTimeFunctions.getDateTimeDiffInMinutes_UTC(startDateTime, endDateTime);        
        
        //System.out.println("Days: " + dateTimeDiffInDays);
        //System.out.println("Minutes: " + dateTimeDiffInMinutes);
        
        //Iterate if time difference is more than 7 days
        String newEndDateTime;
        double differenceFactor;
        int iterationCount;
        String startDateISO;
        String endDateISO;
        String fmiResponse;
        FmiOpenDataParser fmiParser = new FmiOpenDataParser();
        if (dateTimeDiffInMinutes > 10080) {
            differenceFactor = (double)dateTimeDiffInMinutes / 10080;
            //System.out.println(differenceFactor);
            iterationCount = (int)Math.ceil(differenceFactor);
            //System.out.println(iterationCount + " iterations");
            for (int i=0; i<iterationCount-1; i++) {
                //System.out.println("Start: " + startDateTime);
                startDateISO = startDateTime.replace(" ", "T");
                startDateISO += "Z";
                newEndDateTime = dateTimeFunctions.getDatePlusDays_UTC("yyyy-MM-dd HH:mm:ss", "UTC", startDateTime, 7);
                endDateISO = newEndDateTime.replace(" ", "T");
                endDateISO += "Z";
                //System.out.println("End: " + newEndDateTime);
                //url = "http://data.fmi.fi/fmi-apikey/49804794-1f38-46f0-adad-bb3d1aae5ffa/wfs?request=getFeature&storedquery_id=fmi::observations::weather::multipointcoverage&fmisid=" + siteID + "&maxlocations=1&starttime=" + startDateISO + "&endtime=" + endDateISO;
                url = "http://opendata.fmi.fi/wfs?request=getFeature&storedquery_id=fmi::observations::weather::multipointcoverage&fmisid=" + siteID + "&maxlocations=1&starttime=" + startDateISO + "&endtime=" + endDateISO;
                startDateTime = newEndDateTime;
                fmiResponse = restGet(url);
                response += fmiParser.getAsJSON(fmiResponse, temporalFactor);
            }
        }
        
        startDateISO = startDateTime.replace(" ", "T");
        startDateISO += "Z";
        endDateISO = endDateTime.replace(" ", "T");
        endDateISO += "Z";
        //url = "http://data.fmi.fi/fmi-apikey/49804794-1f38-46f0-adad-bb3d1aae5ffa/wfs?request=getFeature&storedquery_id=fmi::observations::weather::multipointcoverage&fmisid=" + siteID + "&maxlocations=1&starttime=" + startDateISO + "&endtime=" + endDateISO;
        url = "http://opendata.fmi.fi/wfs?request=getFeature&storedquery_id=fmi::observations::weather::multipointcoverage&fmisid=" + siteID + "&maxlocations=1&starttime=" + startDateISO + "&endtime=" + endDateISO;
        fmiResponse = restGet(url);
        response += fmiParser.getAsJSON(fmiResponse, temporalFactor);
        response = response.replace("][", ", ");
        return response;
    }
    
    private String restGet(String url) {
        String response = "";
        
        String responseLine = "";
        
        try {
            URL urli = new URL(url);
            URLConnection connection=urli.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) connection;
            
            //byte[] b = soapRequest.getBytes();
            
            //httpConn.setRequestProperty( "Content-Length", String.valueOf( b.length ) );
            //httpConn.setRequestProperty("Accept","application/json");
            //httpConn.setRequestProperty("Content-Type","text/plain; charset=UTF-8");
            httpConn.setRequestMethod( "GET" );
            //httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            
            //OutputStream out = httpConn.getOutputStream();
            //out.write( b );
            //out.close();
        
            InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
        
            BufferedReader in = new BufferedReader(isr);
            
            while ((responseLine = in.readLine()) != null) {
                response = response + responseLine;
            }
            in.close();
            
        } catch (Exception e) { System.out.println(e.toString()); }
        
        return response;
    }
    
}
