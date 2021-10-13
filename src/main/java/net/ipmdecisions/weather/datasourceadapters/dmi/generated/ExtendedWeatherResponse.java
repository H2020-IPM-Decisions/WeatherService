
package net.ipmdecisions.weather.datasourceadapters.dmi.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExtendedWeatherResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExtendedWeatherResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="WeatherPoint" type="{http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract}Point" minOccurs="0"/>
 *         &lt;element name="WeatherData" type="{http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract}WeatherResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtendedWeatherResponse", propOrder = {
    "weatherPoint",
    "weatherData"
})
public class ExtendedWeatherResponse {

    @XmlElementRef(name = "WeatherPoint", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Point> weatherPoint;
    @XmlElementRef(name = "WeatherData", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<WeatherResponse> weatherData;

    /**
     * Gets the value of the weatherPoint property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Point }{@code >}
     *     
     */
    public JAXBElement<Point> getWeatherPoint() {
        return weatherPoint;
    }

    /**
     * Sets the value of the weatherPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Point }{@code >}
     *     
     */
    public void setWeatherPoint(JAXBElement<Point> value) {
        this.weatherPoint = value;
    }

    /**
     * Gets the value of the weatherData property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link WeatherResponse }{@code >}
     *     
     */
    public JAXBElement<WeatherResponse> getWeatherData() {
        return weatherData;
    }

    /**
     * Sets the value of the weatherData property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link WeatherResponse }{@code >}
     *     
     */
    public void setWeatherData(JAXBElement<WeatherResponse> value) {
        this.weatherData = value;
    }

}
