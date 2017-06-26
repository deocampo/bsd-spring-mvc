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

import java.io.File;
import java.net.URL;

public class PropertyLicenseLoaderTest extends LicenseLoaderTest {

	@Override
	protected LicenseLoader invalidLicenseLoader() {
		return new PropertyLicenseLoader();
	}

	@Override
	protected LicenseLoader[] validLicenseLoaders() throws Exception {
		String key = "encrypt!encrypt!";

		String classPathPropertiesFile = getClass().getName().replaceAll("\\.", "/") + ".properties";

		URL licenseUrl = this.getClass().getClassLoader().getResource(classPathPropertiesFile);
		File propertiesFile = new File(licenseUrl.getFile());

		PropertyLicenseLoader propertyLicenseLoader = new PropertyLicenseLoader();
		propertyLicenseLoader.setKey(key);
		propertyLicenseLoader.setLicenseProperty("license");
		propertyLicenseLoader.setPropertiesFile(propertiesFile);

		return new LicenseLoader[] { propertyLicenseLoader };
	}
}
