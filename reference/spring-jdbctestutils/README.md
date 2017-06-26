# Extending Spring's JdbcTestUtils

_23 Mar 2009_

[SPR-4545](http://jira.springframework.org/browse/SPR-4545) suggests an extension of `JdbcTestUtils` to add two simple JDBC-based `queryRow()` methods, which can be used to test other database functionality, such as Hibernate code.

```java
public class JdbcTestUtils {

    public static Map<String, Object> queryRow(JdbcTemplate jdbcTemplate, String tableName, String keyName, Object keyValue) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(keyName, keyValue);
        return queryRow(jdbcTemplate, tableName, keys);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> queryRow(JdbcTemplate jdbcTemplate, String tableName, final Map<String, Object> keys) {
        StringBuffer keyNamesStringBuffer = new StringBuffer();
        for (Object key : keys.keySet()) {
            if (keyNamesStringBuffer.length() > 0) {
                keyNamesStringBuffer.append(" ,");
            }
            keyNamesStringBuffer.append(key);
        }

        StringBuffer queryStringBuffer = new StringBuffer();
        queryStringBuffer.append("select ");
        queryStringBuffer.append(keyNamesStringBuffer);
        queryStringBuffer.append(" from ");
        queryStringBuffer.append(tableName);
        queryStringBuffer.append(" where ");

        StringBuffer keyValuesStringBuffer = new StringBuffer();
        for (Object key : keys.keySet()) {
            if (keyValuesStringBuffer.length() > 0) {
                keyValuesStringBuffer.append(" and");
            }
            keyValuesStringBuffer.append(key);
            keyValuesStringBuffer.append(" = ? ");
        }

        queryStringBuffer.append(keyValuesStringBuffer);

        return (Map<String, Object>)jdbcTemplate.queryForObject(queryStringBuffer.toString(), keys.values().toArray(), new RowMapper<Object>() {
            public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                Map<String, Object> rowMap = new HashMap<String, Object>();
                for (String key : keys.keySet()) {
                    rowMap.put(key, resultSet.getObject((String) key));
                }

                return rowMap;
            }
        });
    }    
```

Including the updated `JdbcTestUtils` with the prior project, [Database Access with Hibernate and Spring](https://github.com/JamesEarlDouglas/spring-hibernate), and updating `HibernateDaoTest`, the new `queryRow()` functionality can be easily tested and used to validate the data access code developed in that project.

```java

public class HibernateDaoTest {

  // . . .

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
```

The new `queryRow()` functionality retrieves a simple `Map` containing the specified key/value pair of `id`/`test identifiable 5`, validating the successful functionality of the Hibernate code. 
