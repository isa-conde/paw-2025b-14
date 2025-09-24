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
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
public class UserJdbcDaoTest {
    private static final String USERNAME = "johndoe";
    private static final String EMAIL = "some@mail.com";
    private static final String PASSWORD = "1234567890";

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
        final User user = userJdbcDao.create(USERNAME,EMAIL, PASSWORD);
        Assert.assertNotNull(user);
        Assert.assertEquals(USERNAME,user.getUsername());
        Assert.assertEquals(EMAIL, user.getEmail());
        Assert.assertEquals(PASSWORD, user.getPassword());
        Assert.assertFalse(user.isVerified());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate,"users"));
    }

    @Test(expected = Exception.class)
    public void testNoUsername(){
        final User user = userJdbcDao.create(USERNAME, null,PASSWORD);
    }

    @Test(expected = Exception.class)
    public void testNoEmail(){
        final User user = userJdbcDao.create(null, EMAIL,PASSWORD);
    }

    @Test(expected = Exception.class)
    public void testRepeatUsername(){
        final User user1 = userJdbcDao.create(USERNAME,EMAIL,PASSWORD);
        final User user2 = userJdbcDao.create(USERNAME,"another@mail.com",PASSWORD);
    }

    @Test(expected = Exception.class)
    public void testRepeatMail(){
        final User user1 = userJdbcDao.create(USERNAME,EMAIL,PASSWORD);
        final User user2 = userJdbcDao.create("janedoe",EMAIL,PASSWORD);
    }

//    @Test
//    public void testAuthenticate(){
//        final User user = userJdbcDao.create(USERNAME,EMAIL,PASSWORD);
//        final Optional<User> ans = userJdbcDao.authenticate(USERNAME,EMAIL);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans.isPresent());
//        Assert.assertEquals(USERNAME,ans.get().getUsername());
//        Assert.assertEquals(EMAIL, ans.get().getEmail());
//        Assert.assertEquals(ans.get().getId(), user.getId());
//    }

//    @Test
//    public void testAuthenticateNonExistent(){
//        final Optional<User> ans = userJdbcDao.authenticate(USERNAME,EMAIL);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans.isEmpty());
//    }

    @Test
    public void testCheckUsernameExists(){
        final User user = userJdbcDao.create(USERNAME,EMAIL,PASSWORD);
        final boolean ans = userJdbcDao.checkUsernameExists(user.getUsername());

        Assert.assertTrue(ans);
    }

    @Test
    public void testCheckEmailExists(){
        final User user = userJdbcDao.create(USERNAME,EMAIL,PASSWORD);
        final boolean ans = userJdbcDao.checkEmailExists(user.getEmail());

        Assert.assertTrue(ans);
    }

    @Test
    public void testCheckUsernameNotExists(){
        final boolean ans = userJdbcDao.checkUsernameExists(USERNAME);

        Assert.assertFalse(ans);
    }

    @Test
    public void testCheckEmailNotExists(){
        final boolean ans = userJdbcDao.checkEmailExists(EMAIL);

        Assert.assertFalse(ans);
    }

    @Test
    public void testFindById(){
        final User user = userJdbcDao.create(USERNAME,EMAIL,PASSWORD);
        final Optional<User> ans = userJdbcDao.findById(user.getId());

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertEquals(USERNAME,ans.get().getUsername());
        Assert.assertEquals(EMAIL, ans.get().getEmail());
        Assert.assertEquals(PASSWORD, ans.get().getPassword());
        Assert.assertEquals(ans.get().getId(), user.getId());
    }

    @Test
    public void testFindNonExistent(){
        final Optional<User> ans = userJdbcDao.findById(3);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }
}