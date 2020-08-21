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

package net.ipmdecisions.weather.util.vips;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


/**
 * Weather related utility methods
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class WeatherUtils {
    
    public final static int AGGREGATION_TYPE_AVERAGE = 1;
    public final static int AGGREGATION_TYPE_SUM = 2;
    public final static int AGGREGATION_TYPE_MINIMUM = 3;
    public final static int AGGREGATION_TYPE_MAXIMUM = 4;
    public final static int AGGREGATION_TYPE_SUM_GLOBAL_RADIATION = 5;

    
    /**
     * 
     * @param observations
     * @param timeZone
     * @param logintervalId
     * @param typeOfAggregation
     * @return
     * @throws WeatherObservationListException
     * @throws InvalidAggregationTypeException 
     */
    public List<VIPSWeatherObservation> getAggregateHourlyValues(List<VIPSWeatherObservation> observations, TimeZone timeZone, Integer logintervalId, Integer typeOfAggregation) throws WeatherObservationListException, InvalidAggregationTypeException{
        if(observations == null || observations.isEmpty())
        {
            return null;
        }
        // First we organize the less-than-hourly values into one bucket per hour
        Map<Date,Map> hourBucket = new HashMap<>();
        String expectedParameter = observations.get(0).getElementMeasurementTypeId();
        Date lastDate = null;
        for(VIPSWeatherObservation observation:observations)
        {
            if(!observation.getElementMeasurementTypeId().equals(expectedParameter))
            {
                throw new WeatherObservationListException("Found multiple parameters: " + observation.getElementMeasurementTypeId() + " and " + expectedParameter);
            }
            Date theDate = normalizeToExactHour(observation.getTimeMeasured(), timeZone);
            lastDate = lastDate == null ? theDate : (lastDate.compareTo(theDate) < 0 ? theDate : lastDate);
            Map<Date, Double> hourValuesForDate = hourBucket.get(theDate);
            if(hourValuesForDate == null)
            {
                hourValuesForDate = new HashMap<>();
                hourBucket.put(theDate, hourValuesForDate);
            }
            
            // Check for double entries
            // TODO: Handle DST change with double entries at 03:00
            Double possibleDuplicate = hourValuesForDate.get(observation.getTimeMeasured());
            if(possibleDuplicate != null)
            {
                throw new WeatherObservationListException(
                        "Found duplicate weatherObservations for parameter " +
                        observation.getElementMeasurementTypeId() + " at time " +
                        observation.getTimeMeasured()
                );
            }
            hourValuesForDate.put(observation.getTimeMeasured(), observation.getValue());
        }
        
        // Then we iterate the buckets, do the aggregation and create return values
        List<VIPSWeatherObservation> aggregatedObservations = new ArrayList<>();
        VIPSWeatherObservation templateObservation = observations.get(0);
        Double aggregateValue;
        for(Date anHour:hourBucket.keySet())
        {
            //System.out.println("date=" + aDay);
            Map valuesForAnHour = hourBucket.get(anHour);
            
            switch(typeOfAggregation){
                case WeatherUtils.AGGREGATION_TYPE_AVERAGE:
                    aggregateValue = getAverage(valuesForAnHour.values()); break;
                case WeatherUtils.AGGREGATION_TYPE_SUM:
                    aggregateValue = getSum(valuesForAnHour.values()); break;
                case WeatherUtils.AGGREGATION_TYPE_MINIMUM:
                    aggregateValue = getMinimum(valuesForAnHour.values()); break;
                case WeatherUtils.AGGREGATION_TYPE_MAXIMUM:
                    aggregateValue = getMaximum(valuesForAnHour.values()); break;
                default:
                    throw new InvalidAggregationTypeException(
                            "No aggregation method with id= " + typeOfAggregation  + " exists."
                            );
            }
            VIPSWeatherObservation aggregatedObservation = new VIPSWeatherObservation();
            aggregatedObservation.setElementMeasurementTypeId(templateObservation.getElementMeasurementTypeId());
            aggregatedObservation.setLogIntervalId(VIPSWeatherObservation.LOG_INTERVAL_ID_1H);
            aggregatedObservation.setTimeMeasured(anHour);
            aggregatedObservation.setValue(aggregateValue);
            aggregatedObservations.add(aggregatedObservation);
        }
        return aggregatedObservations;
    }
    
    /**
     * Slicing off minutes, seconds and milliseconds
     * @param timeStamp
     * @param timeZone
     * @return 
     */
     public Date normalizeToExactHour(Date timeStamp, TimeZone timeZone)
    {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTime(timeStamp);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        return cal.getTime();
    }
  
     public Double getAverage(Collection<Double> values)
    {
        return this.getSum(values)/values.size();
    }

    public Double getSum(Collection<Double> values)
    {
       Double sum = 0d;
        for(Double value:values)
        {
            sum += value;
        }
        return sum;
    }
    
    public Double getMinimum(Collection<Double> values)
    {
        Double minimum = null;
        for(Double value:values)
        {
            minimum = minimum == null ? value : Math.min(minimum, value);
        }
        return minimum;
    }
    
    public Double getMaximum(Collection<Double> values)
    {
        Double maximum = null;
        for(Double value:values)
        {
            maximum = maximum == null ? value : Math.max(maximum, value);
        }
        return maximum;
    }
}
