package dk.statsbiblioteket.doms.central.connectors.fedora;

import dk.statsbiblioteket.doms.webservices.authentication.Credentials;
import org.junit.Ignore;

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
}
