package net.ipmdecisions.weather.datasourceadapters.v2.mapper;

import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.util.DOMUtils;
import org.w3c.dom.Node;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Generic helper for extracting instantaneous (from == to) weather elements
 * (temperature, humidity, windSpeed) into a LocationWeatherData structure.
 * Used by multiple upstream datasource mappers to avoid duplication.
 */
public final class DocumentElementExtractor {

    private DocumentElementExtractor() {}

    /**
     * Extract instantaneous elements from a time node/location node pair.
     *
     * @param timeNode       the <time> DOM node
     * @param locationNode   the corresponding <location> child node
     * @param timeStart      start of the dataset time span
     * @param target         target data container
     * @param intervalSeconds interval length in seconds (e.g. 3600)
     */
    public static void extract(Node timeNode,
                               Node locationNode,
                               Instant timeStart,
                               LocationWeatherData target,
                               int intervalSeconds) {
        if (timeNode == null || locationNode == null) {
            return;
        }
        Instant fromTime = Instant.parse(timeNode.getAttributes().getNamedItem("from").getNodeValue());
        Instant toTime = Instant.parse(timeNode.getAttributes().getNamedItem("to").getNodeValue());

        // Only handle instantaneous values here
        if (fromTime.compareTo(toTime) != 0) {
            return;
        }

        int row = Long.valueOf(timeStart.until(fromTime, ChronoUnit.SECONDS) / intervalSeconds).intValue();

        if (DOMUtils.getNode("temperature", locationNode.getChildNodes()) != null) {
            target.setValue(row, 0,
                    Double.valueOf(DOMUtils.getNodeAttr("temperature", "value", locationNode.getChildNodes())));
        }
        if (DOMUtils.getNode("humidity", locationNode.getChildNodes()) != null) {
            target.setValue(row, 1,
                    Double.valueOf(DOMUtils.getNodeAttr("humidity", "value", locationNode.getChildNodes())));
        }
        if (DOMUtils.getNode("windSpeed", locationNode.getChildNodes()) != null) {
            target.setValue(row, 3,
                    Double.valueOf(DOMUtils.getNodeAttr("windSpeed", "mps", locationNode.getChildNodes())));
        }
    }
}

