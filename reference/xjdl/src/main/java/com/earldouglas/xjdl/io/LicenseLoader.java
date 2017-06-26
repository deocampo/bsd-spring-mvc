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

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.earldouglas.xjdl.License;
import com.earldouglas.xjdl.exception.LicenseException;
import com.earldouglas.xjdl.exception.LicenseInvalidException;

public abstract class LicenseLoader {

	private String key;

	public void setKey(String key) {
		this.key = key;
	}

	public License loadLicense() {
		try {
			return readLicense();
		} catch (Exception e) {
			LicenseException licenseException = new LicenseInvalidException();
			licenseException.setStackTrace(e.getStackTrace());
			throw (licenseException);
		}
	}

	protected abstract License readLicense() throws Exception;

	protected License decryptLicense(String encodedLicense) throws BadPaddingException, UnsupportedEncodingException, Exception {
		byte[] encryptedLicense = Base64.decodeBase64(encodedLicense);

		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		byte[] serializedLicense = cipher.doFinal(encryptedLicense);

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedLicense);
		ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
		License license = (License) objectInputStream.readObject();
		objectInputStream.close();
		return license;
	}
}