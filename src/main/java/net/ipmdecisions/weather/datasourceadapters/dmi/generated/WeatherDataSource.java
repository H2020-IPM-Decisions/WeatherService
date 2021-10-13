
package net.ipmdecisions.weather.datasourceadapters.dmi.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WeatherDataSource.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WeatherDataSource">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="obs"/>
 *     &lt;enumeration value="forecast"/>
 *     &lt;enumeration value="normal"/>
 *     &lt;enumeration value="auto"/>
 *     &lt;enumeration value="partly_constructed"/>
 *     &lt;enumeration value="constructed_case1"/>
 *     &lt;enumeration value="constructed_case2"/>
 *     &lt;enumeration value="constructed_case3"/>
 *     &lt;enumeration value="obs_forecast"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WeatherDataSource")
@XmlEnum
public enum WeatherDataSource {

    @XmlEnumValue("obs")
    OBS("obs"),
    @XmlEnumValue("forecast")
    FORECAST("forecast"),
    @XmlEnumValue("normal")
    NORMAL("normal"),
    @XmlEnumValue("auto")
    AUTO("auto"),
    @XmlEnumValue("partly_constructed")
    PARTLY_CONSTRUCTED("partly_constructed"),
    @XmlEnumValue("constructed_case1")
    CONSTRUCTED_CASE_1("constructed_case1"),
    @XmlEnumValue("constructed_case2")
    CONSTRUCTED_CASE_2("constructed_case2"),
    @XmlEnumValue("constructed_case3")
    CONSTRUCTED_CASE_3("constructed_case3"),
    @XmlEnumValue("obs_forecast")
    OBS_FORECAST("obs_forecast");
    private final String value;

    WeatherDataSource(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WeatherDataSource fromValue(String v) {
        for (WeatherDataSource c: WeatherDataSource.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
