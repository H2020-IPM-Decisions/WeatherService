/*
 * Copyright (c) 2021 NIBIO <http://www.nibio.no/>. 
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

package net.ipmdecisions.weather.datasourceadapters.dmi;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import net.ipmdecisions.weather.datasourceadapters.dmi.generated.ArrayOfWeatherDataModel;
import net.ipmdecisions.weather.datasourceadapters.dmi.generated.WeatherDataModel;
import net.ipmdecisions.weather.datasourceadapters.dmi.generated.IWeatherService;
import net.ipmdecisions.weather.datasourceadapters.dmi.generated.WeatherDataParameter;
import net.ipmdecisions.weather.datasourceadapters.dmi.generated.WeatherDataSource;
import net.ipmdecisions.weather.datasourceadapters.dmi.generated.WeatherInterval;
import net.ipmdecisions.weather.datasourceadapters.dmi.generated.WeatherResponse;
import net.ipmdecisions.weather.datasourceadapters.dmi.generated.WeatherService;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;


/**
 * Gets data from the Danish Meteorological Institute's Point Web service
 * Source files (except this and the others in the same package) have been auto generated using this command:
 * <code>wsimport -keep -Xnocompile -p net.ipmdecisions.weather.datasourceadapters.dmi.generated  https://dmiweatherservice-plant.dlbr.dk/DMIWeatherService.svc?wsdl</code>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class DMIPointWebDataParser {

    private static final Map<Integer, WeatherDataParameter> PARAM_MAP;
    static
    {
        PARAM_MAP = new HashMap<>();
        PARAM_MAP.put(1002, WeatherDataParameter.AIRTEMP);
        PARAM_MAP.put(2001, WeatherDataParameter.PREC);
        PARAM_MAP.put(3002, WeatherDataParameter.AIRRH);
        PARAM_MAP.put(4002, WeatherDataParameter.WINDSPEED);
        PARAM_MAP.put(1112, WeatherDataParameter.SOILTEMP);
        PARAM_MAP.put(3101, WeatherDataParameter.LEAFWET);
    }
    
    private final TimeZone danishTZ = TimeZone.getTimeZone("GMT+1");
    
    /**
     * 
     * @param longitude WGS84 Decimal degrees
     * @param latitude WGS84 Decimal degrees
     * @param dateFrom 
     * @param dateTo
     * @param weatherInterval hourly or daily
     * @return
     * @throws DatatypeConfigurationException 
     */
    public WeatherData getData(Double longitude, Double latitude, Date dateFrom, Date dateTo, Integer interval) throws DatatypeConfigurationException
    {


            IWeatherService proxy = new WeatherService().getSslOffloadedBasicHttpBindingIWeatherService();
            UseableArrayOfWeatherDataSource wdsource = new UseableArrayOfWeatherDataSource();
            wdsource.add(WeatherDataSource.OBS);
            wdsource.add(WeatherDataSource.FORECAST);
            
            GregorianCalendar gc = new GregorianCalendar(danishTZ);
            gc.setTime(dateFrom);
            XMLGregorianCalendar calFrom = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            XMLGregorianCalendar calTo = null;
            if(dateTo != null)
            {
               gc.setTime(dateTo);
               calTo = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            }

            UseableArrayOfWeatherDataParameter wdparam = new UseableArrayOfWeatherDataParameter();
            DMIPointWebDataParser.PARAM_MAP.values().stream().forEach(
                    listedParam -> wdparam.add(listedParam)
            );
            
            WeatherResponse result = proxy.getWeatherDataExtended(
                String.valueOf(latitude).replace(".", ","), // Latitude ("X") Decimal separator is comma!
                String.valueOf(longitude).replace(".", ","), // Longitude ("Y") Decimal separator is comma!
                false, // true if UTM, false if decimal degrees
                null, // UTM Zone if using UTM
                interval == 3600 ? WeatherInterval.HOUR : WeatherInterval.DAY, // Hourly or Daily data
                wdsource, // Set of data sources
                calFrom, // Start of period with data
                calTo, // End of period with data
                wdparam, // Set of requested parameters
                0.0, // Base temperature value
                false, // Use base temperature? (t/f)
                true, // Get 6 days of forecasts (only for the proxy.getWeatherDataExtended method)
                "IPMDecisions", // Client info for the service
                "IPMDecisions" // Identification
            );
            
            ArrayOfWeatherDataModel value = result.getWeahterDataList().getValue();
            
            WeatherData retVal = new WeatherData();
            // Get the start and end times of the list
            // Collect the parameters as well
            Date timeStart = null;
            Date timeEnd = null;
            Set<Integer> foundParameters = new HashSet<>();
            Integer[] potentialParameters = {1002,1112,2001,3002,3101,4002};
            if(value.getWeatherDataModel().isEmpty())
            {
                return null;
            }
            for(WeatherDataModel wdm: value.getWeatherDataModel())
            {
            	Date obsTime = wdm.getDateDay().toGregorianCalendar(danishTZ, null, null).getTime();
            	if(timeStart == null || timeStart.after(obsTime))
            	{
            		timeStart = obsTime;
            	}
            	
            	if(timeEnd == null || timeEnd.before(obsTime))
            	{
            		timeEnd = obsTime;
            	}
            	
            	for(Integer potParam : potentialParameters)
            	{
            		if(this.getValueForParameter(wdm, potParam) != null)
            		{
            			foundParameters.add(potParam);
            		}
            	}
            }
            
           
            retVal.setTimeStart(timeStart.toInstant());
            retVal.setTimeEnd(timeEnd.toInstant());
            retVal.setWeatherParameters(foundParameters.stream().toArray(Integer[] ::new));
            retVal.setInterval(interval); // Hourly or daily data
            
            // Dim the data array, create LocationWeatherData etc.
            // Loop again, place stuff in the data array
            Long startingPoint = retVal.getTimeStart().getEpochSecond();
            Long endingPoint = retVal.getTimeEnd().getEpochSecond();
            Integer rows = 1 + (int) (endingPoint - startingPoint) / retVal.getInterval();
            LocationWeatherData lwd = new LocationWeatherData(longitude, latitude, 0.0, rows, retVal.getWeatherParameters().length);
            
            value.getWeatherDataModel().forEach(wdm -> {
                Date obsTime = wdm.getDateDay().toGregorianCalendar(danishTZ, null, null).getTime();
                Integer row = (int) ((obsTime.getTime()/1000 - startingPoint) / retVal.getInterval());
                for(int i=0;i<retVal.getWeatherParameters().length;i++)
                {
                    lwd.getData()[row][i] = this.getValueForParameter(wdm, retVal.getWeatherParameters()[i]);
                }
            });
            retVal.addLocationWeatherData(lwd);
            return retVal;
    }
    
    
    
    private Double getValueForParameter(WeatherDataModel wDataModel, Integer ipmParam)
    {
    	switch(ipmParam) {
    		case 1002:
    			return wDataModel.getAirtemp() != null ? wDataModel.getAirtemp().getValue() : null;
    		case 1112:
    			return wDataModel.getSoiltemp() != null ? wDataModel.getSoiltemp().getValue() : null;
    		case 2001:
    			return wDataModel.getPrec() != null ? wDataModel.getPrec().getValue() : null;
    		case 3002:
    			return wDataModel.getAirrh() != null ? wDataModel.getAirrh().getValue() : null;
    		case 3101:
    			return wDataModel.getLeafwet() != null ? wDataModel.getLeafwet().getValue() : null;
    		case 4002:
    			return wDataModel.getWindspeed() != null ? wDataModel.getWindspeed().getValue() : null;
    		default:
    			return null;
    	}
    }

}
