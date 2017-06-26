package com.earldouglas.springhibernate;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.jdbc.JdbcTestUtils.queryRow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class HibernateDaoTest {

    @Autowired
    private HibernateDao hibernateDao;

    private Collection<TestIdentifiable> getTestIdentifiables() {
        Collection<TestIdentifiable> testIdentifiables = new ArrayList<TestIdentifiable>();

        for (int i = 0; i < 10; i++) {
            TestIdentifiable testIdentifiable = new TestIdentifiable();
            testIdentifiable.setId("test identifiable " + i);
            testIdentifiable.setData("some data for test identifiable " + i);

            testIdentifiables.add(testIdentifiable);
        }

        return testIdentifiables;
    }

    @Before
    public void populateDatabase() {
        for (TestIdentifiable testIdentifiable : getTestIdentifiables()) {
            hibernateDao.save(testIdentifiable);
        }
    }

    @After
    public void clearDatabase() {
        for (TestIdentifiable testIdentifiable : getTestIdentifiables()) {
            hibernateDao.delete(testIdentifiable);
        }
    }

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    @Test
    public void testSaveGet() throws Exception {

        TestIdentifiable testIdentifiable = hibernateDao.get(TestIdentifiable.class, "test identifiable 5");

        TestCase.assertNotNull(testIdentifiable);
        TestCase.assertEquals(testIdentifiable.getId(), "test identifiable 5");

        Map<String, Object> map = queryRow(jdbcTemplate, "TestIdentifiable", "id", "test identifiable 5");

        assertEquals(1, map.keySet().size());
        assertEquals("test identifiable 5", map.get("id"));
    }
}
