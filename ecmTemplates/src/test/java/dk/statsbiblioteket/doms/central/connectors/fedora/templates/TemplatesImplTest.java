package dk.statsbiblioteket.doms.central.connectors.fedora.templates;

import dk.statsbiblioteket.doms.central.connectors.fedora.Fedora;
import dk.statsbiblioteket.doms.central.connectors.fedora.FedoraRest;
import dk.statsbiblioteket.doms.central.connectors.fedora.inheritance.ContentModelInheritanceImpl;
import dk.statsbiblioteket.doms.central.connectors.fedora.pidGenerator.PidGeneratorImpl;
import dk.statsbiblioteket.doms.central.connectors.fedora.tripleStore.TripleStore;
import dk.statsbiblioteket.doms.central.connectors.fedora.tripleStore.TripleStoreRest;
import dk.statsbiblioteket.doms.webservices.authentication.Credentials;

/**
 * Created with IntelliJ IDEA. User: abr Date: 7/5/13 Time: 1:27 PM To change this template use File | Settings | File
 * Templates.
 */
public class TemplatesImplTest {
    @org.junit.Before
    public void setUp()
            throws
            Exception {

    }

    @org.junit.After
    public void tearDown()
            throws
            Exception {

    }

    @org.junit.Test
    public void testFindTemplatesFor()
            throws
            Exception {
        Credentials creds = new Credentials("fedoraAdmin", "fedoraAdminPass");
        Fedora fedora = new FedoraRest(creds, "http://alhena:7980/fedora");
        TripleStore ts = new TripleStoreRest(creds, "http://alhena:7980/fedora");
        TemplatesImpl
                templates =
                new TemplatesImpl(fedora, new PidGeneratorImpl(null), ts, new ContentModelInheritanceImpl(fedora, ts));

        templates.findTemplatesFor("doms:ContentModel_Program");
    }
}
