
package net.ipmdecisions.weather.datasourceadapters.dmi.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfExtendedWeatherResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfExtendedWeatherResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ExtendedWeatherResponse" type="{http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract}ExtendedWeatherResponse" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfExtendedWeatherResponse", propOrder = {
    "extendedWeatherResponse"
})
public class ArrayOfExtendedWeatherResponse {

    @XmlElement(name = "ExtendedWeatherResponse", nillable = true)
    protected List<ExtendedWeatherResponse> extendedWeatherResponse;

    /**
     * Gets the value of the extendedWeatherResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extendedWeatherResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtendedWeatherResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExtendedWeatherResponse }
     * 
     * 
     */
    public List<ExtendedWeatherResponse> getExtendedWeatherResponse() {
        if (extendedWeatherResponse == null) {
            extendedWeatherResponse = new ArrayList<ExtendedWeatherResponse>();
        }
        return this.extendedWeatherResponse;
    }

}
