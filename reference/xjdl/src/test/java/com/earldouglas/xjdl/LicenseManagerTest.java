/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.earldouglas.xjdl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.earldouglas.test.service.SampleService;
import com.earldouglas.xjdl.exception.LicenseCountExceededException;
import com.earldouglas.xjdl.exception.LicenseException;
import com.earldouglas.xjdl.exception.LicenseExpiredException;
import com.earldouglas.xjdl.exception.LicenseInvalidException;
import com.earldouglas.xjdl.exception.LicenseNotYetActiveException;
import com.earldouglas.xjdl.io.FileLicenseLoader;
import com.earldouglas.xjdl.io.LicenseLoader;

public class LicenseManagerTest {

	private LicenseManager licenseManager;
	private SampleService testService;
	private FileLicenseLoader testLicenseLoader;
	private License testLicense;

	public LicenseManagerTest() {
		String contextLocation = getClass().getName().replaceAll("\\.", "/") + "-context.xml";
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(contextLocation);
		licenseManager = applicationContext.getBean(LicenseManager.class);
		testService = applicationContext.getBean(SampleService.class);

		try {
			LicenseLoader defaultLicenseLoader = licenseManager.getLicenseLoader();
			assertTrue(defaultLicenseLoader instanceof FileLicenseLoader);
		} catch (Exception exception) {
		}

		testLicenseLoader = new FileLicenseLoader() {
			@Override
			public License loadLicense() {
				return testLicense;
			}
		};
		licenseManager.setLicenseLoader(testLicenseLoader);
	}

	@Test
	public void testLicense() {
		expectException(LicenseInvalidException.class);
	}

	@Test
	public void testBasicLicense() {
		BasicLicense basicLicense = new BasicLicense();
		testLicense = basicLicense;
		expectNoException();
		expectNoException();
		expectNoException();
		expectNoException();
	}

	@Test
	public void testDateLicense() {
		DateLicense dateLicense = new DateLicense();
		dateLicense.getEnd();
		dateLicense.getBegin();

		testLicense = dateLicense;
		expectException(LicenseInvalidException.class);

		Date beforeNow = new Date(0);
		Date afterNow = new Date(new Date().getTime() + 60 * 1000);

		dateLicense.setBegin(beforeNow);
		expectNoException();
		dateLicense.setBegin(afterNow);
		expectException(LicenseNotYetActiveException.class);

		dateLicense = new DateLicense();
		testLicense = dateLicense;

		dateLicense.setEnd(afterNow);
		expectNoException();
		dateLicense.setEnd(beforeNow);
		expectException(LicenseExpiredException.class);

		dateLicense.setBegin(afterNow);
		dateLicense.setEnd(beforeNow);
		expectException(LicenseInvalidException.class);
	}

	@Test
	public void testCountdownLicense() {
		CountdownLicense countdownLicense = new CountdownLicense();
		countdownLicense.setCount(2);
		testLicense = countdownLicense;
		expectNoException();
		assertEquals(1, countdownLicense.getCount());
		expectNoException();
		assertEquals(0, countdownLicense.getCount());
		expectException(LicenseCountExceededException.class);
		assertEquals(0, countdownLicense.getCount());
		expectException(LicenseCountExceededException.class);
		assertEquals(0, countdownLicense.getCount());
	}

	private void expectNoException() {
		int sum = testService.add(2, 3);
		assertEquals(5, sum);
	}

	private void expectException(Class<? extends LicenseException> exceptionType) {
		LicenseException licenseException = null;
		try {
			testService.add(2, 3);
		} catch (LicenseException caughtLicenseException) {
			licenseException = caughtLicenseException;
		}
		assertNotNull("expected exception was not thrown", licenseException);
		assertEquals("unexpected exception type", exceptionType, licenseException.getClass());
	}
}
