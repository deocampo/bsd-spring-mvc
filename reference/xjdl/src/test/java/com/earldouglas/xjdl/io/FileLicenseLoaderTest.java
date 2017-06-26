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
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.URL;

import com.earldouglas.xjdl.BasicLicense;

public class FileLicenseLoaderTest extends LicenseLoaderTest {

	@Override
	protected LicenseLoader invalidLicenseLoader() {
		return new FileLicenseLoader();
	}

	@Override
	protected LicenseLoader[] validLicenseLoaders() throws Exception {
		String key = "encrypt!encrypt!";

		URL licenseUrl = getClass().getClassLoader().getResource("");
		File licenseFile = new File(licenseUrl.getFile() + File.separator + "license");

		String encryptedLicenseString = new LicenseCreator().encryptLicense(new BasicLicense(), key);
		FileOutputStream fileOutputStream = new FileOutputStream(licenseFile);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(encryptedLicenseString);
		objectOutputStream.close();

		FileLicenseLoader fileLicenseLoader = new FileLicenseLoader();
		fileLicenseLoader.setKey(key);
		fileLicenseLoader.setLicenseFile("classpath:license");

		FileLicenseLoader fileLicenseLoader1 = new FileLicenseLoader();
		fileLicenseLoader1.setKey(key);
		fileLicenseLoader1.setLicenseFile(licenseFile.getAbsolutePath());

		FileLicenseLoader fileLicenseLoader2 = new FileLicenseLoader();
		fileLicenseLoader2.setKey(key);
		fileLicenseLoader2.setLicenseFile(new File(licenseFile.getAbsolutePath()));

		return new LicenseLoader[] { fileLicenseLoader, fileLicenseLoader1, fileLicenseLoader2 };
	}
}
