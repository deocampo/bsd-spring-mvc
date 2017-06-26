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

import java.sql.Connection;
import java.sql.Statement;

import org.apache.commons.dbcp.BasicDataSource;
import org.hsqldb.jdbcDriver;
import org.junit.BeforeClass;

import com.earldouglas.xjdl.BasicLicense;
import com.earldouglas.xjdl.License;

public class JdbcLicenseLoaderTest extends LicenseLoaderTest {

	private static BasicDataSource basicDataSource;

	@BeforeClass
	public static void setupDatabase() throws Exception {
		basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName(jdbcDriver.class.getName());
		basicDataSource.setUrl("jdbc:hsqldb:mem:db");
		basicDataSource.setUsername("sa");
		basicDataSource.setPassword("");

		Connection connection = basicDataSource.getConnection();
		Statement statement = connection.createStatement();
		statement.execute("create table licenses (license varchar(512))");
	}

	@Override
	protected LicenseLoader invalidLicenseLoader() {
		JdbcLicenseLoader jdbcLicenseLoader = new JdbcLicenseLoader();
		jdbcLicenseLoader.setKey("encrypt!encrypt!");
		jdbcLicenseLoader.setDataSource(basicDataSource);
		return jdbcLicenseLoader;
	}

	@Override
	protected LicenseLoader[] validLicenseLoaders() throws Exception {
		String key = "encrypt!encrypt!";

		License license = new BasicLicense();
		String encryptedLicense = new LicenseCreator().encryptLicense(license, key);

		Connection connection = basicDataSource.getConnection();
		Statement statement = connection.createStatement();
		statement.execute("insert into licenses values('" + encryptedLicense + "')");

		JdbcLicenseLoader jdbcLicenseLoader = new JdbcLicenseLoader();
		jdbcLicenseLoader.setKey("encrypt!encrypt!");
		jdbcLicenseLoader.setDataSource(basicDataSource);

		return new LicenseLoader[] { jdbcLicenseLoader };
	}
}
