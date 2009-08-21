/**
 * DomsPIDGeneratorService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dk.statsbiblioteket.doms.gen.pidgenerator;

public interface DomsPIDGeneratorService extends javax.xml.rpc.Service {

    /**
     * DomsPIDGeneratorService is a webservice for generating a PID
     * (Persistent ID).
     */
    public java.lang.String getDomsPIDGeneratorAddress();

    public DomsPIDGenerator_PortType getDomsPIDGenerator()
            throws javax.xml.rpc.ServiceException;

    public DomsPIDGenerator_PortType getDomsPIDGenerator(
            java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
