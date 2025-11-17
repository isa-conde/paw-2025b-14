package ar.edu.itba.paw.persistence.hibernate;

import ar.edu.itba.paw.persistence.Hibernate.TeamMemberHibernateDao;
import ar.edu.itba.paw.persistence.TestConfig;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class TeamMemberHibernateDaoTest {
    @PersistenceContext
    private EntityManager em;

    private static final Long ID = 101L;

    @Autowired
    private DataSource ds;

    @Autowired
    private TeamMemberHibernateDao teamMemberHibernateDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testAddMember(){
        teamMemberHibernateDao.addMember(ID,ID);
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"team_member","user_id = team_id and user_id = " + ID));
        Assert.assertEquals(4, JdbcTestUtils.countRowsInTable(jdbcTemplate,"team_member"));
    }

    @Test(expected = RuntimeException.class)
    public void testRepeatMember(){
        teamMemberHibernateDao.addMember(ID-1,ID-1);
        em.flush();
    }

    @Test
    public void testIsMember(){
        Boolean isMember = teamMemberHibernateDao.isMember(ID, ID-1);

        Assert.assertNotNull(isMember);
        Assert.assertTrue(isMember);
    }

    @Test
    public void testIsNotMember(){
        Boolean isMember = teamMemberHibernateDao.isMember(ID, ID);

        Assert.assertNotNull(isMember);
        Assert.assertFalse(isMember);
    }
}
