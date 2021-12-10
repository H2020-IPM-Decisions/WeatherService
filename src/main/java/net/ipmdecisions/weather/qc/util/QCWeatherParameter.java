package net.ipmdecisions.weather.qc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.ipmdecisions.weather.entity.WeatherParameter;

public class QCWeatherParameter {

    private Integer type;
    private QCWeatherParameterAggregationType aggregationType;

    public QCWeatherParameter() {
    }

    public QCWeatherParameter(Integer type, QCWeatherParameterAggregationType aggregationType) {
        this.type = type;
        this.aggregationType = aggregationType;
    }

    public QCWeatherParameter(WeatherParameter wp) {
        this.setWeatherParameter(wp);
    }
    
    public void setWeatherParameter(WeatherParameter wp) {
        this.setType(wp);
        this.setAggregationType(wp);
    }

    private void setType(WeatherParameter wp) {
        // Get the three first numbers (123) of the four number (1234) 
        // weather parameter, indicating the type of value.
        Integer id = wp.getId();
        
        if (id == null) return;

        this.type = id / 10;
    }

    private void setAggregationType(WeatherParameter wp) {
        String wpAggregationType = wp.getAggregationType();

        if (wpAggregationType == null) return;

        switch (wpAggregationType) {
            case WeatherParameter.AGGREGATION_TYPE_MINIMUM: {
                this.aggregationType = QCWeatherParameterAggregationType.MINIMUM;
                break;
            }
            case WeatherParameter.AGGREGATION_TYPE_MAXIMUM: {
                this.aggregationType = QCWeatherParameterAggregationType.MAXIMUM;
                break;
            }
            case WeatherParameter.AGGREGATION_TYPE_SUM: {
                this.aggregationType = QCWeatherParameterAggregationType.SUM;
                break;
            }
            case WeatherParameter.AGGREGATION_TYPE_AVERAGE: {
                this.aggregationType = QCWeatherParameterAggregationType.AVERAGE;

                String name = wp.getName();

                Pattern pattern = Pattern.compile("^\\s*Mean\\s*.*$");
                Matcher m = pattern.matcher(name);

                if (m.matches()) {
                    this.aggregationType = QCWeatherParameterAggregationType.MEAN;
                }
                break;
            }
            default: {
                this.aggregationType = QCWeatherParameterAggregationType.OTHER;
            }
        }
    }

    public Integer getType() {
        return this.type;
    }

    public QCWeatherParameterAggregationType getAggregationType() {
        return this.aggregationType;
    }
}
