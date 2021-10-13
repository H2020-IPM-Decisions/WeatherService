
package net.ipmdecisions.weather.datasourceadapters.dmi.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfWeatherDataParameter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfWeatherDataParameter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="WeatherDataParameter" type="{http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract}WeatherDataParameter" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfWeatherDataParameter", propOrder = {
    "weatherDataParameter"
})
public class ArrayOfWeatherDataParameter {

    @XmlElement(name = "WeatherDataParameter")
    @XmlSchemaType(name = "string")
    protected List<WeatherDataParameter> weatherDataParameter;

    /**
     * Gets the value of the weatherDataParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the weatherDataParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWeatherDataParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WeatherDataParameter }
     * 
     * 
     */
    public List<WeatherDataParameter> getWeatherDataParameter() {
        if (weatherDataParameter == null) {
            weatherDataParameter = new ArrayList<WeatherDataParameter>();
        }
        return this.weatherDataParameter;
    }

}
