package net.ipmdecisions.weather.datasourceadapters.v2.mapper.metireland;

import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.util.DOMUtils;
import org.w3c.dom.Node;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class MetIrelandElementExtractor {
    static void extractElements(Node timeNode,
                                Node locationNode,
                                Instant timeStart,
                                LocationWeatherData target,
                                int intervalSeconds) {

        Instant fromTime = Instant.parse(timeNode.getAttributes().getNamedItem("from").getNodeValue());
        Instant toTime = Instant.parse(timeNode.getAttributes().getNamedItem("to").getNodeValue());

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
