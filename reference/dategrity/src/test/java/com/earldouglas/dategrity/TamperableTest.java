package com.earldouglas.dategrity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Session;
import org.junit.Test;

public class TamperableTest extends HibernateTest {

	@Test
	public void testTamper() throws SQLException {
		Session session = session();
		session.beginTransaction();

		User user = new User(1);
		session.save(user);

		session.getTransaction().commit();

		session = session();

		String tamperedPassword = "tampered!";
		connection().createStatement().execute(
				"update User set password = '" + tamperedPassword + "'");

		session.beginTransaction();

		User candidateUser = (User) session.createCriteria(User.class)
				.uniqueResult();

		assertTrue(user != candidateUser);
		assertEquals(user.getId(), candidateUser.getId());
		assertEquals(user.getName(), candidateUser.getName());
		assertEquals(user.getUsername(), candidateUser.getUsername());
		assertEquals(tamperedPassword, candidateUser.getPassword());
		assertTrue(candidateUser.tampered());

		session.getTransaction().commit();
	}

	@Test
	public void testNominal() throws SQLException {
		Session session = session();
		session.beginTransaction();

		User user = new User(1);
		session.save(user);

		session.getTransaction().commit();

		session = session();

		ResultSet resultSet = connection().createStatement().executeQuery(
				"select tamperCode from User");

		if (resultSet.next()) {
			assertEquals(user.hashCode(), resultSet.getInt("tamperCode"));
		}

		session.beginTransaction();

		User candidateUser = (User) session.createCriteria(User.class)
				.uniqueResult();

		assertTrue(user != candidateUser);
		assertEquals(user.getId(), candidateUser.getId());
		assertEquals(user.getName(), candidateUser.getName());
		assertEquals(user.getUsername(), candidateUser.getUsername());
		assertEquals(user.getPassword(), candidateUser.getPassword());
		assertFalse(candidateUser.tampered());

		session.getTransaction().commit();
	}
}
