package ar.edu.itba.paw.persistence.hibernate;

import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.persistence.Hibernate.TokenHibernateDao;
import ar.edu.itba.paw.persistence.TestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class TokenHibernateDaoTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource ds;

    @Autowired
    private TokenHibernateDao tokenHibernateDao;

    private JdbcTemplate jdbcTemplate;

    private final Long usedId = 100L;


    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate(){
        final Token ans = tokenHibernateDao.create(3L,3L,LocalDate.now().plusDays(1));
        em.flush();

        Assert.assertNotNull(ans);
        Assert.assertEquals(Long.valueOf(3),ans.getToken());
        Assert.assertEquals(Long.valueOf(3),ans.getUserId());
        Assert.assertEquals(LocalDate.now().plusDays(1),ans.getExpiryDate());
        Assert.assertFalse(ans.isUsed());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tokens",
                "user_id = 3 and token = 3 and expiry_date = '" + LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"));
    }

    @Test
    public void testFindNothing(){
        final Optional<Token> ans = tokenHibernateDao.findByToken(0L);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testFindByToken(){
        final Optional<Token> ans = tokenHibernateDao.findByToken(usedId);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertEquals(usedId, ans.get().getId());
        Assert.assertEquals(usedId, ans.get().getUserId());
        Assert.assertEquals(usedId, ans.get().getToken());
        Assert.assertEquals(LocalDate.of(2003, Month.FEBRUARY,21),ans.get().getExpiryDate());
        Assert.assertFalse(ans.get().isUsed());
    }

    @Test
    public void testMarkAsUsed(){
        tokenHibernateDao.markAsUsed(usedId);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tokens",
                "id = " + usedId + " and used = true"));
    }

    @Test
    public void testDeleteExpiredTokens(){
        tokenHibernateDao.deleteExpiredTokens();
        em.flush();

        Assert.assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate,"tokens"));
    }
}
