package dk.statsbiblioteket.doms.central.connectors.fedora.methods;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.fedora.Fedora;
import dk.statsbiblioteket.doms.central.connectors.fedora.inheritance.ContentModelInheritance;
import dk.statsbiblioteket.doms.central.connectors.fedora.methods.generated.Method;
import dk.statsbiblioteket.doms.central.connectors.fedora.tripleStore.TripleStore;
import dk.statsbiblioteket.util.Pair;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 9/13/12
 * Time: 10:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class MethodsImpl implements Methods{


    Fedora fedora;
    TripleStore ts;
    ContentModelInheritance inheritance;
    private Unmarshaller jaxb;


    public MethodsImpl(Fedora fedora, TripleStore ts, ContentModelInheritance inheritance) throws JAXBException {
        this.fedora = fedora;
        this.ts = ts;
        this.inheritance = inheritance;
        jaxb = JAXBContext.newInstance("dk.statsbiblioteket.doms.central.connectors.fedora.methods.generated").createUnmarshaller();
    }

    @Override
    public List<Method> getMethods(String cmpid) throws BackendInvalidCredsException, BackendMethodFailedException, BackendInvalidResourceException {
        String methodsXml = null;
        try {
            methodsXml = fedora.getXMLDatastreamContents(cmpid, "METHODS");
        } catch (BackendInvalidResourceException e) {
            return new ArrayList<Method>();
        }

        try {
            JAXBElement<dk.statsbiblioteket.doms.central.connectors.fedora.methods.generated.Methods> parsed = (JAXBElement<dk.statsbiblioteket.doms.central.connectors.fedora.methods.generated.Methods>) jaxb.unmarshal(new StringReader(methodsXml));
            return parsed.getValue().getMethod();
        } catch (JAXBException e) {
            throw new BackendMethodFailedException("failed to parse Methods definition",e);
        }
    }

    @Override
    public String invokeMethod(String cmpid,String methodName,List<Pair<String,String>> parameters, String logMessage){
        //TODO figure out username and password used for this connection
        //TODO extract the command string from the method
        //TODO replace the parameter values into the command string
        //Run the command string
        //If exit 0, return the std out
        //else return stdout+stderr

        return UUID.randomUUID().toString();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
