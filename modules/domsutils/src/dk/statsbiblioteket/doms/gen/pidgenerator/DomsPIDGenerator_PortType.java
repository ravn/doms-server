/**
 * DomsPIDGenerator_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dk.statsbiblioteket.doms.gen.pidgenerator;

public interface DomsPIDGenerator_PortType extends java.rmi.Remote {

    /**
     * Operation generateNextAvailablePID generates the next available
     * PID.
     */
    public java.lang.String generateNextAvailablePID(java.lang.String infix)
            throws java.rmi.RemoteException;
}
