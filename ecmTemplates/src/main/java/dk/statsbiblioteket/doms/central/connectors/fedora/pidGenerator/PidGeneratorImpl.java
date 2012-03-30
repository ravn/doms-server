package dk.statsbiblioteket.doms.central.connectors.fedora.pidGenerator;

import dk.statsbiblioteket.doms.pidgenerator.CommunicationException;
import dk.statsbiblioteket.doms.pidgenerator.PidGeneratorSoapWebservice;
import dk.statsbiblioteket.doms.pidgenerator.PidGeneratorSoapWebserviceService;
import dk.statsbiblioteket.doms.webservices.configuration.ConfigCollection;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 3/29/12
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class PidGeneratorImpl implements PidGenerator {

    private PidGeneratorSoapWebservice pidgen;


    public PidGeneratorImpl(String pidGenLocation) throws PIDGeneratorException {


        URL WSDLLOCATION = null;
        try {
            WSDLLOCATION = new URL(pidGenLocation);
        } catch (MalformedURLException e) {
            throw new PIDGeneratorException("Failed to parse the location of the pidgenerator service",e);
        }

        PidGeneratorSoapWebserviceService service
                = new PidGeneratorSoapWebserviceService(WSDLLOCATION,
                                                        new QName(
                                                                "http://pidgenerator.doms.statsbiblioteket.dk/",
                                                                "PidGeneratorSoapWebserviceService"));
        pidgen = service.getPort(PidGeneratorSoapWebservice.class);
    }

    /**
     * Generate the next available PID.
     *
     * @param infix A string, all or part of which may be used as part of the
     * PID, but with no guarantee. May be left empty.
     * @return The next available (unique) PID, possibly including (part of) the
     * requested infix.
     */
    public String generateNextAvailablePID(String infix)
            throws PIDGeneratorException {

        try {
            return pidgen.generatePidWithInfix(infix);
        } catch (CommunicationException e) {
            throw new PIDGeneratorException("Encountered a communication problem with the pidgenerator webservice",e);
        }
    }

}
