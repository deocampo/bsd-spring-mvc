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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.earldouglas.xjdl.exception.LicenseInvalidException;
import com.earldouglas.xjdl.io.FileLicenseLoader;
import com.earldouglas.xjdl.io.LicenseLoader;

@Aspect
public class LicenseManager {

	private Log log = LogFactory.getLog(getClass());
	private LicenseLoader licenseLoader;

	public void setLicenseLoader(LicenseLoader licenseLoader) {
		this.licenseLoader = licenseLoader;
	}

	public LicenseLoader getLicenseLoader() {
		if (licenseLoader == null) {
			FileLicenseLoader fileLicenseLoader = new FileLicenseLoader();
			fileLicenseLoader.setLicenseFile("classpath:license");
			licenseLoader = fileLicenseLoader;
		}
		return licenseLoader;
	}

	@Before("@annotation(com.earldouglas.xjdl.Licensed)")
	public void authorize(JoinPoint joinPoint) {
		License license = getLicenseLoader().loadLicense();

		if (license == null) {
			log.warn("unlicensed feature: " + joinPoint.toLongString());
			throw new LicenseInvalidException();
		} else {
			license.authorize();
		}
	}
}
