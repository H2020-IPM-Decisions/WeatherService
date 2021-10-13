
package net.ipmdecisions.weather.datasourceadapters.dmi.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WeatherErrorModel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WeatherErrorModel">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ErrorMessages" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeatherErrorModel", propOrder = {
    "errorMessages"
})
public class WeatherErrorModel {

    @XmlElementRef(name = "ErrorMessages", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfstring> errorMessages;

    /**
     * Gets the value of the errorMessages property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public JAXBElement<ArrayOfstring> getErrorMessages() {
        return errorMessages;
    }

    /**
     * Sets the value of the errorMessages property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public void setErrorMessages(JAXBElement<ArrayOfstring> value) {
        this.errorMessages = value;
    }

}
