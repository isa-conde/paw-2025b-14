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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class UserJdbcDaoTest {
    private static final String USERNAME = "johndoe";
    private static final String USED_USERNAME = "janedoe";
    private static final String USED_EMAIL = "another@mail.com";
    private static final String EMAIL = "some@mail.com";
    private static final String PASSWORD = "1234567890";
    private static final Log log = LogFactory.getLog(UserJdbcDaoTest.class);
    private long usedId;
    private int rows;

    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("username"), rs.getString("email"), rs.getString("password"), rs.getBoolean("verified"), rs.getString("bio"), rs.getLong("profile_picture_id"), rs.getLong("banner_id"), rs.getString("locale"));

    @Autowired
    private DataSource ds;

    @Autowired
    private UserJdbcDao userJdbcDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).usingGeneratedKeyColumns("id").withTableName("users");
        final Map<String,Object> values = Map.of("username",USED_USERNAME, "email", USED_EMAIL, "password", PASSWORD, "verified", false);
        usedId = jdbcInsert.executeAndReturnKey(values).longValue();
        rows = JdbcTestUtils.countRowsInTable(jdbcTemplate,"users");
    }

    @Test
    public void testCreate(){
        final User user = userJdbcDao.create(USERNAME,EMAIL, PASSWORD);
        final List<User> inserted = jdbcTemplate.query("select * from users where id = ?", ROW_MAPPER, user.getId());

        Assert.assertNotNull(inserted);
        Assert.assertFalse(inserted.isEmpty());
        Assert.assertEquals(1, inserted.size());
        Assert.assertNotNull(user);
        Assert.assertEquals(USERNAME,inserted.get(0).getUsername());
        Assert.assertEquals(USERNAME,user.getUsername());
        Assert.assertEquals(EMAIL, inserted.get(0).getEmail());
        Assert.assertEquals(EMAIL, user.getEmail());
        Assert.assertEquals(PASSWORD, inserted.get(0).getPassword());
        Assert.assertEquals(PASSWORD, user.getPassword());
        Assert.assertFalse(inserted.get(0).isVerified());
        Assert.assertFalse(user.isVerified());
        Assert.assertNull(inserted.get(0).getBio());
        Assert.assertNull(user.getBio());
        Assert.assertNull(user.getPfp_id());
        Assert.assertNull(user.getBanner_id());
        Assert.assertEquals(rows + 1, JdbcTestUtils.countRowsInTable(jdbcTemplate,"users"));
    }

    @Test(expected = Exception.class)
    public void testNoUsername(){
        userJdbcDao.create(USERNAME, null,PASSWORD);
    }

    @Test(expected = Exception.class)
    public void testNoEmail(){
        userJdbcDao.create(null, EMAIL,PASSWORD);
    }

    @Test(expected = Exception.class)
    public void testRepeatUsername(){
        userJdbcDao.create(USED_USERNAME,EMAIL,PASSWORD);
    }

    @Test(expected = Exception.class)
    public void testRepeatMail(){
        userJdbcDao.create(USERNAME,USED_EMAIL,PASSWORD);
    }

    @Test
    public void testChangePassword(){
        userJdbcDao.changePassword(usedId,PASSWORD + "_new");
        String newPassword = jdbcTemplate.queryForObject(
                "SELECT password FROM users WHERE id = ?",
                String.class,
                usedId);

        Assert.assertNotNull(newPassword);
        Assert.assertNotEquals(PASSWORD, newPassword);
        Assert.assertEquals(PASSWORD + "_new", newPassword);
    }

    @Test
    public void testVerify(){
        userJdbcDao.verifyUser(usedId);
        final Optional<User> userVerified = userJdbcDao.findById(usedId);

        Assert.assertTrue(userVerified.isPresent());
//        Assert.assertFalse(user.isVerified());
        Assert.assertTrue(userVerified.get().isVerified());
    }

    @Test
    public void testCheckUsernameExists(){
        final boolean ans = userJdbcDao.checkUsernameExists(USED_USERNAME);

        Assert.assertTrue(ans);
    }

    @Test
    public void testCheckEmailExists(){
        final boolean ans = userJdbcDao.checkEmailExists(USED_EMAIL);

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
        final Optional<User> ans = userJdbcDao.findById(usedId);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertEquals(USED_USERNAME,ans.get().getUsername());
        Assert.assertEquals(USED_EMAIL, ans.get().getEmail());
        Assert.assertEquals(PASSWORD, ans.get().getPassword());
        Assert.assertEquals(ans.get().getId(), usedId);
    }

    @Test
    public void testFindNonExistentId(){
        final Optional<User> ans = userJdbcDao.findById(3);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testFindByUsername(){
        final Optional<User> ans = userJdbcDao.findByUsername(USED_USERNAME);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertEquals(USED_USERNAME,ans.get().getUsername());
        Assert.assertEquals(USED_EMAIL, ans.get().getEmail());
        Assert.assertEquals(PASSWORD, ans.get().getPassword());
        Assert.assertEquals(ans.get().getId(), usedId);
    }

    @Test
    public void testFindNonExistentUsername(){
        final Optional<User> ans = userJdbcDao.findByUsername(USERNAME);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testFindByEmail(){
        final Optional<User> ans = userJdbcDao.findByEmail(USED_EMAIL);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertEquals(USED_USERNAME,ans.get().getUsername());
        Assert.assertEquals(USED_EMAIL, ans.get().getEmail());
        Assert.assertEquals(PASSWORD, ans.get().getPassword());
        Assert.assertEquals(ans.get().getId(), usedId);
    }

    @Test
    public void testFindNonExistentEmail(){
        final Optional<User> ans = userJdbcDao.findByEmail(EMAIL);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testUpdateProfileInfo(){
        userJdbcDao.updateProfileInfo(usedId,USED_USERNAME+"a",USERNAME,usedId,usedId);
        final List<User> updated = jdbcTemplate.query("select * from users where id = ?", ROW_MAPPER, usedId);

        Assert.assertNotNull(updated);
        Assert.assertFalse(updated.isEmpty());
        Assert.assertEquals(1,updated.size());
        User row = updated.get(0);
        Assert.assertEquals(USED_USERNAME+"a",row.getUsername());
        Assert.assertEquals(USERNAME,row.getBio());
        Assert.assertEquals(usedId,row.getBanner_id().longValue());
        Assert.assertEquals(usedId,row.getPfp_id().longValue());
    }
}