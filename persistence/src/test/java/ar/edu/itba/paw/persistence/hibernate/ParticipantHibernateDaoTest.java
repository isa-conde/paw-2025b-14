package ar.edu.itba.paw.persistence.hibernate;

import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.persistence.Hibernate.ParticipantHibernateDao;
import ar.edu.itba.paw.persistence.TestConfig;
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
public class ParticipantHibernateDaoTest {
    @PersistenceContext
    private EntityManager em;
    
    private static final Long ID = 100L;
    private static final String USERNAME = "johndoe";
    private static final String OTHER_USERNAME = "janedoe";
    private static final String TEAM = "Grupo 14";
    private static final String OTHER_EMAIL = "another@mail.com";
    private static final String EMAIL = "some@mail.com";
    private static final String PASSWORD = "1234567890";
    
    @Autowired
    private DataSource ds;

    @Autowired
    private ParticipantHibernateDao participantHibernateDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testJoinUser(){
        participantHibernateDao.joinTournamentUser(ID+2,ID);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "tournament_id = " + ID +
                        " and user_id = " + (ID+2)));
    }

    @Test
    public void testJoinTeam(){
        participantHibernateDao.joinTournamentTeam(ID,ID+1);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "tournament_id = " + ID +
                        " and team_id = " + (ID+1)));
    }

    @Test
    public void testJoinUserWithTeam(){
        participantHibernateDao.joinTournamentUserWithTeam(ID+1,ID,ID+1);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "tournament_id = " + ID +
                " and team_id = " + (ID+1) +
                " and user_id = " + (ID+1)));
    }

    @Test
    public void testGetUsers(){
        List<Participant> ans = participantHibernateDao.getTournamentParticipantUsers(ID);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(1,ans.size());
        Assert.assertEquals(ID,ans.get(0).getId());
        Assert.assertEquals(0,ans.get(0).getPoints().intValue());
        Assert.assertEquals(OTHER_USERNAME,ans.get(0).getName());
    }

    @Test
    public void testGetNoOne(){
        List<Participant> empty = participantHibernateDao.getTournamentParticipantUsers(0L);

        Assert.assertNotNull(empty);
        Assert.assertTrue(empty.isEmpty());
    }

    @Test
    public void testGetTeams(){
        List<Participant> ans = participantHibernateDao.getTournamentParticipantTeams(ID);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(1,ans.size());
        Assert.assertEquals(ID+20,ans.get(0).getId().longValue());
        Assert.assertEquals(0,ans.get(0).getPoints().intValue());
        Assert.assertEquals(TEAM,ans.get(0).getName());
    }

    @Test
    public void testGetNoTeams(){
        List<Participant> empty = participantHibernateDao.getTournamentParticipantTeams(0L);

        Assert.assertNotNull(empty);
        Assert.assertTrue(empty.isEmpty());
    }

    @Test
    public void testGetUserById(){
        Participant ans = participantHibernateDao.getTournamentParticipantById(ID,ID, 1);

        Assert.assertNotNull(ans);
        Assert.assertEquals(OTHER_USERNAME,ans.getName());
        Assert.assertEquals(ID,ans.getId());
        Assert.assertEquals(ID,ans.getTournament().getId());
        Assert.assertEquals(Integer.valueOf(0),ans.getPoints());
    }

    @Test
    public void testGetByIdNoOne(){
        Participant ans = participantHibernateDao.getTournamentParticipantById(0L, 0L, 1);

        Assert.assertNull(ans);
    }

//    @Test
//    public void testHasJoined(){
//        boolean ans = participantHibernateDao.hasJoined(ID,ID);
//
//        Assert.assertTrue(ans);
//    }
//
//    @Test
//    public void testHasNotJoined(){
//        boolean ans = participantHibernateDao.hasJoined(ID+2,ID);
//
//        Assert.assertFalse(ans);
//    }
//
//    @Test
//    public void testLeaveUser(){
//        participantHibernateDao.leaveTournamentUser(ID,ID);
//
//        Assert.assertEquals(3,JdbcTestUtils.countRowsInTable(jdbcTemplate,"participant"));
//        Assert.assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
//                "tournament_id = user_id and user_id = ID"));
//    }
//
//    @Test
//    public void testLeaveNoOne(){
//        participantHibernateDao.leaveTournamentUser(ID+2,ID);
//
//        Assert.assertEquals(4,JdbcTestUtils.countRowsInTable(jdbcTemplate,"participant"));
//    }
//
//    @Test
//    public void testUpdateGroupNumberUsers(){
//        participantHibernateDao.updateGroupNumberForUsers(ID,3,List.of(ID,ID+1),1);
//
//        Assert.assertEquals(2,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
//                "groupNumber = 3 and user_id is not null"));
//        Assert.assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
//                "groupNumber <> 3 and user_id is not null"));
//        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "participant",
//                "groupNumber = 3 and team_id is not null"));
//    }
//
//    @Test
//    public void testUpdateGroupNumberTeams(){
//        participantHibernateDao.updateGroupNumberForUsers(ID+1, 3, List.of(ID, ID + 1), 2);
//
//        Assert.assertEquals(2, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "participant",
//                "groupNumber = 3 and team_id is not null"));
//        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "participant",
//                "groupNumber <> 3 and team_id is not null"));
//        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "participant",
//                "groupNumber = 3 and user_id is not null"));
//    }
//
//    @Test
//    public void testSwapGroups(){
//        participantHibernateDao.swapGroups(ID,ID,ID+1,1,2,1);
//
//        Assert.assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
//                "groupNumber = user_id"));
//        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
//                "groupNumber = 1 and user_id = 2"));
//        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
//                "groupNumber = 2 and user_id = 1"));
//    }
//
//    @Test
//    public void testGetMaxPoints(){
//        int ans = participantHibernateDao.getTournamentMaxPointsGroup(ID, 1);
//
//        Assert.assertEquals(21,ans);
//    }
//
//    @Test
//    public void testGetSecondMaxPoints(){
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("participant")
//                .usingGeneratedKeyColumns("id");
//        jdbcInsert.execute(Map.of("user_id",ID+2,"tournament_id",ID,"points",14,"groupNumber",1));
//        int ans = participantHibernateDao.getTournamentSecondMaxPointsGroup(ID, 1);
//
//        Assert.assertEquals(14,ans);
//    }
//
//    @Test
//    public void testGetGroupNumberUser(){
//        int ans = participantHibernateDao.getGroupNumber(ID,ID,1);
//
//        Assert.assertEquals(1,ans);
//    }
//
//    @Test
//    public void testGetNoGroupNumberUser(){
//        jdbcTemplate.update("update participant set groupNumber = null");
//        int ans = participantHibernateDao.getGroupNumber(ID,ID,1);
//
//        Assert.assertEquals(0,ans);
//    }
//
//    @Test
//    public void testGetGroupNumberTeam(){
//        int ans = participantHibernateDao.getGroupNumber(ID+1,ID,2);
//
//        Assert.assertEquals(1,ans);
//    }
//
//    @Test
//    public void testGetNoGroupNumberTeam(){
//        jdbcTemplate.update("update participant set groupNumber = null");
//        int ans = participantHibernateDao.getGroupNumber(ID+1,ID,2);
//
//        Assert.assertEquals(0,ans);
//    }
//
//    @Test
//    public void testGetUserByPoints(){
//        List<Participant> ans = participantHibernateDao.getTournamentParticipantsByPoints(ID,null,21,1);
//
//        Assert.assertNotNull(ans);
//        Assert.assertFalse(ans.isEmpty());
//        Assert.assertEquals(1,ans.size());
//        Assert.assertEquals(ID,ans.get(0).getId());
//        Assert.assertEquals(21,ans.get(0).getPoints().intValue());
//        Assert.assertEquals(1,ans.get(0).getGroupNumber().intValue());
//        Assert.assertEquals(USERNAME,ans.get(0).getName());
//    }
//
//    @Test
//    public void testGetUserByPointsInGroup(){
//        jdbcTemplate.update("update participant set points = 21 where groupNumber = 2");
//        List<Participant> ans = participantHibernateDao.getTournamentParticipantsByPoints(ID,1,21,1);
//
//        Assert.assertNotNull(ans);
//        Assert.assertFalse(ans.isEmpty());
//        Assert.assertEquals(1,ans.size());
//        Assert.assertEquals(ID,ans.get(0).getId());
//        Assert.assertEquals(21,ans.get(0).getPoints().intValue());
//        Assert.assertEquals(1,ans.get(0).getGroupNumber().intValue());
//        Assert.assertEquals(USERNAME,ans.get(0).getName());
//    }
//
//    @Test
//    public void testGetTeamByPoints(){
//        List<Participant> ans = participantHibernateDao.getTournamentParticipantsByPoints(ID+1,null,21,2);
//
//        Assert.assertNotNull(ans);
//        Assert.assertFalse(ans.isEmpty());
//        Assert.assertEquals(1,ans.size());
//        Assert.assertEquals(ID,ans.get(0).getId());
//        Assert.assertEquals(21,ans.get(0).getPoints().intValue());
//        Assert.assertEquals(1,ans.get(0).getGroupNumber().intValue());
//        Assert.assertEquals(USERNAME,ans.get(0).getName());
//    }
//
//    @Test
//    public void testGetTeamByPointsInGroup(){
//        jdbcTemplate.update("update participant set points = 21 where groupNumber = 2");
//        List<Participant> ans = participantHibernateDao.getTournamentParticipantsByPoints(ID+1,1,21,2);
//
//        Assert.assertNotNull(ans);
//        Assert.assertFalse(ans.isEmpty());
//        Assert.assertEquals(1,ans.size());
//        Assert.assertEquals(ID,ans.get(0).getId());
//        Assert.assertEquals(21,ans.get(0).getPoints().intValue());
//        Assert.assertEquals(1,ans.get(0).getGroupNumber().intValue());
//        Assert.assertEquals(USERNAME,ans.get(0).getName());
//    }
//
//    @Test
//    public void testGetGroups(){
//        int ans = participantHibernateDao.getTournamentGroups(ID);
//
//        Assert.assertEquals(2,ans);
//    }
//
//    @Test
//    public void testGetNoGroups(){
//        int ans = participantHibernateDao.getTournamentGroups(ID+2);
//
//        Assert.assertEquals(0,ans);
//    }
//
//    @Test
//    public void testAddPoints(){
//        participantHibernateDao.sumPoints(ID,ID+1,3, 1);
//        int ans = jdbcTemplate.queryForObject("select points from participant where tournament_id = ? and user_id = ?", Integer.class,ID,ID+1);
//
//        Assert.assertEquals(5,ans);
//    }
}
