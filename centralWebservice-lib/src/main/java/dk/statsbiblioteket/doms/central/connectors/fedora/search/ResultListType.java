package dk.statsbiblioteket.doms.central.connectors.fedora.search;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resultListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resultListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="objectFields" type="{http://www.fedora.info/definitions/1/0/types/}objectFieldsType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultListType", namespace = "http://www.fedora.info/definitions/1/0/types/", propOrder = {
    "objectFields"
})
public class ResultListType {

    @XmlElement(namespace = "http://www.fedora.info/definitions/1/0/types/")
    protected List<ObjectFieldsType> objectFields;

    /**
     * Gets the value of the objectFields property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the objectFields property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getObjectFields().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ObjectFieldsType }
     * 
     * 
     */
    public List<ObjectFieldsType> getObjectFields() {
        if (objectFields == null) {
            objectFields = new ArrayList<ObjectFieldsType>();
        }
        return this.objectFields;
    }

}
