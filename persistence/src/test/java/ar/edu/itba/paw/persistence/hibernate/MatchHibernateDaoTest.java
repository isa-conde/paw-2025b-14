package ar.edu.itba.paw.persistence.hibernate;

import ar.edu.itba.paw.model.Match.Match;
import ar.edu.itba.paw.persistence.Hibernate.MatchHibernateDao;
import ar.edu.itba.paw.persistence.TestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final String USERNAME = "johndoe";
    private static final String OTHER_USERNAME = "janedoe";
    private static final String OTHER_EMAIL = "another@mail.com";
    private static final String EMAIL = "some@mail.com";
    private static final String PASSWORD = "1234567890";

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
        matchHibernateDao.insertMatch(1L,ID,ID,OTHER_ID,1,4,5,2,true);
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"match",
                "id = 1 and tournament_id = " + ID + " and local_id = " + ID + " and visitor_id = "
        + OTHER_ID + " and stage = 1 and local_score = 4 and visitor_score = 5 and winner = 2 and is_group_stage = true"));
    }

    @Test
    public void testGetMatchWinner(){
        Long ans = matchHibernateDao.getMatchWinner(ID,0L);

        Assert.assertNotNull(ans);
        Assert.assertEquals(OTHER_ID,ans);
    }

    @Test
    public void testGetNoWinner(){
        Long ans = matchHibernateDao.getMatchWinner(ID,1L);

        Assert.assertNull(ans);
    }

    @Test
    public void testGetTournamentMatches(){
        List<Match> ans = matchHibernateDao.getTournamentMatches(ID, 1);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(1,ans.size());
        Match match = ans.get(0);
        Assert.assertEquals(0L,match.getId().longValue());
        Assert.assertEquals(ID,match.getTournamentId());
        Assert.assertEquals(ID,match.getLocalId());
        Assert.assertEquals(OTHER_ID,match.getVisitorId());
        Assert.assertEquals(Integer.valueOf(4),match.getLocalScore());
        Assert.assertEquals(Integer.valueOf(5),match.getVisitorScore());
        Assert.assertEquals(Integer.valueOf(2),match.getWinner());
        Assert.assertTrue(match.getIsGroupStage());
    }

    @Test
    public void testGetNoMatches(){
        List<Match> ans = matchHibernateDao.getTournamentMatches(OTHER_ID, 1);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

//    @Test
//    public void testGetMaxMatchById(){
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("match");
//        Map<String,Object> values = new HashMap<>();
//        values.put("id",OTHER_ID);
//        values.put("tournament_id", OTHER_ID);
//        values.put("local_id", ID);
//        values.put("visitor_id", OTHER_ID);
//        values.put("local_score", 0);
//        values.put("visitor_score", 1);
//        values.put("winner", 2);
//        values.put("stage", 1);
//        values.put("is_group_stage", true);
//        jdbcInsert.execute(values);
//        Long ans = matchHibernateDao.getMaxMatchId(OTHER_ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertEquals(OTHER_ID,ans);
//    }
//
//    @Test
//    public void testGetNoMaxMatch(){
//        Long ans = matchHibernateDao.getMaxMatchId(ID);
//
//        Assert.assertNull(ans);
//    }
//
//    @Test
//    public void testSetMatchWinner(){
//        matchHibernateDao.setMatchWinner(ID,OTHER_ID+1,2);
//        Integer ans = jdbcTemplate.queryForObject("select winner from match where tournament_id = ? and id = ?",Integer.class,OTHER_ID+1,ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertEquals(2,ans.intValue());
//    }
//
//    @Test
//    public void testUpdateMatchLocal(){
//        matchHibernateDao.updateMatchLocal(OTHER_ID,ID,OTHER_ID);
//        Long ans = jdbcTemplate.queryForObject("select local_id from match where tournament_id = ? and id = ?",Long.class,OTHER_ID,ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertNotEquals(ID,ans);
//        Assert.assertEquals(OTHER_ID,ans);
//    }
//
//    @Test
//    public void testUpdateMatchVisitor(){
//        matchHibernateDao.updateMatchVisitor(OTHER_ID,ID,ID);
//        Long ans = jdbcTemplate.queryForObject("select local_id from match where tournament_id = ? and id = ?",Long.class,OTHER_ID,ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertNotEquals(OTHER_ID,ans);
//        Assert.assertEquals(ID,ans);
//    }
//
//    @Test
//    public void testGetMatch(){
//        Match ans = matchHibernateDao.getMatch(OTHER_ID,ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertEquals(ID,ans.getId());
//        Assert.assertEquals(OTHER_ID,ans.getTournamentId());
//        Assert.assertEquals(ID,ans.getLocalId());
//        Assert.assertEquals(OTHER_ID,ans.getVisitorId());
//        Assert.assertEquals(Integer.valueOf(4),ans.getLocalScore());
//        Assert.assertEquals(Integer.valueOf(5),ans.getVisitorScore());
//        Assert.assertEquals(Integer.valueOf(2),ans.getWinner());
//        Assert.assertEquals(Integer.valueOf(1),ans.getStage());
//        Assert.assertTrue(ans.getIsGroupStage());
//    }
//
//    @Test(expected = EmptyResultDataAccessException.class)
//    public void testGetNoMatch(){
//        matchHibernateDao.getMatch(ID,ID);
//    }
//
//    @Test
//    public void testAllMatchesPlayed(){
//        Boolean ans = matchHibernateDao.allMatchesPlayed(OTHER_ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans);
//    }
//
//    @Test
//    public void testNotAllMatchesPlayed(){
//        Boolean ans = matchHibernateDao.allMatchesPlayed(OTHER_ID+1);
//
//        Assert.assertNotNull(ans);
//        Assert.assertFalse(ans);
//    }
//
//    @Test
//    public void testGetMatchStage(){
//        Integer ans = matchHibernateDao.getMatchStage(OTHER_ID,ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertEquals(1,ans.intValue());
//    }
//
//    @Test
//    public void testGetStageMatches(){
//        List<Long> ans = matchHibernateDao.getStageMatchIds(1,OTHER_ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertFalse(ans.isEmpty());
//        Assert.assertEquals(1,ans.size());
//        Assert.assertEquals(ID,ans.get(0));
//    }
//
//    @Test
//    public void testGetStageNoMatches(){
//        List<Long> ans = matchHibernateDao.getStageMatchIds(2,OTHER_ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans.isEmpty());
//    }
//
//    @Test
//    public void testGetMaxStage(){
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("match");
//        Map<String,Object> values = new HashMap<>();
//        values.put("id",OTHER_ID);
//        values.put("tournament_id", OTHER_ID);
//        values.put("local_id", ID);
//        values.put("visitor_id", OTHER_ID);
//        values.put("local_score", 0);
//        values.put("visitor_score", 1);
//        values.put("winner", 2);
//        values.put("stage", 2);
//        values.put("is_group_stage", true);
//        jdbcInsert.execute(values);
//        Integer ans = matchHibernateDao.getTournamentMaxStage(OTHER_ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertEquals(2,ans.intValue());
//    }
//
//    @Test
//    public void testGetNoMaxStage(){
//        Integer ans = matchHibernateDao.getTournamentMaxStage(ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertEquals(0,ans.intValue());
//    }
//
//    @Test
//    public void testGetGroupMaxStage(){
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("match");
//        Map<String,Object> values = new HashMap<>();
//        values.put("id",OTHER_ID);
//        values.put("tournament_id", OTHER_ID);
//        values.put("local_id", ID);
//        values.put("visitor_id", OTHER_ID);
//        values.put("local_score", 0);
//        values.put("visitor_score", 1);
//        values.put("winner", 2);
//        values.put("stage", 2);
//        values.put("is_group_stage", true);
//        jdbcInsert.execute(values);
//        Integer ans = matchHibernateDao.getTournamentGroupMaxStage(OTHER_ID,1);
//
//        Assert.assertNotNull(ans);
//        Assert.assertEquals(2,ans.intValue());
//    }
}
