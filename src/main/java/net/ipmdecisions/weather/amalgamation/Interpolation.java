/*
 * Copyright (c) 2021 NIBIO <http://www.nibio.no/>. 
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

package net.ipmdecisions.weather.amalgamation;

import java.util.List;
import java.util.Set;

import net.ipmdecisions.weather.entity.AmalgamationType;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.LocationWeatherDataException;
import net.ipmdecisions.weather.entity.WeatherData;

// TODO Add different interpolation methods?
public class Interpolation {

	public WeatherData interpolate(WeatherData input, Set<Integer> parametersToInterpolate, Integer maxMissingSteps) throws LocationWeatherDataException
	{
		for(LocationWeatherData l : input.getLocationWeatherData())
		{
			for(Integer param:parametersToInterpolate)
			{
				Double[] dataToInterpolate = l.getColumn(input.getParameterIndex(param));
				// Now do the interpolation
				for(int i=0;i<dataToInterpolate.length;i++)
				{
					if(dataToInterpolate[i] == null)
					{
						if(i == 0)
						{
							// ERROR: Can't interpolate without starting value
						}
						Integer lastIndexBeforeHole = i-1;
						while(i<dataToInterpolate.length && dataToInterpolate[i] == null)
						{
							i++;
						}
						if(i >= dataToInterpolate.length -1) // <- Do we ever get there?
						{
							// ERROR: Can't interpolate without ending value
						}
						Integer firstIndexAfterHole = i;
						// If there are too many missing steps, do nothing
						if(firstIndexAfterHole - 1 - lastIndexBeforeHole > maxMissingSteps)
						{
							continue;
						}
						Double lastValueBeforeHole = dataToInterpolate[lastIndexBeforeHole];
						Double firstValueAfterHole = dataToInterpolate[firstIndexAfterHole];
						Double diff = firstValueAfterHole - lastValueBeforeHole;
						Double fraction = diff / (firstIndexAfterHole - lastIndexBeforeHole);
						for(int j=1; j<firstIndexAfterHole - lastIndexBeforeHole;j++)
						{
							//System.out.println("lastValueBeforeHole + j * fraction = " + lastIndexBeforeHole + " + " + j + " * " + fraction + "="  + (lastValueBeforeHole + j * fraction));
							dataToInterpolate[lastIndexBeforeHole + j] = lastValueBeforeHole + j * fraction;
						}
					}
				}
				// Insert the data into the returned data
				try 
				{
					l.setColumn(input.getParameterIndex(param), dataToInterpolate);
					// Set the method used (Adding to existing)
					Integer[] amalgamation = l.getAmalgamation();
					// "|" is the bitwise OR operator
					amalgamation[input.getParameterIndex(param)] = amalgamation[input.getParameterIndex(param)] | AmalgamationType.INTERPOLATED;
				}
				catch(LocationWeatherDataException ex)
				{
					// TODO: Handle error???
				}
			}
			
		}
		return input;
	}
}
