package dk.statsbiblioteket.doms.central.connectors.fedora.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resultType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resultType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="listSession" type="{http://www.fedora.info/definitions/1/0/types/}listSessionType"/>
 *         &lt;element name="resultList" type="{http://www.fedora.info/definitions/1/0/types/}resultListType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultType", namespace = "http://www.fedora.info/definitions/1/0/types/", propOrder = {
    "listSession",
    "resultList"
})
public class ResultType {

    @XmlElement(namespace = "http://www.fedora.info/definitions/1/0/types/", required = true)
    protected ListSessionType listSession;
    @XmlElement(namespace = "http://www.fedora.info/definitions/1/0/types/", required = true)
    protected ResultListType resultList;

    /**
     * Gets the value of the listSession property.
     * 
     * @return
     *     possible object is
     *     {@link ListSessionType }
     *     
     */
    public ListSessionType getListSession() {
        return listSession;
    }

    /**
     * Sets the value of the listSession property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListSessionType }
     *     
     */
    public void setListSession(ListSessionType value) {
        this.listSession = value;
    }

    /**
     * Gets the value of the resultList property.
     * 
     * @return
     *     possible object is
     *     {@link ResultListType }
     *     
     */
    public ResultListType getResultList() {
        return resultList;
    }

    /**
     * Sets the value of the resultList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultListType }
     *     
     */
    public void setResultList(ResultListType value) {
        this.resultList = value;
    }

}
