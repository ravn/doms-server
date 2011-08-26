package dk.statsbiblioteket.doms.central.connectors.fedora.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for objectFieldsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="objectFieldsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="label" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="mDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "objectFieldsType", namespace = "http://www.fedora.info/definitions/1/0/types/", propOrder = {
    "pid",
    "label",
    "state",
    "cDate",
    "mDate"
})
public class ObjectFieldsType {

    @XmlElement(namespace = "http://www.fedora.info/definitions/1/0/types/", required = true)
    protected String pid;
    @XmlElement(namespace = "http://www.fedora.info/definitions/1/0/types/", required = true)
    protected String label;
    @XmlElement(namespace = "http://www.fedora.info/definitions/1/0/types/", required = true)
    protected String state;
    @XmlElement(namespace = "http://www.fedora.info/definitions/1/0/types/", required = true)
    protected String cDate;
    @XmlElement(namespace = "http://www.fedora.info/definitions/1/0/types/", required = true)
    protected String mDate;

    /**
     * Gets the value of the pid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPid() {
        return pid;
    }

    /**
     * Sets the value of the pid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPid(String value) {
        this.pid = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setState(String value) {
        this.state = value;
    }

    /**
     * Gets the value of the cDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCDate() {
        return cDate;
    }

    /**
     * Sets the value of the cDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCDate(String value) {
        this.cDate = value;
    }

    /**
     * Gets the value of the mDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMDate() {
        return mDate;
    }

    /**
     * Sets the value of the mDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMDate(String value) {
        this.mDate = value;
    }

}
