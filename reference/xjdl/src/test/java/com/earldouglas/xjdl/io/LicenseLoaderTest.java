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

package com.earldouglas.xjdl.io;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.earldouglas.xjdl.BasicLicense;
import com.earldouglas.xjdl.License;
import com.earldouglas.xjdl.exception.LicenseInvalidException;

public abstract class LicenseLoaderTest {

	protected abstract LicenseLoader invalidLicenseLoader();

	protected abstract LicenseLoader[] validLicenseLoaders() throws Exception;

	@Test
	public void testMissingLicense() {
		LicenseLoader invalidLicenseLoader = invalidLicenseLoader();

		boolean exceptionOccurred = false;
		try {
			invalidLicenseLoader.loadLicense();
		} catch (LicenseInvalidException exception) {
			exceptionOccurred = true;
		} finally {
			assertTrue(exceptionOccurred);
		}
	}

	@Test
	public void testValidLicense() throws Exception {
		for (LicenseLoader validLicenseLoader : validLicenseLoaders()) {
			License candidateLicense = null;
			candidateLicense = validLicenseLoader.loadLicense();
			assertNotNull(candidateLicense);
			assertTrue(candidateLicense instanceof BasicLicense);
		}
	}
}
