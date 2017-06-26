/**
 * DirectoryService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.earldouglas.www.directory;

public interface DirectoryService extends javax.xml.rpc.Service {
    public java.lang.String getDirectorySoap11Address();

    public com.earldouglas.www.directory.Directory getDirectorySoap11() throws javax.xml.rpc.ServiceException;

    public com.earldouglas.www.directory.Directory getDirectorySoap11(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
