package ar.edu.itba.paw.persistence.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.persistence.Hibernate.TeamHibernateDao;
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

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class TeamHibernateDaoTest {
    @PersistenceContext
    private EntityManager em;

    private static final String TEAM = "Group 14";
    private static final String OTHER_TEAM = "Grupo 14";
    private static final Long ID = 100L;
    private static final Long OTHER_ID = 101L;
    private static final int GRID_PAGE_SIZE = 9;

    @Autowired
    private DataSource ds;

    @Autowired
    private TeamHibernateDao teamHibernateDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate(){
        Team created = teamHibernateDao.create(TEAM,ID,ID,ID);
        em.flush();

        Assert.assertNotNull(created);
        Assert.assertEquals(TEAM,created.getName());
        Assert.assertEquals(ID.longValue(), (long) created.getOwner().getId());
        Assert.assertEquals(ID,created.getBannerId());
        Assert.assertEquals(ID,created.getPfpId());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"team",
                "name = '" + TEAM + "' and owner_id = " + ID + " and pfp_id = owner_id and banner_id = pfp_id"));
    }

    @Test
    public void testFindNothing(){
        Optional<Team> nothing = teamHibernateDao.findById(0L);

        Assert.assertNotNull(nothing);
        Assert.assertTrue(nothing.isEmpty());
    }

    @Test
    public void testFindById(){
        Optional<Team> team = teamHibernateDao.findById(ID);

        Assert.assertNotNull(team);
        Assert.assertTrue(team.isPresent());
        Team present = team.get();
        Assert.assertEquals(OTHER_TEAM,present.getName());
        Assert.assertEquals(ID.longValue(), (long) present.getOwner().getId());
        Assert.assertNull(present.getBannerId());
        Assert.assertNull(present.getPfpId());
        Assert.assertEquals(ID,present.getId());
    }

    @Test
    public void testGetActiveNothing(){
        List<Long> empty = teamHibernateDao.getActiveTournaments(0L, 0);

        Assert.assertNotNull(empty);
        Assert.assertTrue(empty.isEmpty());
    }

    @Test
    public void testGetPastNothing(){
        List<Long> empty = teamHibernateDao.getPastTournaments(0L, 0);

        Assert.assertNotNull(empty);
        Assert.assertTrue(empty.isEmpty());
    }

    @Test
    public void testGetActiveTournaments() {
        List<Long> active = teamHibernateDao.getActiveTournaments(ID, 0);

        Assert.assertNotNull(active);
        Assert.assertFalse(active.isEmpty());
        Assert.assertEquals(GRID_PAGE_SIZE, active.size());
        for (int i = 0; i < GRID_PAGE_SIZE; i++) {
            Assert.assertEquals(0, active.get(i) % 2);
        }
    }

    @Test
    public void testGetPastTournaments(){
        List<Long> past = teamHibernateDao.getPastTournaments(ID,0);

        Assert.assertNotNull(past);
        Assert.assertFalse(past.isEmpty());
        Assert.assertEquals(GRID_PAGE_SIZE,past.size());
        for (int i = 0; i < GRID_PAGE_SIZE; i++) {
            Assert.assertEquals(1,past.get(i) % 2);
        }
    }

    @Test
    public void testGetActiveTournamentsLastPage(){
        List<Long> active = teamHibernateDao.getActiveTournaments(ID, 1);

        Assert.assertNotNull(active);
        Assert.assertFalse(active.isEmpty());
        Assert.assertEquals(1, active.size());
        Assert.assertEquals(0, active.get(0) % 2);
    }

    @Test
    public void testGetPastTournamentsLastPage(){
        List<Long> past = teamHibernateDao.getPastTournaments(ID, 1);

        Assert.assertNotNull(past);
        Assert.assertFalse(past.isEmpty());
        Assert.assertEquals(1, past.size());
        Assert.assertEquals(1, past.get(0) % 2);
    }

    @Test
    public void testGetActiveTournamentsEmptyPage(){
        List<Long> active = teamHibernateDao.getActiveTournaments(ID, 2);

        Assert.assertNotNull(active);
        Assert.assertTrue(active.isEmpty());
    }

    @Test
    public void testGetPastTournamentsEmptyPage(){
        List<Long> past = teamHibernateDao.getActiveTournaments(ID, 2);

        Assert.assertNotNull(past);
        Assert.assertTrue(past.isEmpty());
    }

    @Test
    public void testGetActiveTournamentsPages(){
        long ans = teamHibernateDao.getActivePages(ID);

        Assert.assertEquals(2L, ans);
    }

    @Test
    public void testGetPastTournamentsPages(){
        long ans = teamHibernateDao.getPastPages(ID);

        Assert.assertEquals(2L, ans);
    }

    @Test
    public void testGetUsersTeams(){
        List<Team> ans = teamHibernateDao.getUserTeams(OTHER_ID);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(1,ans.size());
        Team present = ans.get(0);
        Assert.assertEquals(OTHER_TEAM,present.getName());
        Assert.assertEquals(ID.longValue(), (long) present.getOwner().getId());
        Assert.assertNull(present.getBannerId());
        Assert.assertNull(present.getPfpId());
        Assert.assertEquals(ID,present.getId());
    }

    @Test
    public void testUpdateTeam(){
        teamHibernateDao.updateTeam(ID,OTHER_TEAM+"a",ID,ID);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"team",
                "id = ID and pfp_id = id and banner_id = id and name = '" + OTHER_TEAM + "a'"));
    }

    @Test
    public void testNameTaken(){
        boolean isTaken = teamHibernateDao.teamNameTaken(OTHER_TEAM);

        Assert.assertTrue(isTaken);
    }

    @Test
    public void testNameNotTaken(){
        boolean isTaken = teamHibernateDao.teamNameTaken(TEAM);

        Assert.assertFalse(isTaken);
    }

    @Test
    public void testSearchByName(){
        List<Team> foundTeams = teamHibernateDao.searchByName("14", 0L);

        Assert.assertNotNull(foundTeams);
        Assert.assertFalse(foundTeams.isEmpty());
        Assert.assertEquals(2, foundTeams.size());
        for (int i = 0; i < 2; i++) {
            Assert.assertEquals(ID.longValue(), (long) foundTeams.get(i).getOwner().getId());
            Assert.assertNull(foundTeams.get(i).getBannerId());
            Assert.assertNull(foundTeams.get(i).getPfpId());
            Assert.assertEquals(ID + i,foundTeams.get(i).getId().longValue());
            Assert.assertTrue(foundTeams.get(i).getName().contains("14"));
        }
    }

    @Test
    public void testSearchByNameNoOne(){
        List<Team> foundTeams = teamHibernateDao.searchByName("nada", 0L);

        Assert.assertNotNull(foundTeams);
        Assert.assertTrue(foundTeams.isEmpty());
    }

    @Rollback
    @Test
    public void testGetUsersTeamNotInTournament(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"participant");
        List<Team> ans = teamHibernateDao.getUserTeamsBySizeNotInTournament(ID,ID,0L);

        Assert.assertNotNull(ans);
        Assert.assertEquals(2,ans.size());
        for (int i = 0; i < 2; i++) {
            Assert.assertEquals(ID + i, ans.get(i).getId().longValue());
            Assert.assertFalse(ans.get(i).getMembers().isEmpty());
        }
    }

    @Rollback
    @Test
    public void testGetUsersTeamNotInTournamentWithEnoughMembers(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"participant");
        List<Team> ans = teamHibernateDao.getUserTeamsBySizeNotInTournament(ID,ID,2L);

        Assert.assertNotNull(ans);
        Assert.assertEquals(1,ans.size());
        Assert.assertEquals(ID, ans.get(0).getId());
        Assert.assertEquals(2,ans.get(0).getMembers().size());
    }

    @Rollback
    @Test
    public void testGetUsersNoTeamBigEnough(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"participant");
        List<Team> ans = teamHibernateDao.getUserTeamsBySizeNotInTournament(ID,ID,ID);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testGetUsersSomeTeamsAlreadyInTournament(){
        List<Team> ans = teamHibernateDao.getUserTeamsBySizeNotInTournament(ID,ID+1,0L);

        Assert.assertNotNull(ans);
        Assert.assertEquals(1,ans.size());
        Assert.assertEquals(ID+1,ans.get(0).getId().longValue());
    }

    @Test
    public void testGetUsersEveryTeamInTournament(){
        List<Team> ans = teamHibernateDao.getUserTeamsBySizeNotInTournament(ID+1,ID,0L);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testGetUsersNoFreeTeamBigEnough(){
        List<Team> ans = teamHibernateDao.getUserTeamsBySizeNotInTournament(ID,ID,2L);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }
}
