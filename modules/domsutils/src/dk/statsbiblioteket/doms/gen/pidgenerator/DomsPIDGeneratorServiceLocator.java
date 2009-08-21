/**
 * DomsPIDGeneratorServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dk.statsbiblioteket.doms.gen.pidgenerator;

public class DomsPIDGeneratorServiceLocator
        extends org.apache.axis.client.Service
        implements DomsPIDGeneratorService {

    /**
     * DomsPIDGeneratorService is a webservice for generating a PID
     * (Persistent ID).
     */

    public DomsPIDGeneratorServiceLocator() {
    }

    public DomsPIDGeneratorServiceLocator(
            org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public DomsPIDGeneratorServiceLocator(java.lang.String wsdlLoc,
                                          javax.xml.namespace.QName sName)
            throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for DomsPIDGenerator
    private java.lang.String DomsPIDGenerator_address
            = "http://localhost:8080/pidgenerator/services/DomsPIDGenerator";

    public java.lang.String getDomsPIDGeneratorAddress() {
        return DomsPIDGenerator_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String DomsPIDGeneratorWSDDServiceName
            = "DomsPIDGenerator";

    public java.lang.String getDomsPIDGeneratorWSDDServiceName() {
        return DomsPIDGeneratorWSDDServiceName;
    }

    public void setDomsPIDGeneratorWSDDServiceName(java.lang.String name) {
        DomsPIDGeneratorWSDDServiceName = name;
    }

    public DomsPIDGenerator_PortType getDomsPIDGenerator()
            throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(DomsPIDGenerator_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getDomsPIDGenerator(endpoint);
    }

    public DomsPIDGenerator_PortType getDomsPIDGenerator(
            java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            DomsPIDGeneratorSoapBindingStub _stub
                    = new DomsPIDGeneratorSoapBindingStub(portAddress, this);
            _stub.setPortName(getDomsPIDGeneratorWSDDServiceName());
            return _stub;
        } catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setDomsPIDGeneratorEndpointAddress(java.lang.String address) {
        DomsPIDGenerator_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface)
            throws javax.xml.rpc.ServiceException {
        try {
            if (DomsPIDGenerator_PortType.class
                    .isAssignableFrom(serviceEndpointInterface)) {
                DomsPIDGeneratorSoapBindingStub _stub
                        = new DomsPIDGeneratorSoapBindingStub(
                        new java.net.URL(DomsPIDGenerator_address), this);
                _stub.setPortName(getDomsPIDGeneratorWSDDServiceName());
                return _stub;
            }
        } catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException(
                "There is no stub implementation for the interface:  " + (
                        serviceEndpointInterface == null ? "null"
                                : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName,
                                   Class serviceEndpointInterface)
            throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("DomsPIDGenerator".equals(inputPortName)) {
            return getDomsPIDGenerator();
        } else {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName(
                "http://pidgenerator.doms.statsbiblioteket.dk",
                "DomsPIDGeneratorService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(
                    new javax.xml.namespace.QName(
                            "http://pidgenerator.doms.statsbiblioteket.dk",
                            "DomsPIDGenerator"));
        }
        return ports.iterator();
    }

    /** Set the endpoint address for the specified port name. */
    public void setEndpointAddress(java.lang.String portName,
                                   java.lang.String address)
            throws javax.xml.rpc.ServiceException {

        if ("DomsPIDGenerator".equals(portName)) {
            setDomsPIDGeneratorEndpointAddress(address);
        } else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(
                    " Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /** Set the endpoint address for the specified port name. */
    public void setEndpointAddress(javax.xml.namespace.QName portName,
                                   java.lang.String address)
            throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
