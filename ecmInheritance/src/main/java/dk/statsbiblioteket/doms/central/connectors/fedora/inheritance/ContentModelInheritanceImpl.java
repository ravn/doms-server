package dk.statsbiblioteket.doms.central.connectors.fedora.inheritance;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.fedora.Fedora;
import dk.statsbiblioteket.doms.central.connectors.fedora.structures.FedoraRelation;
import dk.statsbiblioteket.doms.central.connectors.fedora.tripleStore.TripleStore;
import dk.statsbiblioteket.doms.central.connectors.fedora.utils.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Created by IntelliJ IDEA. User: abr Date: 3/29/12 Time: 1:59 PM To change this template use File | Settings | File
 * Templates.
 */
public class ContentModelInheritanceImpl implements ContentModelInheritance {

    private Fedora fedora;
    private TripleStore ts;


    public ContentModelInheritanceImpl(Fedora fedora,
                                       TripleStore ts) {
        this.fedora = fedora;
        this.ts = ts;
    }
}
