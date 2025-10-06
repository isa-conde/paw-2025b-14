package ar.edu.itba.paw.persistence;

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

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class TeamMemberJdbcDaoTest {
    private static final Long ID = (long) 1;

    @Autowired
    private DataSource ds;

    @Autowired
    private TeamMemberJdbcDao teamMemberJdbcDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("team_member");
        jdbcInsert.execute(Map.of("user_id",0,"team_id",0));
        jdbcInsert.execute(Map.of("user_id",1,"team_id",0));
        jdbcInsert.execute(Map.of("user_id",0,"team_id",1));
    }

    @Test
    public void testAddMember(){
        teamMemberJdbcDao.addMember(ID,ID);

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"team_member","user_id = team_id and user_id = " + ID));
        Assert.assertEquals(4, JdbcTestUtils.countRowsInTable(jdbcTemplate,"team_member"));
    }

    @Test(expected = RuntimeException.class)
    public void testRepeatMember(){
        teamMemberJdbcDao.addMember((long)0,ID);
    }

    @Test(expected = RuntimeException.class)
    public void testAddNoOne(){
        teamMemberJdbcDao.addMember(ID,null);
    }

    @Test(expected = RuntimeException.class)
    public void testAddNowhere(){
        teamMemberJdbcDao.addMember(null,ID);
    }

    @Test
    public void testIsMember(){
        Boolean isMember = teamMemberJdbcDao.isMember(1L, 0L);

        Assert.assertNotNull(isMember);
        Assert.assertTrue(isMember);
    }

    @Test
    public void testIsNotMember(){
        Boolean isMember = teamMemberJdbcDao.isMember(1L, 1L);

        Assert.assertNotNull(isMember);
        Assert.assertFalse(isMember);
    }

    @Test
    public void testGetTeamMembers(){
        List<Long> members = teamMemberJdbcDao.getTeamMembers(0L);

        Assert.assertNotNull(members);
        Assert.assertFalse(members.isEmpty());
        Assert.assertEquals(2, members.size());
        Assert.assertTrue(members.contains(0L));
        Assert.assertTrue(members.contains(1L));
    }

    @Test
    public void testGetNoTeamMembers(){
        List<Long> members = teamMemberJdbcDao.getTeamMembers((long) -1);

        Assert.assertNotNull(members);
        Assert.assertTrue(members.isEmpty());
    }
}
