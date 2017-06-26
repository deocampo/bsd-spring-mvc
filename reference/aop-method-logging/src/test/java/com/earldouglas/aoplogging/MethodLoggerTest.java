package com.earldouglas.aoplogging;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.earldouglas.greeter.Greeter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MethodLoggerTest {

	@Autowired
	private Greeter greeter;
	
	@Test
	public void testMethodLogger() {
		greeter.getGreeting();
	}
}
