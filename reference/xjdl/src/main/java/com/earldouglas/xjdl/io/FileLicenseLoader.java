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
import java.io.ObjectInputStream;
import java.net.URL;

import com.earldouglas.xjdl.License;

public class FileLicenseLoader extends LicenseLoader {

	private File licenseFile;

	public void setLicenseFile(File licenseFile) {
		this.licenseFile = licenseFile;
	}

	public void setLicenseFile(String licensePath) {
		if (licensePath.startsWith("classpath:")) {
			licensePath = licensePath.substring("classpath:".length());
			URL licenseUrl = this.getClass().getClassLoader().getResource(licensePath);
			licenseFile = new File(licenseUrl.getFile());
		} else {
			licenseFile = new File(licensePath);
		}
	}

	@Override
	public License readLicense() throws Exception {
		FileInputStream fileInputStream = new FileInputStream(licenseFile);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		String encryptedLicense = (String) objectInputStream.readObject();
		objectInputStream.close();
		return decryptLicense(encryptedLicense);
	}
}
