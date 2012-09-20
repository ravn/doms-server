package dk.statsbiblioteket.doms.central.connectors.fedora.methods;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.fedora.methods.generated.Method;
import dk.statsbiblioteket.util.Pair;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 9/13/12
 * Time: 10:54 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Methods {

    public List<Method> getStaticMethods(
            String cmpid)
            throws BackendInvalidCredsException, BackendMethodFailedException, BackendInvalidResourceException;

    public List<Method> getDynamicMethods(
            String objpid)
            throws BackendInvalidCredsException, BackendMethodFailedException, BackendInvalidResourceException;


    public String invokeMethod(String cmpid,String methodName,List<Pair<String,String>> parameters, String logMessage)
            throws BackendInvalidCredsException, BackendMethodFailedException,
            BackendInvalidResourceException;
}
