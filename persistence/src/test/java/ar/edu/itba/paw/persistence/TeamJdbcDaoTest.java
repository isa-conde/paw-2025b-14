//package ar.edu.itba.paw.persistence;
//
//import ar.edu.itba.paw.model.Team;
//import ar.edu.itba.paw.model.enums.Elo;
//import ar.edu.itba.paw.model.enums.Region;
//import ar.edu.itba.paw.model.enums.Structure;
//import ar.edu.itba.paw.persistence.Jdbc.TeamJdbcDao;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.SqlParameterSource;
//import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.jdbc.JdbcTestUtils;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.sql.DataSource;
//import java.time.LocalDate;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TestConfig.class)
//@Sql("classpath:db/init.sql")
//@Transactional
//@Rollback
//public class TeamJdbcDaoTest {
//    private static final RowMapper<Team> ROW_MAPPER = (rs, rowNum) -> new Team(rs.getLong("id"), rs.getString("name"), rs.getLong("profile_picture_id"), rs.getLong("banner_id"));
//    private static final String TEAM = "Group 14";
//    private static final String OTHER_TEAM = "Grupo 14";
//    private static final Long ID = 1L;
//    private static final Long OTHER_ID = 2L;
//    private Long used_id;
//    private Long activeTournament;
//    private Long pastTournament;
//
//    @Autowired
//    private DataSource ds;
//
//    @Autowired
//    private TeamJdbcDao teamJdbcDao;
//
//    private JdbcTemplate jdbcTemplate;
//
//    @Before
//    public void setUp(){
//        jdbcTemplate = new JdbcTemplate(ds);
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("team")
//                .usingGeneratedKeyColumns("id");
//        used_id = jdbcInsert.executeAndReturnKey(Map.of("name",OTHER_TEAM,"profile_picture_id",OTHER_ID,"banner_id",OTHER_ID,"owner_id",OTHER_ID)).longValue();
//        SimpleJdbcInsert tournamentInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("tournament")
//                .usingGeneratedKeyColumns("id");
//        SimpleJdbcInsert participantInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("participant")
//                .usingGeneratedKeyColumns("id");
//        SqlParameterSource values = new MapSqlParameterSource()
//                .addValue("creator_id",OTHER_ID)
//                .addValue("name",OTHER_TEAM)
//                .addValue("game_id",OTHER_ID)
//                .addValue("region",Region.LAS)
//                .addValue("elo",Elo.HIGH)
//                .addValue("start_date",LocalDate.of(2026,2,21))
//                .addValue("end_date",LocalDate.of(2027,2,21))
//                .addValue("format",OTHER_TEAM)
//                .addValue("structure",Structure.LEAGUE)
//                .addValue("max_participants",14)
//                .addValue("image_id",OTHER_ID)
//                .addValue("open_inscriptions",true)
//                .addValue("is_finished",false)
//                .addValue("tournament_started",false);
//        activeTournament = tournamentInsert.executeAndReturnKey(values).longValue();
//        participantInsert.executeAndReturnKey(Map.of("team_id",used_id,"tournament_id",activeTournament,"points",0));
//        SqlParameterSource values2 = new MapSqlParameterSource()
//                .addValue("creator_id",OTHER_ID)
//                .addValue("name",OTHER_TEAM)
//                .addValue("game_id",OTHER_ID)
//                .addValue("region",Region.LAS)
//                .addValue("elo",Elo.HIGH)
//                .addValue("start_date",LocalDate.of(2026,2,21))
//                .addValue("end_date",LocalDate.of(2027,2,21))
//                .addValue("format",OTHER_TEAM)
//                .addValue("structure",Structure.LEAGUE)
//                .addValue("max_participants",14)
//                .addValue("image_id",OTHER_ID)
//                .addValue("open_inscriptions",true)
//                .addValue("is_finished",true)
//                .addValue("tournament_started",false);
//        pastTournament = tournamentInsert.executeAndReturnKey(values2).longValue();
//        participantInsert.executeAndReturnKey(Map.of("team_id",used_id,"tournament_id",pastTournament,"points",0));
//    }
//
//    @Test
//    public void testCreate(){
//        Team created = teamJdbcDao.create(TEAM,ID,ID,ID);
//        Assert.assertNotNull(created);
//        List<Team> inserted = jdbcTemplate.query("select * from team where id = ?", ROW_MAPPER, created.getId());
//
//        Assert.assertNotNull(inserted);
//        Assert.assertFalse(inserted.isEmpty());
//        Assert.assertEquals(1,inserted.size());
//        Team retrieved = inserted.get(0);
//        Assert.assertEquals(TEAM,created.getName());
//        Assert.assertEquals(TEAM,retrieved.getName());
//        Assert.assertEquals(ID,created.getOwner_id());
//        Assert.assertEquals(ID,retrieved.getOwner_id());
//        Assert.assertEquals(ID,created.getBanner_id());
//        Assert.assertEquals(ID,retrieved.getBanner_id());
//        Assert.assertEquals(ID,created.getPfp_id());
//        Assert.assertEquals(ID,retrieved.getPfp_id());
//        Assert.assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate,"team"));
//    }
//
//    @Test
//    public void testFindNothing(){
//        Optional<Team> nothing = teamJdbcDao.getById((long) -1);
//
//        Assert.assertNotNull(nothing);
//        Assert.assertTrue(nothing.isEmpty());
//    }
//
//    @Test
//    public void testFindById(){
//        Optional<Team> team = teamJdbcDao.getById(used_id);
//
//        Assert.assertNotNull(team);
//        Assert.assertTrue(team.isPresent());
//        Team present = team.get();
//        Assert.assertEquals(OTHER_TEAM,present.getName());
//        Assert.assertEquals(OTHER_ID,present.getOwner_id());
//        Assert.assertEquals(OTHER_ID,present.getBanner_id());
//        Assert.assertEquals(OTHER_ID,present.getPfp_id());
//        Assert.assertEquals(used_id,present.getId());
//    }
//
//    @Test
//    public void testGetActiveNothing(){
//        List<Long> empty = teamJdbcDao.getActiveTournaments((long) -1);
//
//        Assert.assertNotNull(empty);
//        Assert.assertTrue(empty.isEmpty());
//    }
//
//    @Test
//    public void testGetPastNothing(){
//        List<Long> empty = teamJdbcDao.getPastTournaments((long) -1);
//
//        Assert.assertNotNull(empty);
//        Assert.assertTrue(empty.isEmpty());
//    }
//
//    @Test
//    public void testGetActiveTournaments(){
//        List<Long> active = teamJdbcDao.getActiveTournaments(used_id);
//
//        Assert.assertNotNull(active);
//        Assert.assertFalse(active.isEmpty());
//        Assert.assertEquals(1,active.size());
//        Assert.assertEquals(activeTournament,active.get(0));
//    }
//
//    @Test
//    public void testGetPastTournaments(){
//        List<Long> past = teamJdbcDao.getPastTournaments(used_id);
//
//        Assert.assertNotNull(past);
//        Assert.assertFalse(past.isEmpty());
//        Assert.assertEquals(1,past.size());
//        Assert.assertEquals(pastTournament,past.get(0));
//    }
//
//    @Test
//    public void testGetUsersTeams(){
//        SimpleJdbcInsert teamMemberInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("team_member");
//        teamMemberInsert.execute(Map.of("team_id",used_id,"user_id",ID));
//        List<Team> ans = teamJdbcDao.getUserTeams(ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertFalse(ans.isEmpty());
//        Assert.assertEquals(1,ans.size());
//        Team present = ans.get(0);
//        Assert.assertEquals(OTHER_TEAM,present.getName());
//        Assert.assertEquals(OTHER_ID,present.getOwner_id());
//        Assert.assertEquals(OTHER_ID,present.getBanner_id());
//        Assert.assertEquals(OTHER_ID,present.getPfp_id());
//        Assert.assertEquals(used_id,present.getId());
//    }
//
//    @Test
//    public void testUpdateTeam(){
//        teamJdbcDao.updateTeam(used_id,OTHER_TEAM+"a",ID,ID);
//        List<Team> list = jdbcTemplate.query("select * from team where id = ?",ROW_MAPPER,used_id);
//
//        Assert.assertNotNull(list);
//        Assert.assertFalse(list.isEmpty());
//        Assert.assertEquals(1,list.size());
//        Team team = list.get(0);
//        Assert.assertEquals(OTHER_TEAM+"a", team.getName());
//        Assert.assertEquals(ID, team.getBanner_id());
//        Assert.assertEquals(ID, team.getPfp_id());
//    }
//
//    @Test
//    public void testNameTaken(){
//        Boolean isTaken = teamJdbcDao.teamNameTaken(OTHER_TEAM);
//
//        Assert.assertNotNull(isTaken);
//        Assert.assertTrue(isTaken);
//    }
//
//    @Test
//    public void testNameNotTaken(){
//        Boolean isTaken = teamJdbcDao.teamNameTaken(TEAM);
//
//        Assert.assertNotNull(isTaken);
//        Assert.assertFalse(isTaken);
//    }
//
//    @Test
//    public void testSearchByName(){
//        List<Team> foundTeams = teamJdbcDao.searchByName("Grupo");
//
//        Assert.assertNotNull(foundTeams);
//        Assert.assertFalse(foundTeams.isEmpty());
//        Assert.assertEquals(1, foundTeams.size());
//        Team found = foundTeams.get(0);
//        Assert.assertEquals(OTHER_TEAM, found.getName());
//        Assert.assertEquals(OTHER_ID, found.getOwner_id());
//        Assert.assertEquals(OTHER_ID, found.getPfp_id());
//        Assert.assertEquals(OTHER_ID, found.getBanner_id());
//        Assert.assertEquals(used_id, found.getId());
//    }
//
//    @Test
//    public void testSearchByNameNoOne(){
//        List<Team> foundTeams = teamJdbcDao.searchByName("nada");
//
//        Assert.assertNotNull(foundTeams);
//        Assert.assertTrue(foundTeams.isEmpty());
//    }
//
//    private Long insertTournament(String name) {
//        SimpleJdbcInsert ins = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("tournament")
//                .usingGeneratedKeyColumns("id");
//
//        LocalDate today = LocalDate.now();
//
//        Map<String, Object> values = new HashMap<>();
//        values.put("name", name);
//        values.put("creator_id", ID);
//        values.put("game_id", 5L);
//        values.put("region", "NA");
//        values.put("elo", "LOW");
//        values.put("start_date", today);
//        values.put("end_date", today.plusDays(7));
//        values.put("format", "5vs5");
//        values.put("structure", "LEAGUE");
//        values.put("max_participants", 4);
//        values.put("open_inscriptions", true);
//        values.put("is_finished", false);
//        values.put("is_group_stage", false);
//        values.put("tournament_started", false);
//        values.put("format_id", 4L);
//        values.put("image_id", null);
//        values.put("tournament_winner", null);
//
//        Number id = ins.executeAndReturnKey(values);
//        return id.longValue();
//    }
//
//    @Test
//    public void testGetUserTeamsBySize_NotInTournament_ReturnsTeam() {
//        SimpleJdbcInsert teamMemberInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("team_member");
//        teamMemberInsert.execute(Map.of("team_id", used_id, "user_id", ID));
//        teamMemberInsert.execute(Map.of("team_id", used_id, "user_id", OTHER_ID));
//
//        Long tournamentId = insertTournament("Tournament X");
//
//        List<Team> teams = teamJdbcDao.getUserTeamsBySizeNotInTournament(ID, tournamentId, 2);
//
//        Assert.assertNotNull(teams);
//        Assert.assertFalse(teams.isEmpty());
//        Assert.assertEquals(1, teams.size());
//
//        Team team = teams.get(0);
//        Assert.assertEquals(used_id, team.getId());
//        Assert.assertEquals(OTHER_TEAM, team.getName());
//        Assert.assertEquals(OTHER_ID, team.getOwner_id());
//        Assert.assertEquals(OTHER_ID, team.getPfp_id());
//        Assert.assertEquals(OTHER_ID, team.getBanner_id());
//
//        Integer memberCount = jdbcTemplate.queryForObject(
//                "SELECT COUNT(*) FROM team_member WHERE team_id = ?",
//                Integer.class, used_id
//        );
//        Assert.assertTrue(memberCount >= 2);
//    }
//
//    @Test
//    public void testGetUserNoTeamsBySize_NotInTournament_ReturnsEmpty() {
//        Long tournamentId = insertTournament("Tournament Y");
//
//        List<Team> teams = teamJdbcDao.getUserTeamsBySizeNotInTournament(9L, tournamentId, 12);
//
//        Assert.assertNotNull(teams);
//        Assert.assertTrue(teams.isEmpty());
//    }
//
//    @Test
//    public void testGetUserTeamsBySize_ExcludesTeamAlreadyInTournament() {
//        SimpleJdbcInsert tmIns = new SimpleJdbcInsert(jdbcTemplate).withTableName("team_member");
//        tmIns.execute(Map.of("team_id", used_id, "user_id", ID));
//        tmIns.execute(Map.of("team_id", used_id, "user_id", OTHER_ID));
//
//        Long tournamentId = insertTournament("Torneo Z");
//
//        new SimpleJdbcInsert(jdbcTemplate).withTableName("participant")
//                .execute(Map.of("tournament_id", tournamentId, "team_id", used_id, "points", 0));
//
//        List<Team> teams = teamJdbcDao.getUserTeamsBySizeNotInTournament(ID, tournamentId, 2);
//
//        Assert.assertNotNull(teams);
//        Assert.assertTrue(teams.isEmpty());
//    }
//}
