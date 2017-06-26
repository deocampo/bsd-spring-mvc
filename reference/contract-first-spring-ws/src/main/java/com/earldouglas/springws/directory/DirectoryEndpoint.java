package com.earldouglas.springws.directory;

import java.math.BigInteger;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.earldouglas.directory.Employee;
import com.earldouglas.directory.Id;

@Endpoint
public class DirectoryEndpoint {

	private Map<BigInteger, Employee> employees;

	public void setEmployees(Map<BigInteger, Employee> employees) {
		this.employees = employees;
	}

	@PayloadRoot(localPart = "employee-request", namespace = "http://www.earldouglas.com/directory")
	public Employee getEmployees(JAXBElement<Id> idElement) {
		return employees.get(idElement.getValue().getId());
	}
}
