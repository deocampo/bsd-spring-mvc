package com.earldouglas.springws.directory;

import java.math.BigInteger;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.springframework.oxm.Marshaller;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

import com.earldouglas.directory.Employee;
import com.earldouglas.directory.Id;

public class MarshallingDirectoryEndpoint extends
		AbstractMarshallingPayloadEndpoint {

	private Map<BigInteger, Employee> employees;

	public void setEmployees(Map<BigInteger, Employee> employees) {
		this.employees = employees;
	}

	public MarshallingDirectoryEndpoint(Marshaller marshaller) {
		super(marshaller);
	}

	@SuppressWarnings("unchecked")
	protected Object invokeInternal(Object request) throws Exception {
		Id id = ((JAXBElement<Id>) request).getValue();
		return employees.get(id.getId());
	}
}