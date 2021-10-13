
package net.ipmdecisions.weather.datasourceadapters.dmi.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WeatherDataParameter.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WeatherDataParameter">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="airtemp"/>
 *     &lt;enumeration value="glorad"/>
 *     &lt;enumeration value="evapo"/>
 *     &lt;enumeration value="mintemp"/>
 *     &lt;enumeration value="maxtemp"/>
 *     &lt;enumeration value="soiltemp"/>
 *     &lt;enumeration value="airrh"/>
 *     &lt;enumeration value="prec"/>
 *     &lt;enumeration value="sunrad"/>
 *     &lt;enumeration value="windspeed"/>
 *     &lt;enumeration value="winddir"/>
 *     &lt;enumeration value="leafwet"/>
 *     &lt;enumeration value="precNoCor"/>
 *     &lt;enumeration value="airtemp_special"/>
 *     &lt;enumeration value="prec_special"/>
 *     &lt;enumeration value="difrad_special"/>
 *     &lt;enumeration value="glorad_special"/>
 *     &lt;enumeration value="windspeed_special"/>
 *     &lt;enumeration value="winddir_special"/>
 *     &lt;enumeration value="leafwet_special"/>
 *     &lt;enumeration value="airrh_special"/>
 *     &lt;enumeration value="Unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WeatherDataParameter")
@XmlEnum
public enum WeatherDataParameter {

    @XmlEnumValue("airtemp")
    AIRTEMP("airtemp"),
    @XmlEnumValue("glorad")
    GLORAD("glorad"),
    @XmlEnumValue("evapo")
    EVAPO("evapo"),
    @XmlEnumValue("mintemp")
    MINTEMP("mintemp"),
    @XmlEnumValue("maxtemp")
    MAXTEMP("maxtemp"),
    @XmlEnumValue("soiltemp")
    SOILTEMP("soiltemp"),
    @XmlEnumValue("airrh")
    AIRRH("airrh"),
    @XmlEnumValue("prec")
    PREC("prec"),
    @XmlEnumValue("sunrad")
    SUNRAD("sunrad"),
    @XmlEnumValue("windspeed")
    WINDSPEED("windspeed"),
    @XmlEnumValue("winddir")
    WINDDIR("winddir"),
    @XmlEnumValue("leafwet")
    LEAFWET("leafwet"),
    @XmlEnumValue("precNoCor")
    PREC_NO_COR("precNoCor"),
    @XmlEnumValue("airtemp_special")
    AIRTEMP_SPECIAL("airtemp_special"),
    @XmlEnumValue("prec_special")
    PREC_SPECIAL("prec_special"),
    @XmlEnumValue("difrad_special")
    DIFRAD_SPECIAL("difrad_special"),
    @XmlEnumValue("glorad_special")
    GLORAD_SPECIAL("glorad_special"),
    @XmlEnumValue("windspeed_special")
    WINDSPEED_SPECIAL("windspeed_special"),
    @XmlEnumValue("winddir_special")
    WINDDIR_SPECIAL("winddir_special"),
    @XmlEnumValue("leafwet_special")
    LEAFWET_SPECIAL("leafwet_special"),
    @XmlEnumValue("airrh_special")
    AIRRH_SPECIAL("airrh_special"),
    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown");
    private final String value;

    WeatherDataParameter(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WeatherDataParameter fromValue(String v) {
        for (WeatherDataParameter c: WeatherDataParameter.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
