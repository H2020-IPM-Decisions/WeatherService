
package net.ipmdecisions.weather.datasourceadapters.dmi.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WeatherInterval.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WeatherInterval">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="day"/>
 *     &lt;enumeration value="hour"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WeatherInterval")
@XmlEnum
public enum WeatherInterval {

    @XmlEnumValue("day")
    DAY("day"),
    @XmlEnumValue("hour")
    HOUR("hour");
    private final String value;

    WeatherInterval(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WeatherInterval fromValue(String v) {
        for (WeatherInterval c: WeatherInterval.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
