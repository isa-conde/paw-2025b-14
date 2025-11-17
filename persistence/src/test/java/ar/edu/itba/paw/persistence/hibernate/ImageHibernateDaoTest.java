package ar.edu.itba.paw.persistence.hibernate;

import ar.edu.itba.paw.persistence.Hibernate.ImageHibernateDao;
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
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class ImageHibernateDaoTest {
    @PersistenceContext
    private EntityManager em;

    private static final byte[] IMAGE = "9".repeat(64).getBytes(StandardCharsets.UTF_8);
    private final Long firstUsedId = 100L;

    @Autowired
    private DataSource ds;

    @Autowired
    private ImageHibernateDao imageHibernateDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testInsert(){
        final Long ans = imageHibernateDao.insertImage(IMAGE);
        em.flush();

        Assert.assertNotNull(ans);
        String hexImage = bytesToHex();
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "image",
                "id = " + ans + " and image = X'" + hexImage + "'"));
    }

    @Test
    public void testFindById(){
        final Optional<byte[]> ans = imageHibernateDao.findById(firstUsedId);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertArrayEquals("0".repeat(64).getBytes(StandardCharsets.UTF_8), ans.get());
    }

    @Test
    public void testFindNothing(){
        final Optional<byte[]> ans = imageHibernateDao.findById(0);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testUpdateImage(){
        imageHibernateDao.updateImage(firstUsedId,IMAGE);
        em.flush();

        String hexImage = bytesToHex();
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "image",
                "id = " + firstUsedId + " and image = X'" + hexImage + "'"));
    }

    private String bytesToHex() {
        StringBuilder sb = new StringBuilder();
        for (byte b : IMAGE) {
            sb.append(String.format("%02X", b & 0xFF));
        }
        return sb.toString();
    }
}
