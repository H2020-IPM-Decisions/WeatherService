package net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel;

import net.ipmdecisions.weather.datasourceadapters.v2.params.MetirelandParamModel;
import org.w3c.dom.Document;

public record MetIrelandResponse(Document doc, MetirelandParamModel params) {
}
