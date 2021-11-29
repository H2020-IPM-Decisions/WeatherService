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

package net.ipmdecisions.weather.util.vips;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import javax.json.bind.annotation.JsonbDateFormat;


/**
 * Data object that represents a weather observation in VIPS. Could be measured (historical
 * and forecasted (future).
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author  Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VIPSWeatherObservation implements Comparable{
    
    public final static Integer LOG_INTERVAL_ID_1M = 8;
    public final static Integer LOG_INTERVAL_ID_10M = 6;
    public final static Integer LOG_INTERVAL_ID_15M = 5;
    public final static Integer LOG_INTERVAL_ID_30M = 4;
    public final static Integer LOG_INTERVAL_ID_1H = 1;
    public final static Integer LOG_INTERVAL_ID_3H = 3;
    public final static Integer LOG_INTERVAL_ID_6H = 7;
    public final static Integer LOG_INTERVAL_ID_1D = 2;

    private String elementMeasurementTypeId;
    private Integer logIntervalId;
    private Date timeMeasured;
    private Double value;
   
    /** Creates a new instance of WeatherObservation */
    public VIPSWeatherObservation() {
    }
    
    /**
     * Constructs a new observation with same properties as original
     * @param original 
     */
    public VIPSWeatherObservation(VIPSWeatherObservation original)
    {
        this.setElementMeasurementTypeId(original.getElementMeasurementTypeId());
        this.setLogIntervalId(original.getLogIntervalId());
        this.setTimeMeasured(original.getTimeMeasured());
        this.setValue(original.getValue());
    }
    
    /**
     * 
     * @param timeMeasured
     * @param elementMeasurementTypeId
     * @param logIntervalId
     * @param value 
     */
    public VIPSWeatherObservation(Date timeMeasured, String elementMeasurementTypeId, Integer logIntervalId, Double value){
        this.timeMeasured = timeMeasured;
        this.elementMeasurementTypeId = elementMeasurementTypeId;
        this.logIntervalId = logIntervalId;
        this.value = value;
    }
        
    public final void setTimeMeasured(Date timeMeasured) { this.timeMeasured = timeMeasured; }
    
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssXXX", timezone="UTC") // Java >= 7
    @JsonbDateFormat(value = "yyyy-MM-dd'T'HH:mm:ssXXX")
    public Date getTimeMeasured() { return this.timeMeasured; }
    
    public final void setElementMeasurementTypeId(String elementMeasurementTypeId) { 
        this.elementMeasurementTypeId = elementMeasurementTypeId; 
    }
    public String getElementMeasurementTypeId() { 
        return this.elementMeasurementTypeId; 
    }
   
    public final void setLogIntervalId(Integer id) { this.logIntervalId = id.intValue(); }
    public Integer getLogIntervalId() { return this.logIntervalId; }
    
    /**
     * Compares by time measured in ascending order
     */
    @Override
    public int compareTo(Object o) {
        VIPSWeatherObservation other = (VIPSWeatherObservation) o;
        return this.compareTo(other);
        
    }
    
    /**
     * Compares by time measured in ascending order
     * @param other the observation to compare to
     * @return
     */
    public int compareTo(VIPSWeatherObservation other)
    {
    	if(this.getTimeMeasured() == null && other.getTimeMeasured() == null)
            return 0;
        else if(this.getTimeMeasured() == null)
            return -1;
        else if(other.getTimeMeasured() == null)
            return 1;
        else
            return this.getTimeMeasured().compareTo(other.getTimeMeasured());
    }
    
    @Override
    public String toString()
    {
        return "Observation \n" +
                "elementMeasurementTypeId=" + this.getElementMeasurementTypeId() + "\n" +
                "timeMeasured=" + this.getTimeMeasured().toString() + "\n" +
                "logIntervalId=" + this.getLogIntervalId().toString() + "\n" +
                "value=" + this.getValue();
    }

    /**
     * @return the value
     */
    public Double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public final void setValue(Double value) {
        this.value = value;
    }
    
    
    //@JsonbTransient
    @JsonIgnore
    public long getValiditySignature()
    {
        long result = 17;
        result = result * this.getLogIntervalId();
        result = result + this.getTimeMeasured().getTime();
        for(char c:this.getElementMeasurementTypeId().toCharArray())
        {
            result = result * c;
        }
        return result;
    }
    
    
    /**
     * Test if the provided WeatherObservation is for the same time, resolution and parameter
     * @param other
     * @return 
     */
    public boolean isDuplicate(VIPSWeatherObservation other)
    {
        if(other == null)
        {
            return false;
        }
        return this.getValiditySignature() == other.getValiditySignature();
    }
}
