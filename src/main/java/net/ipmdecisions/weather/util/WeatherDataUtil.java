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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;

/**
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class WeatherDataUtil {
    
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
            Double[][] filteredData = new Double[lwd.getLength()][paramsToKeep.size()];
            for(int i=0;i<lwd.getLength();i++)
            {
                for(int j=0; j<paramIndexesToKeep.size();j++)
                {
                    filteredData[i][j] = lwd.getData()[i][paramIndexesToKeep.get(j)];
                }
            }
            lwd.setData(filteredData);
            for(int i=0; i<paramIndexesToKeep.size();i++)
            {
                filteredQC[i] = lwd.getQC()[paramIndexesToKeep.get(i)];
            }
            lwd.setQC(filteredQC);
        });
        
        source.setWeatherParameters(paramsToKeep.toArray(new Integer[paramsToKeep.size()]));
        
        return source;
        
    }

}
