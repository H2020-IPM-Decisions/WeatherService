package net.ipmdecisions.weather.datasourceadapters.v2.mapper.fmi;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.xml.sax.InputSource;

public class FMIXmlExtractor {

    private static final Logger LOG = Logger.getLogger(FMIXmlExtractor.class.getName());
    private static final String TAG_FIELD = "swe:field";
    private static final String TAG_POSITIONS = "gmlcov:positions";
    private static final String TAG_VALUES = "gml:doubleOrNilReasonTupleList";

    private FMIXmlExtractor() {}

    public static FMIParsingResult extract(String xml) {
        try {
            Document doc = buildSecureBuilder().parse(new InputSource(new StringReader(xml)));

            List<String> parameterNames = readParameterNames(doc.getElementsByTagName(TAG_FIELD));
            String[] positionLines = splitLines(doc, TAG_POSITIONS);
            String[] valueLines = splitLines(doc, TAG_VALUES);

            int usableLines = Math.min(positionLines.length, valueLines.length);
            List<Long> timestamps = new ArrayList<>();
            List<double[]> rows = new ArrayList<>();

            for (int i = 0; i < usableLines; i++) {
                String posLine = positionLines[i].trim();
                String valLine = valueLines[i].trim();
                if (posLine.isBlank() || valLine.isBlank()) continue;
                if (valLine.contains("NaN")) continue;

                String[] posTokens = posLine.split("\\s+");
                if (posTokens.length < 3) continue;

                Long epoch;
                try {
                    epoch = Long.parseLong(posTokens[2]);
                } catch (NumberFormatException ex) {
                    continue;
                }

                String[] valueTokens = valLine.split("\\s+");
                if (valueTokens.length < parameterNames.size()) continue;

                double[] row = new double[parameterNames.size()];
                boolean invalid = false;
                for (int p = 0; p < parameterNames.size(); p++) {
                    try {
                        row[p] = Double.parseDouble(valueTokens[p]);
                    } catch (NumberFormatException e) {
                        invalid = true;
                        break;
                    }
                }
                if (invalid) continue;

                timestamps.add(epoch);
                rows.add(row);
            }

            if (timestamps.isEmpty()) {
                return null;
            }

            double[][] matrix = rows.toArray(new double[0][]);
            return new FMIParsingResult(parameterNames, timestamps, matrix);

        } catch (Exception e) {
            LOG.warning("Failed to parse FMI XML: " + e.getMessage());
            return null;
        }
    }

    private static DocumentBuilder buildSecureBuilder() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setExpandEntityReferences(false);
        dbf.setNamespaceAware(false);
        return dbf.newDocumentBuilder();
    }

    private static List<String> readParameterNames(NodeList fields) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < fields.getLength(); i++) {
            Element e = (Element) fields.item(i);
            String name = e.getAttribute("name");
            names.add(name.trim());
        }
        return names;
    }

    private static String[] splitLines(Document doc, String tag) {
        NodeList nl = doc.getElementsByTagName(tag);
        if (nl.getLength() == 0 || nl.item(0) == null) {
            return new String[0];
        }
        return nl.item(0).getTextContent().split("\\r?\\n");
    }
}
