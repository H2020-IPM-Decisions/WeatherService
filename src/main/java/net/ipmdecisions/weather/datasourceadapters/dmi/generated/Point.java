
package net.ipmdecisions.weather.datasourceadapters.dmi.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Point complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Point">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="X" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="Y" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="UtmE" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="UtmN" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Point", propOrder = {
    "x",
    "y",
    "utmE",
    "utmN"
})
public class Point {

    @XmlElement(name = "X")
    protected Float x;
    @XmlElement(name = "Y")
    protected Float y;
    @XmlElement(name = "UtmE")
    protected Integer utmE;
    @XmlElement(name = "UtmN")
    protected Integer utmN;

    /**
     * Gets the value of the x property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setX(Float value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setY(Float value) {
        this.y = value;
    }

    /**
     * Gets the value of the utmE property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUtmE() {
        return utmE;
    }

    /**
     * Sets the value of the utmE property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUtmE(Integer value) {
        this.utmE = value;
    }

    /**
     * Gets the value of the utmN property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUtmN() {
        return utmN;
    }

    /**
     * Sets the value of the utmN property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUtmN(Integer value) {
        this.utmN = value;
    }

}
