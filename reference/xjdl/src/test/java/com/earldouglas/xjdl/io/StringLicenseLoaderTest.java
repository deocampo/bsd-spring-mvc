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

import com.earldouglas.xjdl.BasicLicense;
import com.earldouglas.xjdl.License;

public class StringLicenseLoaderTest extends LicenseLoaderTest {

	@Override
	protected LicenseLoader invalidLicenseLoader() {
		return new StringLicenseLoader();
	}

	@Override
	protected LicenseLoader[] validLicenseLoaders() throws Exception {
		String key = "encrypt!encrypt!";

		License license = new BasicLicense();
		String encryptedLicense = new LicenseCreator().encryptLicense(license, key);

		StringLicenseLoader stringLicenseLoader = new StringLicenseLoader();
		stringLicenseLoader.setKey(key);
		stringLicenseLoader.setLicense(encryptedLicense);

		return new LicenseLoader[] { stringLicenseLoader };
	}
}
