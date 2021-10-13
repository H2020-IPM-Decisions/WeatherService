
package net.ipmdecisions.weather.datasourceadapters.dmi.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfWeatherDataSource complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfWeatherDataSource">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="WeatherDataSource" type="{http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract}WeatherDataSource" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfWeatherDataSource", propOrder = {
    "weatherDataSource"
})
public class ArrayOfWeatherDataSource {

    @XmlElement(name = "WeatherDataSource")
    @XmlSchemaType(name = "string")
    protected List<WeatherDataSource> weatherDataSource;

    /**
     * Gets the value of the weatherDataSource property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the weatherDataSource property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWeatherDataSource().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WeatherDataSource }
     * 
     * 
     */
    public List<WeatherDataSource> getWeatherDataSource() {
        if (weatherDataSource == null) {
            weatherDataSource = new ArrayList<WeatherDataSource>();
        }
        return this.weatherDataSource;
    }

}
