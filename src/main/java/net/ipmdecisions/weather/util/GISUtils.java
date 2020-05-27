/*
 * Copyright (c) 2020 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of IPM Decisions DSS Service.
 * IPM Decisions DSS Service is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * IPM Decisions DSS Service is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with IPM Decisions DSS Service.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.ipmdecisions.weather.util;

/**
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class GISUtils {

    /**
     * Ref this post: https://gis.stackexchange.com/questions/14449/java-vividsolutions-jts-wgs-84-distance-to-meters
     * @param jtsDistanceAngularUnits
     * @return 
     */
    public Double getDistanceInMetersWGS84(Double jtsDistanceAngularUnits)
    {
        return jtsDistanceAngularUnits * (Math.PI/180) * 6378137;
    }
}
