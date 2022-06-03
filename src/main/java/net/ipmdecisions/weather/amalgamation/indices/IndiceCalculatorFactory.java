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
package net.ipmdecisions.weather.amalgamation.indices;

import net.ipmdecisions.weather.amalgamation.indices.leafwetness.LeafWetnessCalculator;

/**
 * Add calculators for indices here. One per indice. 
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 *
 */
public class IndiceCalculatorFactory {

	public synchronized static IndiceCalculator getIndiceCalculator(Integer weatherParameterId)
	{
		if(weatherParameterId.equals(3101))
		{
			return new LeafWetnessCalculator();
		}
		return null;
	}
}
