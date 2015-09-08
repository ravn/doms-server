package dk.statsbiblioteket.doms.central;

import dk.statsbiblioteket.doms.central.connectors.EnhancedFedora;
import dk.statsbiblioteket.doms.central.connectors.fedora.pidGenerator.PIDGeneratorException;
import dk.statsbiblioteket.doms.central.connectors.fedora.structures.FedoraRelation;
import dk.statsbiblioteket.doms.central.connectors.fedora.structures.ObjectType;
import dk.statsbiblioteket.doms.central.summasearch.SearchWS;
import dk.statsbiblioteket.sbutil.webservices.authentication.Credentials;
import dk.statsbiblioteket.util.Strings;
import org.hamcrest.MatcherAssert;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.lang.*;
import java.lang.String;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by abr on 9/8/15.
 */
public class CentralWebserviceImplIMockIT {

    private CentralWebservice mockCentral(final EnhancedFedora enhancedFedora, final SearchWS searchWS) throws JAXBException, PIDGeneratorException, MalformedURLException {
        CentralWebserviceImpl centralWebservice = new CentralWebserviceImpl() {
            @Override
            protected EnhancedFedora getEnhancedFedora(Credentials creds) throws MalformedURLException, PIDGeneratorException, JAXBException {
                return enhancedFedora;
            }

            @Override
            protected Credentials getCredentials() {
                return new Credentials("user","password");
            }

            @Override
            protected SearchWS getSearchWSService() throws MalformedURLException {
                return searchWS;
            }
        };
        centralWebservice.initialise();
        return centralWebservice;
    }

    @Test
    public void testGetObjectProfile() throws Exception {

        EnhancedFedora fedora = mock(EnhancedFedora.class);
        String value = "doms:testPid";
        dk.statsbiblioteket.doms.central.connectors.fedora.structures.ObjectProfile objectProfile = mockSimpleObjectProfile(value);
        when(fedora.getObjectProfile(eq(value), anyLong())).thenReturn(objectProfile);

        CentralWebservice central = mockCentral(fedora,null);

        ObjectProfile profile = central.getObjectProfile(value);
        assertThat(profile.getPid(), is(value));
        verify(fedora).getObjectProfile(value,null);
        verifyNoMoreInteractions(fedora);
    }

    @Test
    public void testSearch() throws Exception {
        EnhancedFedora fedora = mock(EnhancedFedora.class);
        SearchWS searchWS = mock(SearchWS.class);

        String name = "searchResponse.xml";
        when(searchWS.directJSON(eq("{\"search.document.resultfields\":\"recordID, domsshortrecord\"," +
                "\"search.document.query\":\"*\"," +
                "\"search.document.startindex\":0," +
                "\"search.document.maxrecords\":10}"))).thenReturn(read(name));

        CentralWebservice central = mockCentral(fedora,searchWS);

        SearchResultList result = central.findObjects("*", 0, 10);

        assertThat(result.getHitCount(),is(6276L));
        assertThat(result.getSearchResult().get(0).getPid(),is("uuid:510e1f30-6023-4f6f-bef5-c86c2fea3a45"));

    }

    private String read(String name) throws IOException {
        return Strings.flush(Thread.currentThread().getContextClassLoader().getResourceAsStream(name));
    }

    private dk.statsbiblioteket.doms.central.connectors.fedora.structures.ObjectProfile mockSimpleObjectProfile(String pid) {
        dk.statsbiblioteket.doms.central.connectors.fedora.structures.ObjectProfile objectProfile = new dk.statsbiblioteket.doms.central.connectors.fedora.structures.ObjectProfile();
        objectProfile.setPid(pid);
        objectProfile.setObjectCreatedDate(new Date());
        objectProfile.setObjectLastModifiedDate(new Date());
        objectProfile.setContentModels(Arrays.<java.lang.String>asList());
        objectProfile.setType(ObjectType.DATA_OBJECT);
        objectProfile.setDatastreams(Arrays.<dk.statsbiblioteket.doms.central.connectors.fedora.structures.DatastreamProfile>asList());
        objectProfile.setRelations(Arrays.<FedoraRelation>asList());
        return objectProfile;
    }
}