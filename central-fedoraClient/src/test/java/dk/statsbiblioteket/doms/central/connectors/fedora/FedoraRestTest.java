package dk.statsbiblioteket.doms.central.connectors.fedora;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.fedora.structures.DatastreamProfile;
import dk.statsbiblioteket.doms.central.connectors.fedora.utils.Constants;
import dk.statsbiblioteket.doms.webservices.authentication.Credentials;
import dk.statsbiblioteket.util.Strings;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;

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
    
    @Ignore
    @org.junit.Test
    public void testModifyDatastream() throws
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
        fedora.modifyDatastreamByValue(pid, "STREAM", null, null, "<foobar/>".getBytes(), null, "Hello World", null);
        DatastreamProfile profile = fedora.getDatastreamProfile(pid, "STREAM", null);
        try {
            fedora.modifyDatastreamByValue(pid, "STREAM", null, null, "<foobar>barfoo</foobar".getBytes(), null, "Hello World", profile.getCreated()/1000L - 10L);
            fail("Should throw " + ConcurrentModificationException.class.getSimpleName());
        } catch (ConcurrentModificationException e) {
            //expected
        }
    }
    @Test
    public void testInlineDatastreams() throws
                                        IOException,
                                        TransformerException,
                                        BackendInvalidResourceException,
                                        BackendMethodFailedException,
                                        BackendInvalidCredsException {
        String xml = Strings.flush(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("sampleExport.xml"));
        FedoraRest fedoraRest = Mockito.mock(FedoraRest.class);
        Mockito.when(fedoraRest.getXMLDatastreamContents(anyString(),anyString(),anyLong())).thenReturn("<testContent/>");
        Mockito.when(fedoraRest.modifyForDate(anyString(),anyLong())).thenCallRealMethod();
        Mockito.when(fedoraRest.stripHiddenDatastreams(anyString())).thenCallRealMethod();
        Mockito.when(fedoraRest.stripOldVersions(anyString())).thenCallRealMethod();
        Mockito.when(fedoraRest.getManagedXmlDatastreams(anyString())).thenCallRealMethod();
        Mockito.when(fedoraRest.inlineDatastream(anyString(),anyString(),anyString(),anyLong())).thenCallRealMethod();
        xml = fedoraRest.modifyForDate(xml, null);

        xml = fedoraRest.stripHiddenDatastreams(xml);

        xml = fedoraRest.stripOldVersions(xml);

        List<String> datastreamIDs = fedoraRest.getManagedXmlDatastreams(xml);
        for (String datastreamID : datastreamIDs) {
            xml = fedoraRest.inlineDatastream(xml, datastreamID, null, null);
        }
        System.out.println(xml);
    }

}
