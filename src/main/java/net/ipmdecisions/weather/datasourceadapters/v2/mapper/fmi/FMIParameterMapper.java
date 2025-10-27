package net.ipmdecisions.weather.datasourceadapters.v2.mapper.fmi;

import java.util.Map;

/**
 * Central mapping between raw FMI parameter names and VIPS/IPM codes.
 */
public class FMIParameterMapper {

    private static final Map<String, String> RAW_TO_VIPS = Map.of(
            "Temperature", "TM",
            "Humidity", "UM",
            "WindSpeedMS", "FF2",
            "DewPoint", "DP",
            "Precipitation1h", "RR",
            "radiationglobal", "Q0"
    );

    private static final Map<String, Integer> VIPS_TO_IPM = Map.of(
            "TM", 1002,
            "RR", 2001,
            "UM", 3002,
            "Q0", 5001,
            "BT", 3101,
            "FF2", 4002,
            "FM2", 4003,
            "DP", 1901
    );

    private FMIParameterMapper() {}

    public static String mapToVipsCode(String rawName) {
        return RAW_TO_VIPS.get(rawName);
    }

    public static Integer getIPMParameterId(String vipsCode) {
        return VIPS_TO_IPM.get(vipsCode);
    }
}