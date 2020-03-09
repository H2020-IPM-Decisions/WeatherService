/*
 * Copyright (c) 2020 NIBIO <http://www.nibio.no/>. 
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

package net.ipmdecisions.weather.entity;

import javax.validation.constraints.NotNull;

/**
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class LocationWeatherData {
    @NotNull
    private Double longitude;
    @NotNull
    private Double latitude;
    private Double altitude;
    private final Double[][] data;
    
    public LocationWeatherData(Double longitude, Double latitude, Double altitude, int rows, int columns){
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.data = new Double[rows][columns];
    }
    
    /**
     * 
     * @return The number of rows in the dataset 
     */
    public Integer getLength()
    {
        return this.data.length;
    }
    
    /**
     * 
     * @return The number of parameters per rows in the dataset 
     */
    public Integer getWidth()
    {
        return this.data[0].length;
    }
    
    public Double[][] getData()
    {
        return this.data;
    }
    
    public void setValue(Integer row, Integer column, Double value)
    {
        this.data[row][column] = value;
    }
    
    public Double getValue(Integer row, Integer column)
    {
        return this.data[row][column];
    }
    
    public Double[] getRow(Integer row)
    {
        return this.data[row];
    }
    
    public Double[] getColumn(Integer column)
    {
        Double[] columnValues = new Double[this.data.length];
        for(Integer i=0; i < this.data.length; i++)
        {
            columnValues[i] = this.data[i][column];
        }
        return columnValues;
    }
    
    @Override
    public String toString()
    {
        String retVal = "";
        for(Integer i=0;i<this.data.length;i++)
        {
            retVal += i + "";
            for(Integer j = 0; j < this.data[i].length; j++)
            {
                retVal += "\t" + this.data[i][j];
            }
            retVal += "\n";
        }
        return retVal;
    }

    /**
     * @return the longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the altitude
     */
    public Double getAltitude() {
        return altitude;
    }

    /**
     * @param altitude the altitude to set
     */
    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }
}
