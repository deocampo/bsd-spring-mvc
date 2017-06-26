package com.earldouglas.www.directory;

import java.math.BigInteger;

import junit.framework.TestCase;

import org.junit.Test;

public class DirectoryProxyTest extends TestCase {

	@Test
	public void testDirectoryProxy() throws Exception {
		DirectoryProxy directoryProxy = new DirectoryProxy();

		Id id = new Id();
		id.setId(BigInteger.valueOf(1));
		Employee employee = directoryProxy.employee(id);
		assertEquals(BigInteger.valueOf(1), employee.getId());
		assertEquals("Max Power", employee.getName());
		assertEquals("The Leader", employee.getTitle());
	}
}
