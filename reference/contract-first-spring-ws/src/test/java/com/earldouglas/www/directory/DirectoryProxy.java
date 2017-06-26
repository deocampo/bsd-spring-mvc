package com.earldouglas.www.directory;

public class DirectoryProxy implements com.earldouglas.www.directory.Directory {
  private String _endpoint = null;
  private com.earldouglas.www.directory.Directory directory = null;
  
  public DirectoryProxy() {
    _initDirectoryProxy();
  }
  
  public DirectoryProxy(String endpoint) {
    _endpoint = endpoint;
    _initDirectoryProxy();
  }
  
  private void _initDirectoryProxy() {
    try {
      directory = (new com.earldouglas.www.directory.DirectoryServiceLocator()).getDirectorySoap11();
      if (directory != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)directory)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)directory)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (directory != null)
      ((javax.xml.rpc.Stub)directory)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.earldouglas.www.directory.Directory getDirectory() {
    if (directory == null)
      _initDirectoryProxy();
    return directory;
  }
  
  public com.earldouglas.www.directory.Employee employee(com.earldouglas.www.directory.Id employeeRequest) throws java.rmi.RemoteException{
    if (directory == null)
      _initDirectoryProxy();
    return directory.employee(employeeRequest);
  }
  
  
}