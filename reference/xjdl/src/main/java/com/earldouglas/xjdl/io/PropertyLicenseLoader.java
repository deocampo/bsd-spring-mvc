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
import java.io.FileInputStream;
import java.util.Properties;

import com.earldouglas.xjdl.License;

public class PropertyLicenseLoader extends LicenseLoader {

	private File propertiesFile;
	private String licenseProperty;

	public void setPropertiesFile(File propertiesFile) {
		this.propertiesFile = propertiesFile;
	}

	public void setLicenseProperty(String licenseProperty) {
		this.licenseProperty = licenseProperty;
	}

	@Override
	public License readLicense() throws Exception {
		Properties properties = new Properties();
		properties.load(new FileInputStream(propertiesFile));
		String encryptedLicense = properties.getProperty(licenseProperty);
		return decryptLicense(encryptedLicense);
	}
}
