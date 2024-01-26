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

package net.ipmdecisions.weather.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;

/**
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class WeatherDataUtil {
    
    public final static Integer INTERVAL_HOURLY = 3600;
    public final static Integer INTERVAL_DAILY = 86400;
    
    /**
     * Returns the weather data set with only the given parameters
     * @param source
     * @param ipmDecisionsParameters
     * @return 
     */
    public WeatherData filterParameters(WeatherData source, Set<Integer> requestedParameters)
    {
        // Figure out which parameters to keep - but register by index
        List<Integer> paramIndexesToKeep = new ArrayList<>();
        List<Integer> paramsToKeep = new ArrayList<>();
        for(int i=0; i< source.getWeatherParameters().length; i++)
        {
            if(requestedParameters.contains(source.getWeatherParameters()[i]))
            {
                paramsToKeep.add(source.getWeatherParameters()[i]);
                paramIndexesToKeep.add(i);
            }
        }

        Integer[] filteredQC = new Integer[paramIndexesToKeep.size()];
        
        source.getLocationWeatherData().forEach(lwd -> {
        	//System.out.println("lwd.getQC().length=" + lwd.getQC().length);
        	for(int i=0; i<paramIndexesToKeep.size();i++)
            {
                filteredQC[i] = lwd.getQC()[paramIndexesToKeep.get(i)];
            }
            lwd.setQC(filteredQC);
            Double[][] filteredData = new Double[lwd.getLength()][paramsToKeep.size()];
            for(int i=0;i<lwd.getLength();i++)
            {
                for(int j=0; j<paramIndexesToKeep.size();j++)
                {
                    filteredData[i][j] = lwd.getData()[i][paramIndexesToKeep.get(j)];
                }
            }
            lwd.setData(filteredData);
            
        });
        
        source.setWeatherParameters(paramsToKeep.toArray(new Integer[paramsToKeep.size()]));
        
        return source;
        
    }
    
    public WeatherData trimDataSet(WeatherData source)
	{
    	//System.out.println(source.getLocationWeatherData().get(0).toString());
		// Find min and max index with data
		Integer min=source.getLocationWeatherData().get(0).getLength(), max=0;
		for(LocationWeatherData lwd:source.getLocationWeatherData())
		{
			// Minimum
			OUTER1:
			for(int i=0;i<lwd.getLength();i++)
			{
				Double[] row = lwd.getData()[i];
				for(int col=0;col<row.length;col++)
				{
					if(row[col] != null)
					{
						min = Math.min(i, min);
						break OUTER1;
					}
				}
			}
			// Maximum
			OUTER2:
			for(int i=lwd.getLength()-1; i>=0; i--)
			{
				//System.out.println("i=" + i);
				Double[] row = lwd.getData()[i];
				for(int col=0;col<row.length;col++)
				{
					if(row[col] != null)
					{
						
						max = Math.max(i, max);
						break OUTER2;
					}
				}
			}
		}
		
		Integer rowsToChopAtEnd = source.getLocationWeatherData().get(0).getLength() - max;
		
		/*
		System.out.println("Min=" + min + ", max=" + max);
		System.out.println("missing at end=" + rowsToChopAtEnd);
		System.out.println("Chopping off this amount of seconds: " + (rowsToChopAtEnd * source.getInterval()));
		System.out.println("Original timeEnd=" + source.getTimeEnd());
		System.out.println("Calculated timeEnd=" + source.getTimeEnd().minus(rowsToChopAtEnd * source.getInterval(), ChronoUnit.SECONDS));
		*/
		
		// Adjust timeStart and timeEnd
		source.setTimeStart(source.getTimeStart().plus(min * source.getInterval(), ChronoUnit.SECONDS));
		source.setTimeEnd(source.getTimeEnd().minus(rowsToChopAtEnd * source.getInterval(), ChronoUnit.SECONDS));
		
		
		Integer newLength = min < source.getLocationWeatherData().get(0).getLength() ?  1 + max - min : 0;
		Integer cols = source.getWeatherParameters().length;
		for(LocationWeatherData lwd:source.getLocationWeatherData())
		{
			Double[][] newData = new Double[newLength][cols];
			Integer newRowIndex = 0;
			for(int row = min; row <= max; row++)
			{
				for(int col=0;col<cols;col++)
				{
					newData[newRowIndex][col] = lwd.getData()[row][col];
				}
				newRowIndex++;
			}
			lwd.setData(newData);
		}
		

		return source;
	}
    
    public String serializeWeatherData(WeatherData weatherData)
    {
        ObjectMapper om = new ObjectMapper();
        JavaTimeModule javaTimeModule =  new JavaTimeModule();
            om.registerModule(javaTimeModule);
        try
        {
            return(om.writeValueAsString(weatherData));
        }
        catch(JsonProcessingException ex)
        {
            return ex.getMessage();
        }
    }

}
