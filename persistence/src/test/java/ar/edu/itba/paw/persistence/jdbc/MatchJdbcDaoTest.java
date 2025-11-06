//package ar.edu.itba.paw.persistence.jdbc;
//
//import ar.edu.itba.paw.model.Match.Match;
//import ar.edu.itba.paw.model.MatchInfo;
//import ar.edu.itba.paw.persistence.Jdbc.MatchJdbcDao;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.jdbc.JdbcTestUtils;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TestConfig.class)
//@Sql("classpath:db/init.sql")
//@Transactional
//@Rollback
//public class MatchJdbcDaoTest {
//    private static final RowMapper<Match> ROW_MAPPER_MATCH = (rs, rowNum) -> new Match(
//            rs.getLong("id"),
//            rs.getLong("tournament_id"),
//            rs.getObject("local_id") != null ? rs.getLong("local_id") : null,
//            rs.getObject("visitor_id") != null ? rs.getLong("visitor_id") : null,
//            rs.getObject("local_score") != null ? rs.getInt("local_score") : null,
//            rs.getObject("visitor_score") != null ? rs.getInt("visitor_score") : null,
//            rs.getObject("winner") != null ? rs.getInt("winner") : null,
//            rs.getInt("stage"),
//            rs.getObject("is_group_stage") != null ? rs.getBoolean("is_group_stage") : null
//    );
//    private static final Long ID = 1L;
//    private static final Long OTHER_ID = 2L;
//    private static final String USERNAME = "johndoe";
//    private static final String OTHER_USERNAME = "janedoe";
//    private static final String OTHER_EMAIL = "another@mail.com";
//    private static final String EMAIL = "some@mail.com";
//    private static final String PASSWORD = "1234567890";
//
//    @Autowired
//    private DataSource ds;
//
//    @Autowired
//    private MatchJdbcDao matchJdbcDao;
//
//    private JdbcTemplate jdbcTemplate;
//
//    @Before
//    public void setUp(){
//        jdbcTemplate = new JdbcTemplate(ds);
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("match");
//        Map<String,Object> values;
//        for (int i = 0; i < 2; i++) {
//            values = new HashMap<>();
//            values.put("id",ID);
//            values.put("tournament_id", OTHER_ID+i);
//            values.put("local_id", ID);
//            values.put("visitor_id", OTHER_ID);
//            values.put("local_score", (i==0) ? 4 : null);
//            values.put("visitor_score", (i==0) ? 5 : null);
//            values.put("winner", (i==0) ? 2 : null);
//            values.put("stage", 1);
//            values.put("is_group_stage", i==0);
//            jdbcInsert.execute(values);
//        }
//        SimpleJdbcInsert insertUser = new SimpleJdbcInsert(jdbcTemplate).withTableName("users");
//        insertUser.execute(Map.of("id",ID,"email",EMAIL,"username",USERNAME,"password",PASSWORD,"verified",true));
//        insertUser.execute(Map.of("id",OTHER_ID,"email",OTHER_EMAIL,"username",OTHER_USERNAME,"password",PASSWORD,"verified",true));
//        SimpleJdbcInsert insertParticipant = new SimpleJdbcInsert(jdbcTemplate).withTableName("participant");
//        for (int i = 0; i < 2; i++) {
//            insertParticipant.execute(Map.of("id",ID+i*2,"user_id",ID+i,"tournament_id",OTHER_ID,"points",(i==0)?4:5,"groupNumber",1));
//            insertParticipant.execute(Map.of("id",ID+1+i*2,"user_id",ID+i,"tournament_id",OTHER_ID+1,"points",0));
//        }
//    }
//
//    @Test
//    public void testInsertMatch(){
//        matchJdbcDao.insertMatch(ID,ID,ID,OTHER_ID,1,4,5,2,true);
//        List<Match> inserted = jdbcTemplate.query("select * from match where id = ? and tournament_id = ?",ROW_MAPPER_MATCH,ID,ID);
//
//        Assert.assertNotNull(inserted);
//        Assert.assertFalse(inserted.isEmpty());
//        Assert.assertEquals(1,inserted.size());
//        Match match = inserted.get(0);
//        Assert.assertEquals(ID,match.getId());
//        Assert.assertEquals(ID,match.getTournamentId());
//        Assert.assertEquals(ID,match.getLocalId());
//        Assert.assertEquals(OTHER_ID,match.getVisitorId());
//        Assert.assertEquals(Integer.valueOf(4),match.getLocalScore());
//        Assert.assertEquals(Integer.valueOf(5),match.getVisitorScore());
//        Assert.assertEquals(Integer.valueOf(2),match.getWinner());
//        Assert.assertEquals(Integer.valueOf(1),match.getStage());
//        Assert.assertTrue(match.getIsGroupStage());
//        Assert.assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate,"match"));
//    }
//
//    @Test
//    public void testGetMatchWinner(){
//        Long ans = matchJdbcDao.getMatchWinner(OTHER_ID,ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertEquals(OTHER_ID,ans);
//    }
//
//    @Test
//    public void testGetNoWinner(){
//        Long ans = matchJdbcDao.getMatchWinner(OTHER_ID+1,ID);
//
//        Assert.assertNull(ans);
//    }
//
//    @Test
//    public void testGetTournamentMatches(){
//        List<MatchInfo> ans = matchJdbcDao.getTournamentMatches(OTHER_ID, 1);
//
//        Assert.assertNotNull(ans);
//        Assert.assertFalse(ans.isEmpty());
//        Assert.assertEquals(1,ans.size());
//        MatchInfo match = ans.get(0);
//        Assert.assertEquals(ID,match.getId());
//        Assert.assertEquals(OTHER_ID,match.getTournamentId());
//        Assert.assertEquals(ID,match.getLocalId());
//        Assert.assertEquals(OTHER_ID,match.getVisitorId());
//        Assert.assertEquals(Integer.valueOf(4),match.getLocalScore());
//        Assert.assertEquals(Integer.valueOf(5),match.getVisitorScore());
//        Assert.assertEquals(Integer.valueOf(2),match.getWinner());
//        Assert.assertEquals(Integer.valueOf(1),match.getStage());
//        Assert.assertTrue(match.getIsGroupStage());
//        Assert.assertEquals(Integer.valueOf(1),match.getGroupNumber());
//    }
//
//    @Test
//    public void testGetTournamentMatchesNoGroup(){
//        List<MatchInfo> ans = matchJdbcDao.getTournamentMatches(OTHER_ID+1, 1);
//
//        Assert.assertNotNull(ans);
//        Assert.assertFalse(ans.isEmpty());
//        Assert.assertEquals(1,ans.size());
//        MatchInfo match = ans.get(0);
//        Assert.assertEquals(ID,match.getId());
//        Assert.assertEquals(OTHER_ID+1,match.getTournamentId().longValue());
//        Assert.assertEquals(ID,match.getLocalId());
//        Assert.assertEquals(OTHER_ID,match.getVisitorId());
//        Assert.assertNull(match.getLocalScore());
//        Assert.assertNull(match.getVisitorScore());
//        Assert.assertNull(match.getWinner());
//        Assert.assertEquals(1,match.getStage().intValue());
//        Assert.assertFalse(match.getIsGroupStage());
//        Assert.assertEquals(Integer.valueOf(0),match.getGroupNumber());
//    }
//
//    @Test
//    public void testGetNoMatches(){
//        List<MatchInfo> ans = matchJdbcDao.getTournamentMatches(ID, 1);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans.isEmpty());
//    }
//
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
//        Long ans = matchJdbcDao.getMaxMatchId(OTHER_ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertEquals(OTHER_ID,ans);
//    }
//
//    @Test
//    public void testGetNoMaxMatch(){
//        Long ans = matchJdbcDao.getMaxMatchId(ID);
//
//        Assert.assertNull(ans);
//    }
//
//    @Test
//    public void testSetMatchWinner(){
//        matchJdbcDao.setMatchWinner(ID,OTHER_ID+1,2);
//        Integer ans = jdbcTemplate.queryForObject("select winner from match where tournament_id = ? and id = ?",Integer.class,OTHER_ID+1,ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertEquals(2,ans.intValue());
//    }
//
//    @Test
//    public void testUpdateMatchLocal(){
//        matchJdbcDao.updateMatchLocal(OTHER_ID,ID,OTHER_ID);
//        Long ans = jdbcTemplate.queryForObject("select local_id from match where tournament_id = ? and id = ?",Long.class,OTHER_ID,ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertNotEquals(ID,ans);
//        Assert.assertEquals(OTHER_ID,ans);
//    }
//
//    @Test
//    public void testUpdateMatchVisitor(){
//        matchJdbcDao.updateMatchVisitor(OTHER_ID,ID,ID);
//        Long ans = jdbcTemplate.queryForObject("select local_id from match where tournament_id = ? and id = ?",Long.class,OTHER_ID,ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertNotEquals(OTHER_ID,ans);
//        Assert.assertEquals(ID,ans);
//    }
//
//    @Test
//    public void testGetMatch(){
//        Match ans = matchJdbcDao.getMatch(OTHER_ID,ID);
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
//        matchJdbcDao.getMatch(ID,ID);
//    }
//
//    @Test
//    public void testAllMatchesPlayed(){
//        Boolean ans = matchJdbcDao.allMatchesPlayed(OTHER_ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans);
//    }
//
//    @Test
//    public void testNotAllMatchesPlayed(){
//        Boolean ans = matchJdbcDao.allMatchesPlayed(OTHER_ID+1);
//
//        Assert.assertNotNull(ans);
//        Assert.assertFalse(ans);
//    }
//
//    @Test
//    public void testGetMatchStage(){
//        Integer ans = matchJdbcDao.getMatchStage(OTHER_ID,ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertEquals(1,ans.intValue());
//    }
//
//    @Test
//    public void testGetStageMatches(){
//        List<Long> ans = matchJdbcDao.getStageMatchIds(1,OTHER_ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertFalse(ans.isEmpty());
//        Assert.assertEquals(1,ans.size());
//        Assert.assertEquals(ID,ans.get(0));
//    }
//
//    @Test
//    public void testGetStageNoMatches(){
//        List<Long> ans = matchJdbcDao.getStageMatchIds(2,OTHER_ID);
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
//        Integer ans = matchJdbcDao.getTournamentMaxStage(OTHER_ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertEquals(2,ans.intValue());
//    }
//
//    @Test
//    public void testGetNoMaxStage(){
//        Integer ans = matchJdbcDao.getTournamentMaxStage(ID);
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
//        Integer ans = matchJdbcDao.getTournamentGroupMaxStage(OTHER_ID,1);
//
//        Assert.assertNotNull(ans);
//        Assert.assertEquals(2,ans.intValue());
//    }
//}
