
package net.ipmdecisions.weather.datasourceadapters.dmi.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WeatherResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WeatherResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="WeahterDataList" type="{http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract}ArrayOfWeatherDataModel" minOccurs="0"/>
 *         &lt;element name="WeatherError" type="{http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract}WeatherErrorModel" minOccurs="0"/>
 *         &lt;element name="HasError" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeatherResponse", propOrder = {
    "weahterDataList",
    "weatherError",
    "hasError"
})
public class WeatherResponse {

    @XmlElementRef(name = "WeahterDataList", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfWeatherDataModel> weahterDataList;
    @XmlElementRef(name = "WeatherError", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<WeatherErrorModel> weatherError;
    @XmlElement(name = "HasError")
    protected Boolean hasError;

    /**
     * Gets the value of the weahterDataList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataModel }{@code >}
     *     
     */
    public JAXBElement<ArrayOfWeatherDataModel> getWeahterDataList() {
        return weahterDataList;
    }

    /**
     * Sets the value of the weahterDataList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataModel }{@code >}
     *     
     */
    public void setWeahterDataList(JAXBElement<ArrayOfWeatherDataModel> value) {
        this.weahterDataList = value;
    }

    /**
     * Gets the value of the weatherError property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link WeatherErrorModel }{@code >}
     *     
     */
    public JAXBElement<WeatherErrorModel> getWeatherError() {
        return weatherError;
    }

    /**
     * Sets the value of the weatherError property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link WeatherErrorModel }{@code >}
     *     
     */
    public void setWeatherError(JAXBElement<WeatherErrorModel> value) {
        this.weatherError = value;
    }

    /**
     * Gets the value of the hasError property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHasError() {
        return hasError;
    }

    /**
     * Sets the value of the hasError property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHasError(Boolean value) {
        this.hasError = value;
    }

}
