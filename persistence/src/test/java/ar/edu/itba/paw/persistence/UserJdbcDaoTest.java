//package ar.edu.itba.paw.persistence;
//
//import ar.edu.itba.paw.interfaces.persistence.UserDao;
//import ar.edu.itba.paw.model.User;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.jdbc.JdbcTestUtils;
//
//import javax.sql.DataSource;
//
//import static org.junit.Assert.*;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TestConfig.class)
//public class UserJdbcDaoTest {
//
//    private static final String USERNAME = "test user";
//
//    private JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    private UserDao userDao;
//
//    @Autowired
//    private DataSource ds;
//
//    @Before
//    public void setUp() {
//        jdbcTemplate = new JdbcTemplate(ds);
//    }
//
//    @Test
//    public void testCreate() {
//        User user = userDao.create(USERNAME);
//
//        assertNotNull(user);
//        assertEquals(USERNAME, user.getUsername());
//        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", "userid = " + user.getId()));
//    }
//
//}
package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
public class UserJdbcDaoTest {
    private static final String USERNAME = "johndoe";
    private static final String EMAIL = "some@mail.com";

    @Autowired
    private DataSource ds;

    @Autowired
    private UserJdbcDao userJdbcDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"users");
    }

    @Test
    public void testCreate(){
        final User user = userJdbcDao.create(USERNAME,EMAIL);
        Assert.assertNotNull(user);
        Assert.assertEquals(USERNAME,user.getUsername());
        Assert.assertEquals(EMAIL, user.getEmail());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate,"users"));
    }
}