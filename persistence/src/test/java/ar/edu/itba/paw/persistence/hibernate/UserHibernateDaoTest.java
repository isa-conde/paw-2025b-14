//package ar.edu.itba.paw.persistence.hibernate;
//
//import ar.edu.itba.paw.model.User;
//import ar.edu.itba.paw.persistence.Hibernate.UserHibernateDao;
//import ar.edu.itba.paw.persistence.TestConfig;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.jdbc.JdbcTestUtils;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.sql.DataSource;
//import java.util.Map;
//import java.util.Optional;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TestConfig.class)
//@Sql("classpath:db/init.sql")
//@Transactional
//@Rollback
//public class UserHibernateDaoTest {
//    @PersistenceContext
//    private EntityManager em;
//
//    @Autowired
//    private DataSource ds;
//
//    @Autowired
//    private UserHibernateDao userHibernateDao;
//
//    private JdbcTemplate jdbcTemplate;
//
//    private static final String USERNAME = "johndoe";
//    private static final String USED_USERNAME = "janedoe";
//    private static final String USED_EMAIL = "another@mail.com";
//    private static final String EMAIL = "some@mail.com";
//    private static final String PASSWORD = "1234567890";
//    private Long usedId = 1L;
//    private static final Log log = LogFactory.getLog(UserHibernateDaoTest.class);
//    private User oldUser;
//    private int rows;
//
//    @Before
//    public void setUp(){
//        jdbcTemplate = new JdbcTemplate(ds);
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).usingGeneratedKeyColumns("id").withTableName("users");
//        final Map<String,Object> values = Map.of("username",USED_USERNAME, "email", USED_EMAIL, "password", PASSWORD, "verified", false);
//        usedId = jdbcInsert.executeAndReturnKey(values).longValue();
//        rows = JdbcTestUtils.countRowsInTable(jdbcTemplate,"users");
//    }
//
//    @Test
//    public void createTest(){
//        final User user = userHibernateDao.create(USERNAME, EMAIL, PASSWORD);
//
//        Assert.assertNotNull(user);
//        Assert.assertEquals(USERNAME, user.getUsername());
//    }
//
//    @Test
//    public void findByIdTest(){
//        Optional<User> ans = userHibernateDao.findById(usedId);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans.isPresent());
//        Assert.assertEquals(USED_USERNAME, ans.get().getUsername());
//        Assert.assertEquals(USED_EMAIL, ans.get().getEmail());
//        Assert.assertEquals(PASSWORD, ans.get().getPassword());
//        Assert.assertEquals(usedId.longValue(), ans.get().getId());
//        Assert.assertFalse(ans.get().isVerified());
//    }
//}
