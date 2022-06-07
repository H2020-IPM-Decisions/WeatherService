/*
 * Copyright (c) 2022 NIBIO <http://www.nibio.no/>. 
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

package net.ipmdecisions.weather.amalgamation.indices.leafwetness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ipmdecisions.weather.amalgamation.indices.IndiceCalculator;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;

/**
 * 
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 *
 */
public class LeafWetnessCalculator implements IndiceCalculator{
	private static Logger LOGGER = LoggerFactory.getLogger(LeafWetnessCalculator.class);

	@Override
	public WeatherData calculateIndice(WeatherData weatherData, Integer weatherParameter) {
		// TODO: Check available source parameters in the weather data object
		// Based on this: Select the preferred method.
		// Currently (June 2022) we only have one, then constant RH method
		return this.calculateFromConstantRH(weatherData);
	}
	
	/**
	 * 
	 * The RH >= 87% method TODO: Add references??
	 * 
	 * @param weatherData
	 * @return
	 */
	public WeatherData calculateFromConstantRH(WeatherData weatherData)
	{
		//LOGGER.debug("Running calculateFromConstantRH");
		// Look for RH (3001, 3002)
		List<Integer> rhParams = List.of(3001,3002);
		List<Integer> rhParamsInDataset = Arrays.asList(weatherData.getWeatherParameters()).stream() 
				.distinct()
				.filter(p->rhParams.contains(p))
				.collect(Collectors.toList());
		if(rhParamsInDataset != null && rhParamsInDataset.size() > 0)
		{
			Integer rhParamIndex = weatherData.getParameterIndex(rhParamsInDataset.get(0));
			for(LocationWeatherData lwd: weatherData.getLocationWeatherData()) {
				Double[][] oldData = lwd.getData();
				Double[][] newData = new Double[oldData.length][oldData[0].length + 1];
				for(int row = 0; row < oldData.length; row++)
				{
					for(int col=0; col < newData[0].length; col++)
					{
						// Old data are copied
						if(col < oldData[0].length)
						{
							newData[row][col] = oldData[row][col];
						}
						else // Column for LW based on RH
						{
							if(oldData[row][rhParamIndex] != null)
							{
								newData[row][col] = oldData[row][rhParamIndex] >= 87.0 ? 60.0 : 0.0;
							}
						}
					}
				}
				lwd.setData(newData);
			}
			// Add the missing parameter to end of parameter list in weather data
			List<Integer> wpList = new ArrayList<Integer>(Arrays.asList(weatherData.getWeatherParameters())); 
			wpList.add(3101);
			weatherData.setWeatherParameters(wpList.toArray(new Integer[wpList.size()]));
		}
		return weatherData;
	}


}
