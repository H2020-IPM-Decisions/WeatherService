
package net.ipmdecisions.weather.datasourceadapters.dmi.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetWeatherDataMultipointResult" type="{http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract}ArrayOfExtendedWeatherResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getWeatherDataMultipointResult"
})
@XmlRootElement(name = "GetWeatherDataMultipointResponse", namespace = "http://tempuri.org/")
public class GetWeatherDataMultipointResponse {

    @XmlElementRef(name = "GetWeatherDataMultipointResult", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfExtendedWeatherResponse> getWeatherDataMultipointResult;

    /**
     * Gets the value of the getWeatherDataMultipointResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfExtendedWeatherResponse }{@code >}
     *     
     */
    public JAXBElement<ArrayOfExtendedWeatherResponse> getGetWeatherDataMultipointResult() {
        return getWeatherDataMultipointResult;
    }

    /**
     * Sets the value of the getWeatherDataMultipointResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfExtendedWeatherResponse }{@code >}
     *     
     */
    public void setGetWeatherDataMultipointResult(JAXBElement<ArrayOfExtendedWeatherResponse> value) {
        this.getWeatherDataMultipointResult = value;
    }

}
