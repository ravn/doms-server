package dk.statsbiblioteket.doms.central.connectors.fedora;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.fedora.utils.Constants;
import dk.statsbiblioteket.doms.webservices.authentication.Credentials;
import org.junit.Ignore;

import java.net.MalformedURLException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class FedoraRestTest {

    @org.junit.Test
    @Ignore
    public void testNewEmptyObject() throws Exception {
        FedoraRest fedora = new FedoraRest(
                new Credentials("fedoraAdmin", "fedoraAdminPass"), "http://achernar:7880/fedora");
        String pid = fedora.newEmptyObject(
                "uuid:testPid", Arrays.asList("oldIdentfier1"), Arrays.asList("uuid:Batch"), "message");
        assertEquals(pid,"uuid:testPid");
    }

    @org.junit.Test
    @Ignore
    public void testAddRelations() throws
                                   MalformedURLException,
                                   BackendInvalidCredsException,
                                   BackendMethodFailedException, BackendInvalidResourceException {
        FedoraRest fedora = new FedoraRest(
                new Credentials("fedoraAdmin", "fedoraAdminPass"), "http://achernar:7880/fedora");
        String pid = "uuid:testPid2";
        try {

            fedora.newEmptyObject(
                    pid, Arrays.asList("oldIdentfier1"), Arrays.asList("uuid:Batch"), "message");
        } catch (Exception e){

        }
        fedora.addRelations(pid,null, Constants.RELATION_COLLECTION,Arrays.asList("uuid:test2","uuid:test3"),false,"comment");

    }
}
