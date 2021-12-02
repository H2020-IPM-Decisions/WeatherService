package net.ipmdecisions.weather.qc.util;

import java.util.ArrayList;
import java.util.Arrays;
import net.ipmdecisions.weather.qc.util.QCTestType;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class QCHelpers {
    
    public QCHelpers() {
    }

    /**
     * Swap axes of `LocationWeatherData` data.
     *
     * Data held in `LocationWeatherData` is a matrix, where the first level 
     * array (row) consists of values at certain point in time, and the second 
     * level (column) of different weather parameters at that moment.
     *
     * This function turns the matrix to its side, so that each weather 
     * parameter's data is easier to handle individually. Returned matrix has 
     * weather parameters at the first level and time based values for that 
     * parameter on the second level.
     *
     * @param data - data from LocationWeatherData
     * @param nParameters - number of parameters in data
     * @return data turned inside out.
     */
    public static Double[][] switchRowsAndColumnsForLocationWeatherData(Double[][] data, int nParameters) {
        if (data == null) return data;
        
        Double[][] turnedData = new Double[nParameters][data.length];

        for(Integer r=0; r < data.length; r++)
        {
            for(Integer c=0; c < nParameters; c++)
            {
                turnedData[c][r] = data[r][c];
            }
        }
        
        return turnedData;
    }
    
    /**
     * Pick aggregate values of a same type from a given list of weather 
     * parameters.
     * 
     * For logical QC test we need to compare mean, min and max values of the 
     * same type. This function takes a list of weather parameters and finds 
     * groups of mean, min and max values, and returns those groups as tuples 
     * (really just arrays).
     * 
     * This function assumes that the last number of a parameter maps to its 
     * aggregation type in the following manner:
     * 
     * - 2 - mean
     * - 3 - min
     * - 4 - max
     * 
     * The returned tuples contain following values:
     * 
     * 1. index of mean value (in the list of weather parameters)
     * 2. index of min value (in the list of weather parameters)
     * 3. index of max value (in the list of weather parameters)
     * 
     * * If a min or max value is missing, null will be in its place in tuple.
     * * If both min and max are missing, no tuple is returned for that mean 
     *   value.
     * 
     * Returned tuples are ordered in ascending order of ids (= the values 
     * that the indices in tuples point to).
     * 
     * @param weatherParameters
     * @return a list of aggregate tuples
     */
    public static Integer[][] getLogicalTuplesFromWeatherParameters(Integer[] weatherParameters) {
        // Key is type of weather parameter (its three first numbers - 123x), 
        // value is an array of 3 indices (mean, min, max - in that order),
        // where indices point to that values location in the 
        // `weatherParameters` array.
        HashMap<Integer, Integer[]> types = new HashMap<Integer, Integer[]>();

        for(Integer i=0; i < weatherParameters.length; i++)
        {
            // null as weather parameter means that a specific weather parameter
            // is removed from the list, as it doesn't need logical QC.
            if (weatherParameters[i] == null) continue;

            // Get the three first numbers (123) of the four number (1234) 
            // weather parameter, indicating the type of value.
            Integer type = weatherParameters[i] / 10;
            // Get the last number (4) of the four number (1234) weather 
            // parameter, indicating the aggregation type.
            Integer aggregationType = weatherParameters[i] % 10;
            
            // We are only interested in aggregation types 2, 3 and 4 (mean, 
            // min and max).
            if (aggregationType < 2 || aggregationType > 4) continue;

            Integer aggs[];
            if (types.containsKey(type)) {
                aggs = types.get(type);
            } else {
                aggs = new Integer[3];
            }

            // Aggregation types start from 2, while arrays start from 0, so we
            // remove padding of 2.
            aggs[aggregationType - 2] = i;
            types.put(type, aggs);
        }

        Integer[] typeKeys = types.keySet().toArray(Integer[]::new);
        // Sort keys to make the order of function's return values predictable 
        // (makes testing easier).
        Arrays.sort(typeKeys);
        Integer[][] initialTuples = new Integer[typeKeys.length][3];
        int iInitialTuples = 0;

        for (Integer type : typeKeys) {
            Integer[] aggs = types.get(type);

            // mean needs to be given
            if (aggs[0] == null) continue;

            // either min or max needs to be given
            if (aggs[1] == null && aggs[2] == null) continue;
            
            initialTuples[iInitialTuples] = aggs;
            iInitialTuples++;
        }

        Integer[][] tuples = new Integer[iInitialTuples][3];

        for (Integer i=0; i<tuples.length; i++) {
            tuples[i] = initialTuples[i];
        }

        return tuples;
    }

    public static boolean isWeatherParameterHandledByQCTest(int weatherParameterId, QCTestType qctesttype) {

        switch(qctesttype) {
            // all weather parameters are checked for prequalification
            case PREQUALIFICATION: return true;
            
            // all weather parameters are checked for interval
            case INTERVAL: {
                return true;
            }

            // only following weather parameters are checked for logical qc:
            // * temperature 1000
            // * soil temperature 1100
            // * humidity 3000
            // * wind 4000
            case LOGICAL: {
                Pattern pattern = Pattern.compile("^((10|11)\\d\\d)|((3|4)\\d\\d\\d)$");
                Matcher m = pattern.matcher("" + weatherParameterId);
                return m.matches();
            }

            // all weather parameters are checked for step qc except:
            // * precipitation 2000
            case STEP: {
                Pattern pattern = Pattern.compile("^(2\\d\\d\\d)$");
                Matcher m = pattern.matcher("" + weatherParameterId);
                return !m.matches();
            }

            // all weather parameters are checked for interval
            case FREEZE: {
                return true;
            }

            // other qctypes are expected to be handled for all weather 
            // parameters, if such are defined.
            default: return true;
        }
    }
    
    /**
     * Quick helper function for checking if given weather parameter describes 
     * precipitation.
     * 
     * Needed in the freeze QC test, where precipitation value 0.0 is a special
     * value.
     * 
     * @param weatherParameter
     * @return boolean 
     */
    public static boolean isWeatherParameterPrecipitation(Integer weatherParameter) {
        Pattern pattern = Pattern.compile("^(2\\d\\d\\d)$");
        Matcher m = pattern.matcher("" + weatherParameter);
        return m.matches();
    }

    
    /**
     * Remove from array of weather parameters the weather parameters that are
     * not handled by a given QC test.
     * 
     * @param weatherParameters - array of weather parameters
     * @param qctesttype - type of qc test to filter weather parameters
     * @return array of weather parameters, with incompatible weather parameters
     * replaced by null value.
     */
    public static Integer[] filterWeatherParametersBasedOnQCType(Integer[] weatherParameters, QCTestType qctesttype) {
        List<Integer> wpList = new ArrayList<Integer>(Arrays.asList(weatherParameters));
        Integer[] weatherParametersToHandle = wpList.stream().map((Integer wp) -> {
            if (QCHelpers.isWeatherParameterHandledByQCTest(wp, qctesttype)) {
                return wp;
            } else {
                return null;
            }
        }).toArray(Integer[]::new);

        return weatherParametersToHandle;
    }
}