package com.earldouglas.dategrity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Before;

public class HibernateTest {

	SessionFactory sessionFactory;
	SchemaExport schemaExport;

	public HibernateTest() {
		AnnotationConfiguration annotationConfiguration = new AnnotationConfiguration();
		annotationConfiguration.addAnnotatedClass(User.class);
		annotationConfiguration
				.configure("com/earldouglas/dategrity/hibernate.cfg.xml");
		sessionFactory = annotationConfiguration.buildSessionFactory();
		schemaExport = new SchemaExport(annotationConfiguration);
	}

	@Before
	public void export() {
		schemaExport.create(true, true);
	}

	public Session session() {
		return sessionFactory.getCurrentSession();
	}

	public Connection connection() throws SQLException {
		return DriverManager.getConnection("jdbc:hsqldb:mem:db", "sa", "");
	}
}
