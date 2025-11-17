package ar.edu.itba.paw.persistence.hibernate;

import ar.edu.itba.paw.interfaces.exception.MatchNotFoundException;
import ar.edu.itba.paw.interfaces.exception.MissingWinnerException;
import ar.edu.itba.paw.model.Match.Match;
import ar.edu.itba.paw.persistence.Hibernate.MatchHibernateDao;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class MatchHibernateDaoTest {
    @PersistenceContext
    private EntityManager em;

    private static final Long ID = 100L;
    private static final Long OTHER_ID = 101L;
    private static final Long UNDETERMINED_MATCH = 102L;

    @Autowired
    private DataSource ds;

    @Autowired
    private MatchHibernateDao matchHibernateDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testInsertMatch(){
        matchHibernateDao.insertMatch(0L,OTHER_ID,ID,OTHER_ID,1,4,5,2,true);
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"match",
                "id = 0 and tournament_id = " + OTHER_ID + " and local_id = " + ID + " and visitor_id = "
        + OTHER_ID + " and stage = 1 and local_score = 4 and visitor_score = 5 and winner = 2 and is_group_stage = true"));
    }

    @Test
    public void testGetMatchWinner(){
        Long ans = matchHibernateDao.getMatchWinner(ID,0L);

        Assert.assertNotNull(ans);
        Assert.assertEquals(OTHER_ID,ans);
    }

    @Test( expected = MatchNotFoundException.class)
    public void testGetNoWinner(){
        Long ans = matchHibernateDao.getMatchWinner(OTHER_ID,1L);

        Assert.assertNull(ans);
    }

    @Test( expected = MissingWinnerException.class)
    public void testGetNoMatchWinner(){
        Long ans = matchHibernateDao.getMatchWinner(102L,0L);

        Assert.assertNull(ans);
    }


    @Test
    public void testGetTournamentMatches(){
        List<Match> ans = matchHibernateDao.getTournamentMatches(ID, null);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(6,ans.size());
        for (int i = 0; i < 6; i++) {
            Assert.assertEquals(i,ans.get(i).getId().intValue());
            Assert.assertEquals(ID,ans.get(i).getTournamentId());
            Assert.assertEquals(Integer.valueOf(4),ans.get(i).getLocalScore());
            Assert.assertEquals(Integer.valueOf(5),ans.get(i).getVisitorScore());
            Assert.assertEquals(Integer.valueOf(2),ans.get(i).getWinner());
            Assert.assertTrue(ans.get(i).getIsGroupStage());
        }
    }

    @Test
    public void testGetNoMatches(){
        List<Match> ans = matchHibernateDao.getTournamentMatches(OTHER_ID, null);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testGetMaxMatchById(){
        Long ans = matchHibernateDao.getMaxMatchId(ID);

        Assert.assertNotNull(ans);
        Assert.assertEquals(5,ans.intValue());
    }

    @Test
    public void testGetNoMaxMatch(){
        Long ans = matchHibernateDao.getMaxMatchId(OTHER_ID);

        Assert.assertNull(ans);
    }

    @Test
    public void testSetMatchWinner(){
        matchHibernateDao.setMatchResults(0L,UNDETERMINED_MATCH,1,0,1, LocalDate.of(2025,11,15));
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"match",
    "id = 0 and tournament_id = " + UNDETERMINED_MATCH +" and local_score = 1 and visitor_score = 0 and " +
            "winner = 1 and date = '" + LocalDate.of(2025,11,15).format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"));
    }

    @Test
    public void testUpdateMatchLocal(){
        matchHibernateDao.updateMatchLocal(UNDETERMINED_MATCH,0L,ID);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"match",
                "id = 0 and tournament_id = " + UNDETERMINED_MATCH + " and local_id = " + ID));
    }

    @Test
    public void testUpdateMatchVisitor(){
        matchHibernateDao.updateMatchVisitor(UNDETERMINED_MATCH,0L,ID);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"match",
                "id = 0 and tournament_id = " + UNDETERMINED_MATCH + " and visitor_id = " + ID));
    }

    @Test
    public void testGetMatch(){
        Match ans = matchHibernateDao.getMatch(ID,0L);

        Assert.assertNotNull(ans);
        Assert.assertEquals(0,ans.getId().intValue());
        Assert.assertEquals(ID,ans.getTournamentId());
        Assert.assertEquals(ID,ans.getLocalId());
        Assert.assertEquals(OTHER_ID,ans.getVisitorId());
        Assert.assertEquals(Integer.valueOf(4),ans.getLocalScore());
        Assert.assertEquals(Integer.valueOf(5),ans.getVisitorScore());
        Assert.assertEquals(Integer.valueOf(2),ans.getWinner());
        Assert.assertTrue(ans.getIsGroupStage());
    }

    @Test
    public void testGetNoMatch(){
        Match ans = matchHibernateDao.getMatch(OTHER_ID,0L);

        Assert.assertNull(ans);
    }

    @Test
    public void testAllMatchesPlayed(){
        boolean ans = matchHibernateDao.allMatchesPlayed(ID);

        Assert.assertTrue(ans);
    }

    @Test
    public void testNotAllMatchesPlayed(){
        boolean ans = matchHibernateDao.allMatchesPlayed(UNDETERMINED_MATCH);

        Assert.assertFalse(ans);
    }

    @Test
    public void testGetMatchStage(){
        Integer ans = matchHibernateDao.getMatchStage(ID,0L);

        Assert.assertNotNull(ans);
        Assert.assertEquals(1,ans.intValue());
    }

    @Test
    public void testGetStageMatches(){
        List<Long> ans = matchHibernateDao.getStageMatchIds(1,ID);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(2,ans.size());
        Assert.assertEquals(0,ans.get(0).intValue());
        Assert.assertEquals(5,ans.get(1).intValue());
    }

    @Test
    public void testGetStageNoMatches(){
        List<Long> ans = matchHibernateDao.getStageMatchIds(4,ID);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testGetMaxStage(){
        Integer ans = matchHibernateDao.getTournamentMaxStage(ID);

        Assert.assertNotNull(ans);
        Assert.assertEquals(3,ans.intValue());
    }

    @Test
    public void testGetNoMaxStage(){
        Integer ans = matchHibernateDao.getTournamentMaxStage(OTHER_ID);

        Assert.assertNotNull(ans);
        Assert.assertEquals(0,ans.intValue());
    }

    @Test
    public void testGetGroupMaxStage(){
        Integer ans = matchHibernateDao.getTournamentGroupMaxStage(OTHER_ID,1);

        Assert.assertNotNull(ans);
        Assert.assertEquals(0,ans.intValue());
    }
}
