/**
 * DirectoryServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.earldouglas.www.directory;

public class DirectoryServiceLocator extends org.apache.axis.client.Service implements com.earldouglas.www.directory.DirectoryService {

    public DirectoryServiceLocator() {
    }


    public DirectoryServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public DirectoryServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for DirectorySoap11
    private java.lang.String DirectorySoap11_address = "http://localhost:8080/spring-ws/";

    public java.lang.String getDirectorySoap11Address() {
        return DirectorySoap11_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String DirectorySoap11WSDDServiceName = "DirectorySoap11";

    public java.lang.String getDirectorySoap11WSDDServiceName() {
        return DirectorySoap11WSDDServiceName;
    }

    public void setDirectorySoap11WSDDServiceName(java.lang.String name) {
        DirectorySoap11WSDDServiceName = name;
    }

    public com.earldouglas.www.directory.Directory getDirectorySoap11() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(DirectorySoap11_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getDirectorySoap11(endpoint);
    }

    public com.earldouglas.www.directory.Directory getDirectorySoap11(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.earldouglas.www.directory.DirectorySoap11Stub _stub = new com.earldouglas.www.directory.DirectorySoap11Stub(portAddress, this);
            _stub.setPortName(getDirectorySoap11WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setDirectorySoap11EndpointAddress(java.lang.String address) {
        DirectorySoap11_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.earldouglas.www.directory.Directory.class.isAssignableFrom(serviceEndpointInterface)) {
                com.earldouglas.www.directory.DirectorySoap11Stub _stub = new com.earldouglas.www.directory.DirectorySoap11Stub(new java.net.URL(DirectorySoap11_address), this);
                _stub.setPortName(getDirectorySoap11WSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("DirectorySoap11".equals(inputPortName)) {
            return getDirectorySoap11();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.earldouglas.com/directory", "DirectoryService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.earldouglas.com/directory", "DirectorySoap11"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("DirectorySoap11".equals(portName)) {
            setDirectorySoap11EndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
