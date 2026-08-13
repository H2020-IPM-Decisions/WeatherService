package net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel;

import net.ipmdecisions.weather.datasourceadapters.v2.params.YrParamModel;
import org.w3c.dom.Document;

public record YrResponse(Document doc, YrParamModel paramModel) {
}
