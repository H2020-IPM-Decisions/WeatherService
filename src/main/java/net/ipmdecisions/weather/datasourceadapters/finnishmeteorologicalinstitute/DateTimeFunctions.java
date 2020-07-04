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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;

/**
 * 
 * @author Markku Koistinen <markku.koistinen@luke.fi>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class DateTimeFunctions {
    
    public DateTimeFunctions() {}
    
    /**
     * Default dateTime format yyyy-MM-dd HH:mm:ss
     * @param dateTimeFormat
     * @return 
     */
    public String getNow(String dateTimeFormat, String timeZone) {
        String response = "";
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);
    	sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        response =  sdf.format(new Date());
        return response;
    }
    
    public String getNowPlusDays(String dateTimeFormat, String timeZone, int days) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);
    	sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, days);
        return sdf.format(cal.getTime());
    }
    
    public String getDatePlusDays(String dateTimeFormat, String timeZone, String startDate, int days) {
        
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);
    	sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        Calendar cal = Calendar.getInstance();
        Date date = null;
        try {
            date = sdf.parse(startDate);
        } catch (Exception e){}
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        
        return sdf.format(cal.getTime());
    }
    
    public String getDatePlusDays_UTC(String dateTimeFormat, String timeZone, String startDate, int days) {
        
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);
    	sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        Calendar cal = Calendar.getInstance();
        Date date = null;
        try {
            date = sdf.parse(startDate);
        } catch (Exception e){}
        cal.setTime(date);
        TimeZone tz = TimeZone.getTimeZone("UTC");
        cal.setTimeZone(tz);
        cal.add(Calendar.DATE, days);
        
        return sdf.format(cal.getTime());
    }
    
    public long getDateTimeDiffInDays_UTC(String lDateTime, String uDateTime) {
        long diffInDays = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            Date lDate = sdf.parse(lDateTime);
            Date uDate = sdf.parse(uDateTime);
	
            Calendar lCalendar = Calendar.getInstance();
            Calendar uCalendar = Calendar.getInstance();
            
            TimeZone tz = TimeZone.getTimeZone("UTC");
            lCalendar.setTimeZone(tz);
            uCalendar.setTimeZone(tz);
            
            lCalendar.setTime(lDate);
            uCalendar.setTime(uDate);
            
            long lDTms = lCalendar.getTimeInMillis();
            long uDTms = uCalendar.getTimeInMillis();
            
            diffInDays = ((uDTms/1000) - (lDTms/1000))/(60*60*24);
        } catch (Exception e) {}
        return diffInDays;
    }
    
    public long getDateTimeDiffInMinutes_UTC(String lDateTime, String uDateTime) {
        long diffInMinutes = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            Date lDate = sdf.parse(lDateTime);
            Date uDate = sdf.parse(uDateTime);
	
            Calendar lCalendar = Calendar.getInstance();
            Calendar uCalendar = Calendar.getInstance();
            
            TimeZone tz = TimeZone.getTimeZone("UTC");
            lCalendar.setTimeZone(tz);
            uCalendar.setTimeZone(tz);
            
            lCalendar.setTime(lDate);
            uCalendar.setTime(uDate);
            
            long lDTms = lCalendar.getTimeInMillis();
            long uDTms = uCalendar.getTimeInMillis();
            
            diffInMinutes = ((uDTms/1000) - (lDTms/1000))/60;
        } catch (Exception e) {}
        return diffInMinutes;
    }
    
    public String getNowStandard() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return sdf.format(cal.getTime());
    }
    
    public String getNowStandardWithMilliseconds() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return sdf.format(cal.getTime());
    }
    
    public String getNowPlusOneMonthStandard() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, 1);
        return sdf.format(cal.getTime());
    }
    
    public String[] getAvailabeTimeZoneIDs() {
        return TimeZone.getAvailableIDs();
    }
    
    public String getNowMinusOneHour(String dateTimeFormat) {
        String response = "";
        return response;
    }
    
    public String getUnixTimeAsISO(long unixTime) {
        String response;
        unixTime = unixTime * 1000L;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        response = sdf.format(new Date(unixTime));
        return response;
    }
    
    public String getNowPlusMilliseconds(String dateTime, int milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateTimeAsDateTime = null;
        try {
            dateTimeAsDateTime= sdf.parse(dateTime);
        } catch (Exception e) {}
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTimeAsDateTime);
        cal.add(Calendar.MILLISECOND, milliseconds);
        return sdf.format(cal.getTime());
    }
    
    public String getPreviousOrNextFullTenMinutes (String dateTime) {
        String response = "";
        dateTime = dateTime.substring(0, 17);
        dateTime = dateTime + "00";
        String timeAsString = dateTime.substring(11);
        String minutesAsString = timeAsString.substring(3,5);
        String minuteAsString = timeAsString.substring(4,5);
        int minutesAsInt = Integer.parseInt(minutesAsString);
        int minuteAsInt = Integer.parseInt(minuteAsString);
        int timeDiffInMs = 0;
        if (minuteAsInt < 5) {
            minutesAsInt = minutesAsInt - minuteAsInt;
            timeDiffInMs = (0 - minuteAsInt) * 1000 * 60;
        } else {
            minutesAsInt = minutesAsInt + (10-minuteAsInt);
            timeDiffInMs = (10 - minuteAsInt) * 1000 * 60;
        }
        //System.out.println(minutesAsInt);
        //System.out.println(timeDiffInMs);
        //String secondsAsString  = timeAsString.substring(6,8);
        response = getNowPlusMilliseconds(dateTime, timeDiffInMs);
        return response;
    }
    
    public int compareDates (String lDate, String uDate) {
        int response = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lDateAsDate;
        Date uDateAsDate;
        try {
            lDateAsDate = formatter.parse(lDate);
            uDateAsDate = formatter.parse(uDate);
            if (lDateAsDate.before(uDateAsDate) == true) {
                response = 1;
            }
        } catch (Exception e) {}
        return response;
    }
    
    public long getDateTimeDiffInDays(String lDateTime, String uDateTime) {
        long diffInDays = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            Date lDate = sdf.parse(lDateTime);
            Date uDate = sdf.parse(uDateTime);
	
            Calendar lCalendar = Calendar.getInstance();
            Calendar uCalendar = Calendar.getInstance();
            
            lCalendar.setTime(lDate);
            uCalendar.setTime(uDate);
            
            long lDTms = lCalendar.getTimeInMillis();
            long uDTms = uCalendar.getTimeInMillis();
            
            diffInDays = ((uDTms/1000) - (lDTms/1000))/(60*60*24);
        } catch (Exception e) {}
        return diffInDays;
    }
    
    public long getDateTimeDiffInMinutes(String lDateTime, String uDateTime) {
        long diffInMinutes = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            Date lDate = sdf.parse(lDateTime);
            Date uDate = sdf.parse(uDateTime);
	
            Calendar lCalendar = Calendar.getInstance();
            Calendar uCalendar = Calendar.getInstance();
            
            lCalendar.setTime(lDate);
            uCalendar.setTime(uDate);
            
            long lDTms = lCalendar.getTimeInMillis();
            long uDTms = uCalendar.getTimeInMillis();
            
            diffInMinutes = ((uDTms/1000) - (lDTms/1000))/60;
        } catch (Exception e) {}
        return diffInMinutes;
    }
    
    public long getDateTimeInMilliseconds(String dateTimeAsString, String dateTimeFormat) {
        long dateTimeInMilliseconds = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);
            Date dateTimeAsDate = sdf.parse(dateTimeAsString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTimeAsDate);
            dateTimeInMilliseconds = calendar.getTimeInMillis();
        } catch (Exception e) {}
        return dateTimeInMilliseconds;
    }
    
}
