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

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle;

import net.ipmdecisions.weather.entity.serializers.LocationWeatherDataDeserializer;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;



/**
 * NOTE: We do not use the meta data here to generate the schema. The schema
 * is a hard coded Json file at the root of the jar file. To get the schema for WeatherData,
 * use SchemaProvider.getWeatherDataSchema()
 * 
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@JsonDeserialize(using = LocationWeatherDataDeserializer.class)
public class LocationWeatherData {

    @NotNull
    @JsonSchemaTitle("Longitude (WGS84)")
    @JsonPropertyDescription("The longitude of the location. Decimal degrees (WGS84)")
    private Double longitude;
    @NotNull
    @JsonSchemaTitle("Latitude (WGS84)")
    @JsonPropertyDescription("The latitude of the location. Decimal degrees (WGS84)")
    private Double latitude;
    @JsonSchemaTitle("Altitude (Meters)")
    @JsonPropertyDescription("The altitude of the location. Measured in meters")
    private Double altitude;
    @Size(min=1)
    @JsonSchemaTitle("QC")
    @JsonPropertyDescription("Quality control results for each weather parameter. Bitmapped against list of tests. For reference, see (TODO)")
    private Integer[] QC;
    @JsonSchemaTitle("Weather data per location")
    @JsonPropertyDescription("The data. In rows, ordered chronologically. Columns ordered as given in weatherParameters.")
    private Double[][] data;
    
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
    
    public void setData(Double[][] data)
    {
        this.data = data;
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
    
    /**
     * @return the QC
     */
    public Integer[] getQC() {
        return QC;
    }

    /**
     * @param QC the QC to set
     */
    public void setQC(Integer[] QC) {
        this.QC = QC;
    }
}
