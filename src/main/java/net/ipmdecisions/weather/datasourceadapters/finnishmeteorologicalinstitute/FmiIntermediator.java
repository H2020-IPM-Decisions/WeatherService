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

/**
 * 
 * @author Markku Koistinen <markku.koistinen@luke.fi>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class FmiIntermediator {
    
    public FmiIntermediator() {}
    
    public String getTemporalData_fmi(String fmiSid, String startDateTime, String endDateTime) {
        String response = "";
        FmiOpenDataAccess fmiAccess = new FmiOpenDataAccess();
        startDateTime = startDateTime.replace("T", " ");
        startDateTime = startDateTime.replace("Z", "");
        endDateTime = endDateTime.replace("T", " ");
        endDateTime = endDateTime.replace("Z", "");
        response = fmiAccess.getTemporalData(fmiSid, startDateTime, endDateTime);
        //System.out.println(fmiSid);
        //System.out.println(startDateTime);
        //System.out.println(endDateTime);
        return response;
    }
    
    public String getTemporalData_vips(String fmiSid, String startDateTime, String endDateTime) {
        String response = "";
        FmiOpenDataAccess fmiAccess = new FmiOpenDataAccess();
        startDateTime = startDateTime.replace("T", " ");
        startDateTime = startDateTime.replace("Z", "");
        endDateTime = endDateTime.replace("T", " ");
        endDateTime = endDateTime.replace("Z", "");
        response = fmiAccess.getTemporalData_prototype(fmiSid, startDateTime, endDateTime);
        //System.out.println(fmiSid);
        //System.out.println(startDateTime);
        //System.out.println(endDateTime);
        return response;
    }
    
}
