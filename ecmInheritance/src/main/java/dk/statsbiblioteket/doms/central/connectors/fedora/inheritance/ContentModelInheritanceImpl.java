package dk.statsbiblioteket.doms.central.connectors.fedora.inheritance;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.fedora.Fedora;
import dk.statsbiblioteket.doms.central.connectors.fedora.structures.FedoraRelation;
import dk.statsbiblioteket.doms.central.connectors.fedora.tripleStore.TripleStore;
import dk.statsbiblioteket.doms.central.connectors.fedora.utils.Constants;
import dk.statsbiblioteket.doms.central.connectors.fedora.utils.FedoraUtil;

import java.util.ArrayList;
import java.util.Arrays;
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

    private List<String> getInheritedContentModelsBreadthFirst(List<String> contentmodels,
                                                               Long asOfDateTime)
            throws
            BackendInvalidCredsException,
            BackendMethodFailedException {


        /*
        bfs (Graph G) {
            all vertices of G are first painted white

            the graph root is painted gray and put in a queue

            while the queue is not empty {
                a vertex u is removed from the queue

                for all white successors v of u {
                    v is painted gray
                    v is added to the queue
                }

                u is painted black
            }
        }
        */

        //all vertices of G are first painted white
        //all content models are white if not in one of the sets grey or black
        Set<String> grey = new HashSet<String>();
        List<String> black = new ArrayList<String>();
        Queue<String> queue = new LinkedList<String>();

        //the graph root is painted gray and put in a queue
        for (String startingcontentmodel : contentmodels) {
            queue.add(startingcontentmodel);
            grey.add(startingcontentmodel);
        }

        //while the queue is not empty {
        while (queue.size() > 0) {
            //a vertex u is removed from the queue
            String u = queue.poll();

            //    for all white successors v of u {
            List<String> successor_of_u = getAncestors(u, asOfDateTime);
            for (String v : successor_of_u) {
                if (grey.contains(v) || black.contains(v)) {
                    continue;
                }

                //v is painted gray
                grey.add(v);
                //v is added to the queue
                queue.add(v);
            }
            //u is painted black
            black.add(u);
        }
        return black;
    }

    private List<String> getAncestors(String s,
                                      Long asOfDateTime)
            throws
            BackendInvalidCredsException,
            BackendMethodFailedException {
        List<String> temp = new ArrayList<String>();

        List<FedoraRelation> ancestors = null;
        try {
            ancestors = fedora.getNamedRelations(s, Constants.RELATION_EXTENDS_MODEL, asOfDateTime);
        } catch (BackendInvalidResourceException e) {
            //Content model does not exist, but that is not a problem. It just
            //does not have ancestors
            return temp;
        }
        for (FedoraRelation ancestor : ancestors) {
            temp.add(ancestor.getObject());
        }
        return temp;
    }


    @Override
    public List<String> getInheritedContentModels(String cmpid,
                                                  Long asOfDateTime)
            throws
            BackendInvalidCredsException,
            BackendMethodFailedException {
        cmpid = FedoraUtil.ensurePID(cmpid);
        return getInheritedContentModelsBreadthFirst(Arrays.asList(new String[]{cmpid}), asOfDateTime);
    }

    /**
     * @param cmpid the content model pid
     *
     * @return an empty list
     */
    public List<String> getInheritingContentModels(String cmpid)
            throws
            BackendInvalidCredsException,
            BackendMethodFailedException {
        cmpid = FedoraUtil.ensureURI(cmpid);
        //TODO sanitize label

        String
                query =
                "select $object \n"
                + "from <#ri>\n"
                + "where \n"
                + "walk(\n"
                + "$object <"
                + Constants.RELATION_EXTENDS_MODEL
                + "> <"
                + cmpid
                + ">\n"
                + "and\n"
                + "$object <"
                + Constants.RELATION_EXTENDS_MODEL
                + "> $temp\n"
                + ");";
        return ts.genericQuery(query);
    }

}
