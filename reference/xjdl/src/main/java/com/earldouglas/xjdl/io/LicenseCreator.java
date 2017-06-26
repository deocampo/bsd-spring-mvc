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

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.earldouglas.xjdl.License;

public class LicenseCreator {

	public String encryptLicense(License license, String key) throws Exception, NoSuchAlgorithmException, NoSuchPaddingException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(license);
		objectOutputStream.close();
		byte[] serializedLicense = byteArrayOutputStream.toByteArray();

		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		byte[] encryptedLicense = cipher.doFinal(serializedLicense);
		String encodedLicense = Base64.encodeBase64String(encryptedLicense);
		encodedLicense = encodedLicense.replaceAll("\n", "");
		encodedLicense = encodedLicense.replaceAll("\r", "");
		return encodedLicense;
	}
}
