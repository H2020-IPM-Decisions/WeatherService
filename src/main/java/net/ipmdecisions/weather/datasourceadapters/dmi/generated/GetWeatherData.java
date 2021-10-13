
package net.ipmdecisions.weather.datasourceadapters.dmi.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element name="X_Coordinate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Y_Coordinate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isUTM" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="UTMZone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="interval" type="{http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract}WeatherInterval" minOccurs="0"/>
 *         &lt;element name="sources" type="{http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract}ArrayOfWeatherDataSource" minOccurs="0"/>
 *         &lt;element name="DateFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="DateTo" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="weatherDataParameters" type="{http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract}ArrayOfWeatherDataParameter" minOccurs="0"/>
 *         &lt;element name="baseTempValue" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="includeTempSum" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="application" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="agroId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "xCoordinate",
    "yCoordinate",
    "isUTM",
    "utmZone",
    "interval",
    "sources",
    "dateFrom",
    "dateTo",
    "weatherDataParameters",
    "baseTempValue",
    "includeTempSum",
    "application",
    "agroId"
})
@XmlRootElement(name = "GetWeatherData", namespace = "http://tempuri.org/")
public class GetWeatherData {

    @XmlElementRef(name = "X_Coordinate", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> xCoordinate;
    @XmlElementRef(name = "Y_Coordinate", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> yCoordinate;
    @XmlElement(namespace = "http://tempuri.org/")
    protected Boolean isUTM;
    @XmlElementRef(name = "UTMZone", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> utmZone;
    @XmlElement(namespace = "http://tempuri.org/")
    @XmlSchemaType(name = "string")
    protected WeatherInterval interval;
    @XmlElementRef(name = "sources", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfWeatherDataSource> sources;
    @XmlElement(name = "DateFrom", namespace = "http://tempuri.org/")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateFrom;
    @XmlElement(name = "DateTo", namespace = "http://tempuri.org/")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateTo;
    @XmlElementRef(name = "weatherDataParameters", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfWeatherDataParameter> weatherDataParameters;
    @XmlElement(namespace = "http://tempuri.org/")
    protected Double baseTempValue;
    @XmlElement(namespace = "http://tempuri.org/")
    protected Boolean includeTempSum;
    @XmlElementRef(name = "application", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> application;
    @XmlElementRef(name = "agroId", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> agroId;

    /**
     * Gets the value of the xCoordinate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getXCoordinate() {
        return xCoordinate;
    }

    /**
     * Sets the value of the xCoordinate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setXCoordinate(JAXBElement<String> value) {
        this.xCoordinate = value;
    }

    /**
     * Gets the value of the yCoordinate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getYCoordinate() {
        return yCoordinate;
    }

    /**
     * Sets the value of the yCoordinate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setYCoordinate(JAXBElement<String> value) {
        this.yCoordinate = value;
    }

    /**
     * Gets the value of the isUTM property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsUTM() {
        return isUTM;
    }

    /**
     * Sets the value of the isUTM property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsUTM(Boolean value) {
        this.isUTM = value;
    }

    /**
     * Gets the value of the utmZone property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUTMZone() {
        return utmZone;
    }

    /**
     * Sets the value of the utmZone property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUTMZone(JAXBElement<String> value) {
        this.utmZone = value;
    }

    /**
     * Gets the value of the interval property.
     * 
     * @return
     *     possible object is
     *     {@link WeatherInterval }
     *     
     */
    public WeatherInterval getInterval() {
        return interval;
    }

    /**
     * Sets the value of the interval property.
     * 
     * @param value
     *     allowed object is
     *     {@link WeatherInterval }
     *     
     */
    public void setInterval(WeatherInterval value) {
        this.interval = value;
    }

    /**
     * Gets the value of the sources property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataSource }{@code >}
     *     
     */
    public JAXBElement<ArrayOfWeatherDataSource> getSources() {
        return sources;
    }

    /**
     * Sets the value of the sources property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataSource }{@code >}
     *     
     */
    public void setSources(JAXBElement<ArrayOfWeatherDataSource> value) {
        this.sources = value;
    }

    /**
     * Gets the value of the dateFrom property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateFrom() {
        return dateFrom;
    }

    /**
     * Sets the value of the dateFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateFrom(XMLGregorianCalendar value) {
        this.dateFrom = value;
    }

    /**
     * Gets the value of the dateTo property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateTo() {
        return dateTo;
    }

    /**
     * Sets the value of the dateTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateTo(XMLGregorianCalendar value) {
        this.dateTo = value;
    }

    /**
     * Gets the value of the weatherDataParameters property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataParameter }{@code >}
     *     
     */
    public JAXBElement<ArrayOfWeatherDataParameter> getWeatherDataParameters() {
        return weatherDataParameters;
    }

    /**
     * Sets the value of the weatherDataParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfWeatherDataParameter }{@code >}
     *     
     */
    public void setWeatherDataParameters(JAXBElement<ArrayOfWeatherDataParameter> value) {
        this.weatherDataParameters = value;
    }

    /**
     * Gets the value of the baseTempValue property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBaseTempValue() {
        return baseTempValue;
    }

    /**
     * Sets the value of the baseTempValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBaseTempValue(Double value) {
        this.baseTempValue = value;
    }

    /**
     * Gets the value of the includeTempSum property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeTempSum() {
        return includeTempSum;
    }

    /**
     * Sets the value of the includeTempSum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeTempSum(Boolean value) {
        this.includeTempSum = value;
    }

    /**
     * Gets the value of the application property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getApplication() {
        return application;
    }

    /**
     * Sets the value of the application property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setApplication(JAXBElement<String> value) {
        this.application = value;
    }

    /**
     * Gets the value of the agroId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAgroId() {
        return agroId;
    }

    /**
     * Sets the value of the agroId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAgroId(JAXBElement<String> value) {
        this.agroId = value;
    }

}
