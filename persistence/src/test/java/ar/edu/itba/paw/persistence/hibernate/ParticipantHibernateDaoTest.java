package ar.edu.itba.paw.persistence.hibernate;

import ar.edu.itba.paw.model.Match.PointsPair;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.persistence.Hibernate.ParticipantHibernateDao;
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
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class ParticipantHibernateDaoTest {
    @PersistenceContext
    private EntityManager em;
    
    private static final Long ID = 100L;
    private static final String OTHER_USERNAME = "janedoe";
    private static final String TEAM = "Grupo 14";
    
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
        participantHibernateDao.joinTournamentTeam(ID+1,ID+1);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "tournament_id = " + (ID+1) +
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
        List<Participant> ans = participantHibernateDao.getTournamentParticipantUsers(ID+1);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(1,ans.size());
        Assert.assertEquals(ID+1,ans.get(0).getId().longValue());
        Assert.assertEquals(7,ans.get(0).getPoints());
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
        List<Participant> ans = participantHibernateDao.getTournamentParticipantTeams(ID+1);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(1,ans.size());
        Assert.assertEquals(ID+21,ans.get(0).getId().longValue());
        Assert.assertEquals(7,ans.get(0).getPoints());
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
        Assert.assertEquals(7,ans.getPoints());
    }

    @Test
    public void testGetByIdNoOne(){
        Participant ans = participantHibernateDao.getTournamentParticipantById(0L, 0L, 1);

        Assert.assertNull(ans);
    }

    @Test
    public void testHasJoined(){
        boolean ans = participantHibernateDao.hasJoined(ID,ID);

        Assert.assertTrue(ans);
    }

    @Test
    public void testHasNotJoined(){
        boolean ans = participantHibernateDao.hasJoined(ID+2,ID);

        Assert.assertFalse(ans);
    }

    @Test
    public void testLeaveUser(){
        participantHibernateDao.leaveTournamentUser(ID,ID);

        Assert.assertEquals(43,JdbcTestUtils.countRowsInTable(jdbcTemplate,"participant"));
        Assert.assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "tournament_id = user_id and user_id = " + ID));
    }

    @Test
    public void testLeaveNoOne(){
        participantHibernateDao.leaveTournamentUser(ID+2,ID);

        Assert.assertEquals(44,JdbcTestUtils.countRowsInTable(jdbcTemplate,"participant"));
    }

    @Test
    public void testUpdateGroupNumberUsers(){
        participantHibernateDao.updateGroupNumberForUsers(ID,3,List.of(ID),1);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "group_number = 3 and user_id = " + ID));
    }

    @Test
    public void testUpdateGroupNumberTeams(){
        participantHibernateDao.updateGroupNumberForUsers(ID+1, 3, List.of(ID), 2);
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "participant",
                "group_number = 3 and team_id = " + ID));
    }

    @Test
    public void testSwapGroups(){
        participantHibernateDao.swapGroups(ID,ID,ID+1,1,2,1);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "group_number = 1 and user_id = " + (ID+1)));
        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "group_number = 2 and user_id = " + ID));
    }

    @Test
    public void testGetMaxPoints(){
        PointsPair ans = participantHibernateDao.getTournamentMaxPointsPairGroup(ID, 1);

        Assert.assertEquals(7,ans.getPoints().intValue());
        Assert.assertEquals(6,ans.getScoreDifference().intValue());
    }

    @Test
    public void testGetSecondMaxPoints(){
        jdbcTemplate.update("update participant set points = 0 where team_id is not null");
        PointsPair ans = participantHibernateDao.getTournamentSecondMaxPointsPairGroup(ID, 1);

        Assert.assertEquals(1,ans.getPoints().intValue());
        Assert.assertEquals(-6,ans.getScoreDifference().intValue());
    }

    @Test
    public void testGetGroupNumberUser(){
        Integer ans = participantHibernateDao.getGroupNumber(ID,ID,1);

        Assert.assertNotNull(ans);
        Assert.assertEquals(1,ans.intValue());
    }

    @Test
    public void testGetNoGroupNumberUser(){
        jdbcTemplate.update("update participant set group_number = null");
        Integer ans = participantHibernateDao.getGroupNumber(ID,ID,1);

        Assert.assertNull(ans);
    }

    @Test
    public void testGetGroupNumberTeam(){
        Integer ans = participantHibernateDao.getGroupNumber(ID,ID,2);

        Assert.assertNotNull(ans);
        Assert.assertEquals(1,ans.intValue());
    }

    @Test
    public void testGetNoGroupNumberTeam(){
        jdbcTemplate.update("update participant set group_number = null");
        Integer ans = participantHibernateDao.getGroupNumber(ID,ID,2);

        Assert.assertNull(ans);
    }

    @Test
    public void testGetUserByPoints(){
        List<Participant> ans = participantHibernateDao.getTournamentParticipantsByPointsPair(ID+1,null,new PointsPair(7,6),1);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(1,ans.size());
        Assert.assertEquals(ID+1,ans.get(0).getId().longValue());
        Assert.assertEquals(7,ans.get(0).getPoints());
        Assert.assertEquals(6, ans.get(0).getScoreDifference().intValue());
        Assert.assertEquals(OTHER_USERNAME,ans.get(0).getName());
    }

    @Test
    public void testGetUserByPointsInGroup(){
        List<Participant> ans = participantHibernateDao.getTournamentParticipantsByPointsPair(ID,1,new PointsPair(7,6),1);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(1,ans.size());
        Assert.assertEquals(ID,ans.get(0).getId());
        Assert.assertEquals(7,ans.get(0).getPoints());
        Assert.assertEquals(6, ans.get(0).getScoreDifference().intValue());
        Assert.assertEquals(1,ans.get(0).getGroupNumber().intValue());
        Assert.assertEquals(OTHER_USERNAME,ans.get(0).getName());
    }

    @Test
    public void testGetTeamByPoints(){
        List<Participant> ans = participantHibernateDao.getTournamentParticipantsByPointsPair(ID+1,null,new PointsPair(7,6),2);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(1,ans.size());
        Assert.assertEquals(ID+21,ans.get(0).getId().longValue());
        Assert.assertEquals(7,ans.get(0).getPoints());
        Assert.assertEquals(6,ans.get(0).getScoreDifference().intValue());
        Assert.assertEquals(TEAM,ans.get(0).getName());
    }

    @Test
    public void testGetTeamByPointsInGroup(){
        List<Participant> ans = participantHibernateDao.getTournamentParticipantsByPointsPair(ID,1,new PointsPair(7,6),2);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(1,ans.size());
        Assert.assertEquals(ID+20,ans.get(0).getId().longValue());
        Assert.assertEquals(7,ans.get(0).getPoints());
        Assert.assertEquals(6,ans.get(0).getScoreDifference().intValue());
        Assert.assertEquals(1,ans.get(0).getGroupNumber().intValue());
        Assert.assertEquals(TEAM,ans.get(0).getName());
    }

    @Test
    public void testGetGroups(){
        int ans = participantHibernateDao.getTournamentGroups(ID);

        Assert.assertEquals(1,ans);
    }

    @Test
    public void testGetNoGroups(){
        int ans = participantHibernateDao.getTournamentGroups(ID+2);

        Assert.assertEquals(0,ans);
    }

    @Test
    public void testAddPoints(){
        participantHibernateDao.sumPoints(ID,ID,3, 1,1);

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "tournament_id = user_id and user_id = " + ID + " and points = " + (7+3) +
                " and score_difference = " + (6+1)));
    }

    @Test
    public void testHasRated(){
        jdbcTemplate.update("update participant set has_rated = true");
        boolean ans = participantHibernateDao.hasRated(ID, ID);

        Assert.assertTrue(ans);
    }

    @Test
    public void testHasNotRated(){
        jdbcTemplate.update("update participant set has_rated = false");
        boolean ans = participantHibernateDao.hasRated(ID, ID);

        Assert.assertFalse(ans);
    }

    @Test
    public void testRate(){
        participantHibernateDao.updateHasRated(ID, ID);
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "participant",
                "tournament_id = user_id and user_id = " + ID + " and has_rated = true"));
    }

    @Test
    public void testLeaveTeam(){
        participantHibernateDao.leaveTournamentTeam(ID,ID);

        Assert.assertEquals(43,JdbcTestUtils.countRowsInTable(jdbcTemplate,"participant"));
        Assert.assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "tournament_id = team_id and team_id = " + ID));
    }

    @Test
    public void testLeaveNoTeam(){
        participantHibernateDao.leaveTournamentTeam(ID+2,ID);

        Assert.assertEquals(44,JdbcTestUtils.countRowsInTable(jdbcTemplate,"participant"));
    }
}
