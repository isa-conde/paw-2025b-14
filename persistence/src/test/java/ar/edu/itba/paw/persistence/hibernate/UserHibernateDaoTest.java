package ar.edu.itba.paw.persistence.hibernate;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.Hibernate.UserHibernateDao;
import ar.edu.itba.paw.persistence.TestConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class UserHibernateDaoTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource ds;

    @Autowired
    private UserHibernateDao userHibernateDao;

    private JdbcTemplate jdbcTemplate;

    private static final String USERNAME = "johndoe";
    private static final String USED_USERNAME = "janedoe";
    private static final String USED_EMAIL = "another@mail.com";
    private static final String EMAIL = "some@mail.com";
    private static final String PASSWORD = "1234567890";
    private Long usedId = 100L;
    private static final Log log = LogFactory.getLog(UserHibernateDaoTest.class);
    private User oldUser;
    private int rows;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).usingGeneratedKeyColumns("id").withTableName("users");
//        final Map<String,Object> values = Map.of("username",USED_USERNAME, "email", USED_EMAIL, "password", PASSWORD, "verified", false,"locale","es");
//        usedId = jdbcInsert.executeAndReturnKey(values).longValue();
        rows = JdbcTestUtils.countRowsInTable(jdbcTemplate,"users");
    }

    @Test
    public void createTest(){
        final User user = userHibernateDao.create(USERNAME, EMAIL, PASSWORD);
        em.flush();

        Assert.assertNotNull(user);
        Assert.assertEquals(USERNAME, user.getUsername());
        Assert.assertEquals(EMAIL,user.getEmail());
        Assert.assertEquals(PASSWORD,user.getPassword());
        Assert.assertFalse(user.isVerified());
        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"users",
                "username = '" + USERNAME + "' and email = '" + EMAIL + "' and password = '" + PASSWORD +"'"));
    }

    @Test(expected = Exception.class)
    public void testNoUsername(){
        userHibernateDao.create(USERNAME, null,PASSWORD);
    }

    @Test(expected = Exception.class)
    public void testNoEmail(){
        userHibernateDao.create(null, EMAIL,PASSWORD);
    }

    @Test(expected = Exception.class)
    public void testRepeatUsername(){
        userHibernateDao.create(USED_USERNAME,EMAIL,PASSWORD);
        em.flush();
    }

    @Test(expected = Exception.class)
    public void testRepeatMail(){
        userHibernateDao.create(USERNAME,USED_EMAIL,PASSWORD);
        em.flush();
    }

    @Test
    public void testCheckUsernameExists(){
        final boolean ans = userHibernateDao.checkUsernameExists(USED_USERNAME);

        Assert.assertTrue(ans);
    }

    @Test
    public void testCheckEmailExists(){
        final boolean ans = userHibernateDao.checkEmailExists(USED_EMAIL);

        Assert.assertTrue(ans);
    }

    @Test
    public void testCheckUsernameNotExists(){
        final boolean ans = userHibernateDao.checkUsernameExists(USERNAME);

        Assert.assertFalse(ans);
    }

    @Test
    public void testCheckEmailNotExists(){
        final boolean ans = userHibernateDao.checkEmailExists(EMAIL);

        Assert.assertFalse(ans);
    }

    @Test
    public void findByIdTest(){
        Optional<User> ans = userHibernateDao.findById(usedId);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertEquals(USED_USERNAME, ans.get().getUsername());
        Assert.assertEquals(USED_EMAIL, ans.get().getEmail());
        Assert.assertEquals(PASSWORD, ans.get().getPassword());
        Assert.assertEquals(usedId.longValue(), ans.get().getId());
        Assert.assertFalse(ans.get().isVerified());
    }

    @Test
    public void testFindNonExistentId(){
        final Optional<User> ans = userHibernateDao.findById(3);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testFindByUsername(){
        final Optional<User> ans = userHibernateDao.findByUsername(USED_USERNAME);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertEquals(USED_USERNAME,ans.get().getUsername());
        Assert.assertEquals(USED_EMAIL, ans.get().getEmail());
        Assert.assertEquals(PASSWORD, ans.get().getPassword());
        Assert.assertEquals(ans.get().getId(), usedId.longValue());
    }

    @Test
    public void testFindNonExistentUsername(){
        final Optional<User> ans = userHibernateDao.findByUsername(USERNAME);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testFindByEmail(){
        final Optional<User> ans = userHibernateDao.findByEmail(USED_EMAIL);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertEquals(USED_USERNAME,ans.get().getUsername());
        Assert.assertEquals(USED_EMAIL, ans.get().getEmail());
        Assert.assertEquals(PASSWORD, ans.get().getPassword());
        Assert.assertEquals(ans.get().getId(), usedId.longValue());
    }

    @Test
    public void testFindNonExistentEmail(){
        final Optional<User> ans = userHibernateDao.findByEmail(EMAIL);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testChangePassword(){
        userHibernateDao.changePassword(usedId,PASSWORD + "_new");
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"users",
                "id = " + usedId + " and password = '" + PASSWORD + "_new'"));
    }

    @Test
    public void testVerify(){
        userHibernateDao.verifyUser(usedId);
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"users",
                "id = " + usedId + " and verified = true"));
    }

    @Test
    public void testUpdateProfileInfo(){
        userHibernateDao.updateProfileInfo(usedId,USED_USERNAME+"a",USERNAME,usedId,usedId);
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"users",
                "id = " + usedId + " and profile_picture_id = "+ usedId + " and banner_id = " + usedId
                        + " and username = '" + USED_USERNAME + "a' and bio = '" + USERNAME + "'"));
    }

    @Test
    public void testUpdateLocale(){
        userHibernateDao.updateUserLocale("en",usedId);
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"users",
                "id = " + usedId + " and locale = 'en'"));
    }
}
