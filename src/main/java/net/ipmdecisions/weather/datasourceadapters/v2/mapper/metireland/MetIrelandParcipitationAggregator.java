package net.ipmdecisions.weather.datasourceadapters.v2.mapper.metireland;

import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.util.DOMUtils;
import org.w3c.dom.Node;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class MetIrelandParcipitationAggregator {
    static void aggregateParcipitation(Node timeNode,
                                       Node locationNode,
                                       Instant timeStart,
                                       Map<Long, String> rrMap,
                                       int intervalSeconds) {

        Instant fromTime = Instant.parse(timeNode.getAttributes().getNamedItem("from").getNodeValue());
        Instant toTime = Instant.parse(timeNode.getAttributes().getNamedItem("to").getNodeValue());

        if (fromTime.compareTo(toTime) == 0 ||
                DOMUtils.getNode("precipitation", locationNode.getChildNodes()) == null) {
            return;
        }

        Long row = timeStart.until(toTime, ChronoUnit.SECONDS) / intervalSeconds;
        Long currentPeriodDuration = (rrMap.get(row) == null) ? null : Long.valueOf(rrMap.get(row).split("_")[0]);
        Long candidatePeriodDuration = fromTime.until(toTime, ChronoUnit.SECONDS);
        if (currentPeriodDuration == null || currentPeriodDuration > candidatePeriodDuration) {
            rrMap.put(row, candidatePeriodDuration + "_" +
                    DOMUtils.getNodeAttr("precipitation", "value", locationNode.getChildNodes()));
        }
    }

    static void applyToLocation(Map<Long, String> rrMap, LocationWeatherData target) {
        for (Long row : rrMap.keySet()) {
            target.setValue(row.intValue(), 2, Double.valueOf(rrMap.get(row).split("_")[1]));
        }
    }
}
