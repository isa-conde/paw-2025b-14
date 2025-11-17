package ar.edu.itba.paw.persistence.hibernate;

import ar.edu.itba.paw.model.Rules;
import ar.edu.itba.paw.persistence.Hibernate.RulesHibernateDao;
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
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class RulesHibernateDaoTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource ds;

    @Autowired
    private RulesHibernateDao rulesHibernateDao;

    private JdbcTemplate jdbcTemplate;
    private static final byte[] FILE = "9".repeat(64).getBytes(StandardCharsets.UTF_8);
    private final Long ID = 100L;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testInsertRules(){
        Rules ans = rulesHibernateDao.insertRules(FILE);
        em.flush();

        Assert.assertEquals(FILE, ans.getFile());
        String hexImage = bytesToHex(FILE);
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"rules",
                "id = " + ans.getId() + " and file = X'" + hexImage + "'"));
    }

    @Test
    public void testFindById(){
        Optional<Rules> ans = rulesHibernateDao.findById(ID);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertArrayEquals("0".repeat(64).getBytes(StandardCharsets.UTF_8), ans.get().getFile());
    }

    @Test
    public void testFindNothing(){
        Optional<Rules> ans = rulesHibernateDao.findById(0L);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testUpdateImage(){
        rulesHibernateDao.updateRules(ID,FILE);
        em.flush();

        String hexImage = bytesToHex(FILE);
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "rules",
                "id = " + ID + " and file = X'" + hexImage + "'"));
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b & 0xFF));
        }
        return sb.toString();
    }
}
