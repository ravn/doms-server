package dk.statsbiblioteket.doms.central.connectors.fedora.inheritance;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 3/29/12
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ContentModelInheritance {
    List<String> getInheritedContentModels(String cmpid, Long asOfDateTime)
            throws BackendInvalidCredsException, BackendMethodFailedException;

    /**
     * @param cmpid the content model pid
     * @return an empty list
     */
    public List<String> getInheritingContentModels(String cmpid)
            throws BackendInvalidCredsException, BackendMethodFailedException;
}
