package dk.statsbiblioteket.doms.central.connectors.fedora.search;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the info.fedora.definitions._1._0.types package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Result_QNAME = new QName("http://www.fedora.info/definitions/1/0/types/", "result");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: info.fedora.definitions._1._0.types
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ListSessionType }
     * 
     */
    public ListSessionType createListSessionType() {
        return new ListSessionType();
    }

    /**
     * Create an instance of {@link ResultListType }
     * 
     */
    public ResultListType createResultListType() {
        return new ResultListType();
    }

    /**
     * Create an instance of {@link ResultType }
     * 
     */
    public ResultType createResultType() {
        return new ResultType();
    }

    /**
     * Create an instance of {@link ObjectFieldsType }
     * 
     */
    public ObjectFieldsType createObjectFieldsType() {
        return new ObjectFieldsType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fedora.info/definitions/1/0/types/", name = "result")
    public JAXBElement<ResultType> createResult(ResultType value) {
        return new JAXBElement<ResultType>(_Result_QNAME, ResultType.class, null, value);
    }

}
