
package net.ipmdecisions.weather.datasourceadapters.dmi.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WeatherDataModel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WeatherDataModel">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Airtemp" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="Glorad" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="Evapo" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="Mintemp" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="Maxtemp" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="Soiltemp" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="Airrh" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="Prec" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="Sunrad" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="Windspeed" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="Winddir" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="Leafwet" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="AirtempSum" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="DateDay" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="DataSource" type="{http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract}WeatherDataSource" minOccurs="0"/>
 *         &lt;element name="ExpectedDataSource" type="{http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract}WeatherDataSource" minOccurs="0"/>
 *         &lt;element name="WeatherDataVariables" type="{http://schemas.datacontract.org/2004/07/System}ArrayOfNullableOfdouble" minOccurs="0"/>
 *         &lt;element name="WeatherDataSources" type="{http://schemas.datacontract.org/2004/07/System}ArrayOfNullableOfWeatherDataSourcev_PY1Rzz9" minOccurs="0"/>
 *         &lt;element name="PrecNoCor" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="AirtempSpecial" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="PrecSpecial" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="DifradSpecial" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="GloradSpecial" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="WindspeedSpecial" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="WinddirSpecial" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="LeafwetSpecial" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="AirrhSpecial" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeatherDataModel", propOrder = {
    "airtemp",
    "glorad",
    "evapo",
    "mintemp",
    "maxtemp",
    "soiltemp",
    "airrh",
    "prec",
    "sunrad",
    "windspeed",
    "winddir",
    "leafwet",
    "airtempSum",
    "dateDay",
    "dataSource",
    "expectedDataSource",
    "weatherDataVariables",
    "weatherDataSources",
    "precNoCor",
    "airtempSpecial",
    "precSpecial",
    "difradSpecial",
    "gloradSpecial",
    "windspeedSpecial",
    "winddirSpecial",
    "leafwetSpecial",
    "airrhSpecial"
})
public class WeatherDataModel {

    @XmlElementRef(name = "Airtemp", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> airtemp;
    @XmlElementRef(name = "Glorad", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> glorad;
    @XmlElementRef(name = "Evapo", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> evapo;
    @XmlElementRef(name = "Mintemp", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> mintemp;
    @XmlElementRef(name = "Maxtemp", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> maxtemp;
    @XmlElementRef(name = "Soiltemp", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> soiltemp;
    @XmlElementRef(name = "Airrh", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> airrh;
    @XmlElementRef(name = "Prec", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> prec;
    @XmlElementRef(name = "Sunrad", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> sunrad;
    @XmlElementRef(name = "Windspeed", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> windspeed;
    @XmlElementRef(name = "Winddir", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> winddir;
    @XmlElementRef(name = "Leafwet", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> leafwet;
    @XmlElementRef(name = "AirtempSum", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> airtempSum;
    @XmlElement(name = "DateDay")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateDay;
    @XmlElement(name = "DataSource")
    @XmlSchemaType(name = "string")
    protected WeatherDataSource dataSource;
    @XmlElement(name = "ExpectedDataSource")
    @XmlSchemaType(name = "string")
    protected WeatherDataSource expectedDataSource;
    @XmlElementRef(name = "WeatherDataVariables", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfNullableOfdouble> weatherDataVariables;
    @XmlElementRef(name = "WeatherDataSources", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfNullableOfWeatherDataSourcevPY1Rzz9> weatherDataSources;
    @XmlElementRef(name = "PrecNoCor", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> precNoCor;
    @XmlElementRef(name = "AirtempSpecial", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> airtempSpecial;
    @XmlElementRef(name = "PrecSpecial", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> precSpecial;
    @XmlElementRef(name = "DifradSpecial", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> difradSpecial;
    @XmlElementRef(name = "GloradSpecial", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> gloradSpecial;
    @XmlElementRef(name = "WindspeedSpecial", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> windspeedSpecial;
    @XmlElementRef(name = "WinddirSpecial", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> winddirSpecial;
    @XmlElementRef(name = "LeafwetSpecial", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> leafwetSpecial;
    @XmlElementRef(name = "AirrhSpecial", namespace = "http://schemas.datacontract.org/2004/07/DMIWeatherService.DataContract", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> airrhSpecial;

    /**
     * Gets the value of the airtemp property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getAirtemp() {
        return airtemp;
    }

    /**
     * Sets the value of the airtemp property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setAirtemp(JAXBElement<Double> value) {
        this.airtemp = value;
    }

    /**
     * Gets the value of the glorad property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getGlorad() {
        return glorad;
    }

    /**
     * Sets the value of the glorad property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setGlorad(JAXBElement<Double> value) {
        this.glorad = value;
    }

    /**
     * Gets the value of the evapo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getEvapo() {
        return evapo;
    }

    /**
     * Sets the value of the evapo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setEvapo(JAXBElement<Double> value) {
        this.evapo = value;
    }

    /**
     * Gets the value of the mintemp property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getMintemp() {
        return mintemp;
    }

    /**
     * Sets the value of the mintemp property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setMintemp(JAXBElement<Double> value) {
        this.mintemp = value;
    }

    /**
     * Gets the value of the maxtemp property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getMaxtemp() {
        return maxtemp;
    }

    /**
     * Sets the value of the maxtemp property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setMaxtemp(JAXBElement<Double> value) {
        this.maxtemp = value;
    }

    /**
     * Gets the value of the soiltemp property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getSoiltemp() {
        return soiltemp;
    }

    /**
     * Sets the value of the soiltemp property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setSoiltemp(JAXBElement<Double> value) {
        this.soiltemp = value;
    }

    /**
     * Gets the value of the airrh property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getAirrh() {
        return airrh;
    }

    /**
     * Sets the value of the airrh property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setAirrh(JAXBElement<Double> value) {
        this.airrh = value;
    }

    /**
     * Gets the value of the prec property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getPrec() {
        return prec;
    }

    /**
     * Sets the value of the prec property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setPrec(JAXBElement<Double> value) {
        this.prec = value;
    }

    /**
     * Gets the value of the sunrad property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getSunrad() {
        return sunrad;
    }

    /**
     * Sets the value of the sunrad property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setSunrad(JAXBElement<Double> value) {
        this.sunrad = value;
    }

    /**
     * Gets the value of the windspeed property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getWindspeed() {
        return windspeed;
    }

    /**
     * Sets the value of the windspeed property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setWindspeed(JAXBElement<Double> value) {
        this.windspeed = value;
    }

    /**
     * Gets the value of the winddir property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getWinddir() {
        return winddir;
    }

    /**
     * Sets the value of the winddir property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setWinddir(JAXBElement<Double> value) {
        this.winddir = value;
    }

    /**
     * Gets the value of the leafwet property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getLeafwet() {
        return leafwet;
    }

    /**
     * Sets the value of the leafwet property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setLeafwet(JAXBElement<Double> value) {
        this.leafwet = value;
    }

    /**
     * Gets the value of the airtempSum property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getAirtempSum() {
        return airtempSum;
    }

    /**
     * Sets the value of the airtempSum property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setAirtempSum(JAXBElement<Double> value) {
        this.airtempSum = value;
    }

    /**
     * Gets the value of the dateDay property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateDay() {
        return dateDay;
    }

    /**
     * Sets the value of the dateDay property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateDay(XMLGregorianCalendar value) {
        this.dateDay = value;
    }

    /**
     * Gets the value of the dataSource property.
     * 
     * @return
     *     possible object is
     *     {@link WeatherDataSource }
     *     
     */
    public WeatherDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Sets the value of the dataSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link WeatherDataSource }
     *     
     */
    public void setDataSource(WeatherDataSource value) {
        this.dataSource = value;
    }

    /**
     * Gets the value of the expectedDataSource property.
     * 
     * @return
     *     possible object is
     *     {@link WeatherDataSource }
     *     
     */
    public WeatherDataSource getExpectedDataSource() {
        return expectedDataSource;
    }

    /**
     * Sets the value of the expectedDataSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link WeatherDataSource }
     *     
     */
    public void setExpectedDataSource(WeatherDataSource value) {
        this.expectedDataSource = value;
    }

    /**
     * Gets the value of the weatherDataVariables property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfNullableOfdouble }{@code >}
     *     
     */
    public JAXBElement<ArrayOfNullableOfdouble> getWeatherDataVariables() {
        return weatherDataVariables;
    }

    /**
     * Sets the value of the weatherDataVariables property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfNullableOfdouble }{@code >}
     *     
     */
    public void setWeatherDataVariables(JAXBElement<ArrayOfNullableOfdouble> value) {
        this.weatherDataVariables = value;
    }

    /**
     * Gets the value of the weatherDataSources property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfNullableOfWeatherDataSourcevPY1Rzz9 }{@code >}
     *     
     */
    public JAXBElement<ArrayOfNullableOfWeatherDataSourcevPY1Rzz9> getWeatherDataSources() {
        return weatherDataSources;
    }

    /**
     * Sets the value of the weatherDataSources property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfNullableOfWeatherDataSourcevPY1Rzz9 }{@code >}
     *     
     */
    public void setWeatherDataSources(JAXBElement<ArrayOfNullableOfWeatherDataSourcevPY1Rzz9> value) {
        this.weatherDataSources = value;
    }

    /**
     * Gets the value of the precNoCor property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getPrecNoCor() {
        return precNoCor;
    }

    /**
     * Sets the value of the precNoCor property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setPrecNoCor(JAXBElement<Double> value) {
        this.precNoCor = value;
    }

    /**
     * Gets the value of the airtempSpecial property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getAirtempSpecial() {
        return airtempSpecial;
    }

    /**
     * Sets the value of the airtempSpecial property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setAirtempSpecial(JAXBElement<Double> value) {
        this.airtempSpecial = value;
    }

    /**
     * Gets the value of the precSpecial property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getPrecSpecial() {
        return precSpecial;
    }

    /**
     * Sets the value of the precSpecial property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setPrecSpecial(JAXBElement<Double> value) {
        this.precSpecial = value;
    }

    /**
     * Gets the value of the difradSpecial property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getDifradSpecial() {
        return difradSpecial;
    }

    /**
     * Sets the value of the difradSpecial property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setDifradSpecial(JAXBElement<Double> value) {
        this.difradSpecial = value;
    }

    /**
     * Gets the value of the gloradSpecial property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getGloradSpecial() {
        return gloradSpecial;
    }

    /**
     * Sets the value of the gloradSpecial property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setGloradSpecial(JAXBElement<Double> value) {
        this.gloradSpecial = value;
    }

    /**
     * Gets the value of the windspeedSpecial property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getWindspeedSpecial() {
        return windspeedSpecial;
    }

    /**
     * Sets the value of the windspeedSpecial property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setWindspeedSpecial(JAXBElement<Double> value) {
        this.windspeedSpecial = value;
    }

    /**
     * Gets the value of the winddirSpecial property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getWinddirSpecial() {
        return winddirSpecial;
    }

    /**
     * Sets the value of the winddirSpecial property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setWinddirSpecial(JAXBElement<Double> value) {
        this.winddirSpecial = value;
    }

    /**
     * Gets the value of the leafwetSpecial property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getLeafwetSpecial() {
        return leafwetSpecial;
    }

    /**
     * Sets the value of the leafwetSpecial property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setLeafwetSpecial(JAXBElement<Double> value) {
        this.leafwetSpecial = value;
    }

    /**
     * Gets the value of the airrhSpecial property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getAirrhSpecial() {
        return airrhSpecial;
    }

    /**
     * Sets the value of the airrhSpecial property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setAirrhSpecial(JAXBElement<Double> value) {
        this.airrhSpecial = value;
    }

}
