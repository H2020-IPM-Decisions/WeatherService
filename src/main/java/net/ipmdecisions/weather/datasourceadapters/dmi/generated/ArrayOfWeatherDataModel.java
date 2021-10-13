
package net.ipmdecisions.weather.datasourceadapters.dmi.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfWeatherDataModel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfWeatherDataModel">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="WeatherDataModel" type="{http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract}WeatherDataModel" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfWeatherDataModel", propOrder = {
    "weatherDataModel"
})
public class ArrayOfWeatherDataModel {

    @XmlElement(name = "WeatherDataModel", nillable = true)
    protected List<WeatherDataModel> weatherDataModel;

    /**
     * Gets the value of the weatherDataModel property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the weatherDataModel property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWeatherDataModel().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WeatherDataModel }
     * 
     * 
     */
    public List<WeatherDataModel> getWeatherDataModel() {
        if (weatherDataModel == null) {
            weatherDataModel = new ArrayList<WeatherDataModel>();
        }
        return this.weatherDataModel;
    }

}
