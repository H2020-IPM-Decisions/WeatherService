package net.ipmdecisions.weather.datasourceadapters.v2.mapper;

import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.util.DOMUtils;
import org.w3c.dom.Node;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * Collects aggregated precipitation choosing shortest duration window per row.
 */
public final class PrecipitationMapper {

    private PrecipitationMapper() {}

    public static void aggregate(Node timeNode,
                                 Node locationNode,
                                 Instant timeStart,
                                 Map<Long, String> storage,
                                 int intervalSeconds) {

        if (timeNode == null || locationNode == null) return;

        Node precipNode = DOMUtils.getNode("precipitation", locationNode.getChildNodes());
        if (precipNode == null) return;

        Instant from = Instant.parse(timeNode.getAttributes().getNamedItem("from").getNodeValue());
        Instant to = Instant.parse(timeNode.getAttributes().getNamedItem("to").getNodeValue());
        if (from.equals(to)) return;

        long row = timeStart.until(to, ChronoUnit.SECONDS) / intervalSeconds;
        long candidateDuration = from.until(to, ChronoUnit.SECONDS);
        String existing = storage.get(row);
        Long currentDuration = (existing == null) ? null : Long.valueOf(existing.split("_")[0]);

        if (currentDuration == null || currentDuration > candidateDuration) {
            String value = DOMUtils.getNodeAttr("precipitation", "value", locationNode.getChildNodes());
            storage.put(row, candidateDuration + "_" + value);
        }
    }

    public static void apply(Map<Long, String> storage,
                             int precipitationColumn,
                             LocationWeatherData target) {
        for (Map.Entry<Long, String> e : storage.entrySet()) {
            Double val = Double.valueOf(e.getValue().split("_")[1]);
            target.setValue(e.getKey().intValue(), precipitationColumn, val);
        }
    }
}
