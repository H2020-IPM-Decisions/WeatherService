package net.ipmdecisions.weather.datasourceadapters.v2.mapper.common;

import java.util.Map;

public class ParameterMapper {

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

    public static final Map<Integer, String> IPM_TO_VIPS = Map.of(
            1002, "TM",
            2001, "RR",
            3002, "UM",
            5001, "Q0",
            3101, "BT",
            4002, "FF2",
            4003, "FM2",
            1901, "DP"
    );

    private ParameterMapper() {}

    public static String mapToVipsCode(String rawName) {
        return RAW_TO_VIPS.get(rawName);
    }

    public static Integer getIPMParameterId(String vipsCode) {
        return VIPS_TO_IPM.get(vipsCode);
    }


}