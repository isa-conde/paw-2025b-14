package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class TournamentJdbcDaoTest {
    private static final Genre GENRE = Genre.MOBA;
    private static final String NAME = "Jerma Rumble";
    private static final Elo ELO = Elo.MID;
    private static final Region REGION = Region.LAS;
    private static final Structure STRUCTURE = Structure.LEAGUE;
    private static final LocalDate START_DATE = LocalDate.of(2025, 2, 21);
    private static final LocalDate END_DATE = LocalDate.of(2026, 2, 21);
    private static final String FORMAT = "some format";
    private static final Long ID = 1L;
    private static final Long OTHER_ID = 2L;
    private static final Integer MAX_PARTICIPANTS = 8;
    private static final RowMapper<Tournament> ROW_MAPPER = (rs, rowNum) -> new Tournament(
            rs.getLong("id"),
            rs.getLong("creator_id"),
            rs.getString("name"),
            rs.getLong("game_id"),
            Region.valueOf(rs.getString("region")),
            Elo.valueOf(rs.getString("elo")),
            rs.getDate("start_date").toLocalDate(),
            rs.getDate("end_date").toLocalDate(),
            rs.getString("format"),
            Structure.valueOf(rs.getString("structure")),
            rs.getInt("max_participants"),
            rs.getLong("image_id"),
            rs.getBoolean("open_inscriptions"),
            rs.getBoolean("is_finished"),
            rs.getLong("tournament_winner"),
            rs.getBoolean("is_group_stage"),
            rs.getBoolean("tournament_started"),
            rs.getLong("format_id"));

    @Autowired
    private DataSource ds;

    @Autowired
    private TournamentJdbcDao tournamentJdbcDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tournament");
        SqlParameterSource values = new MapSqlParameterSource()
                .addValue("id",OTHER_ID)
                .addValue("creator_id",ID)
                .addValue("name",NAME)
                .addValue("game_id",ID)
                .addValue("region", REGION)
                .addValue("elo", ELO)
                .addValue("start_date", START_DATE)
                .addValue("end_date", END_DATE)
                .addValue("format", FORMAT)
                .addValue("structure", STRUCTURE)
                .addValue("max_participants", MAX_PARTICIPANTS)
                .addValue("image_id", ID)
                .addValue("open_inscriptions", true)
                .addValue("is_finished", false)
                .addValue("tournament_started", false)
                .addValue("format_id", ID);
        jdbcInsert.execute(values);
        SimpleJdbcInsert gameInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("game");
        gameInsert.execute(Map.of("id",OTHER_ID,"name",NAME,"genre",GENRE,"image_id",ID));
        gameInsert.execute(Map.of("id",ID,"name",NAME + "a","genre",GENRE,"image_id",ID));
        SimpleJdbcInsert formatInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("game_format");
        formatInsert.execute(Map.of("id",ID,"name",FORMAT,"players_per_team",6,"game_id",ID));
    }

    @Test
    public void testCreate(){
        Tournament tournament = tournamentJdbcDao.create(
                ID,
                NAME,
                ID,
                REGION,
                ELO,
                START_DATE,
                END_DATE,
                FORMAT,
                STRUCTURE,
                MAX_PARTICIPANTS,
                ID,
                true,
                false,
                ID);
        List<Tournament> queried = jdbcTemplate.query("select * from tournament where id = ?",
                ROW_MAPPER,tournament.getId());

        Assert.assertNotNull(queried);
        Assert.assertFalse(queried.isEmpty());
        Assert.assertEquals(1,queried.size());
        Tournament inserted = queried.get(0);
        Assert.assertNotNull(tournament);
        Assert.assertEquals(ID,tournament.getCreator_id());
        Assert.assertEquals(NAME, tournament.getName());
        Assert.assertEquals(ID,tournament.getGame_id());
        Assert.assertEquals(REGION, tournament.getRegion());
        Assert.assertEquals(ELO, tournament.getElo());
        Assert.assertEquals(START_DATE, tournament.getStart_date());
        Assert.assertEquals(END_DATE, tournament.getEnd_date());
        Assert.assertEquals(FORMAT, tournament.getFormat());
        Assert.assertEquals(STRUCTURE, tournament.getStructure());
        Assert.assertEquals(MAX_PARTICIPANTS, tournament.getMax_participants());
        Assert.assertEquals(ID,tournament.getImage_id());
        Assert.assertTrue(tournament.getOpenInscriptions());
        Assert.assertFalse(tournament.getFinished());
        Assert.assertEquals(ID,tournament.getFormat_id());
        Assert.assertEquals(ID,inserted.getCreator_id());
        Assert.assertEquals(NAME, inserted.getName());
        Assert.assertEquals(ID,inserted.getGame_id());
        Assert.assertEquals(REGION, inserted.getRegion());
        Assert.assertEquals(ELO, inserted.getElo());
        Assert.assertEquals(START_DATE, inserted.getStart_date());
        Assert.assertEquals(END_DATE, inserted.getEnd_date());
        Assert.assertEquals(FORMAT, inserted.getFormat());
        Assert.assertEquals(STRUCTURE, inserted.getStructure());
        Assert.assertEquals(MAX_PARTICIPANTS, inserted.getMax_participants());
        Assert.assertEquals(ID,inserted.getImage_id());
        Assert.assertTrue(inserted.getOpenInscriptions());
        Assert.assertFalse(inserted.getFinished());
        Assert.assertEquals(ID,inserted.getFormat_id());
        Assert.assertEquals(2,JdbcTestUtils.countRowsInTable(jdbcTemplate,"tournament"));
    }

    @Test
    public void testFindById(){
        Optional<Tournament> found = tournamentJdbcDao.findById(OTHER_ID);

        Assert.assertTrue(found.isPresent());
        Tournament tournament = found.get();
        Assert.assertEquals(OTHER_ID, tournament.getId());
        Assert.assertEquals(ID, tournament.getCreator_id());
        Assert.assertEquals(NAME, tournament.getName());
        Assert.assertEquals(ID, tournament.getGame_id());
        Assert.assertEquals(REGION, tournament.getRegion());
        Assert.assertEquals(ELO, tournament.getElo());
        Assert.assertEquals(START_DATE, tournament.getStart_date());
        Assert.assertEquals(END_DATE, tournament.getEnd_date());
        Assert.assertEquals(FORMAT, tournament.getFormat());
        Assert.assertEquals(STRUCTURE, tournament.getStructure());
        Assert.assertEquals(MAX_PARTICIPANTS, tournament.getMax_participants());
        Assert.assertEquals(ID, tournament.getImage_id());
        Assert.assertTrue(tournament.getOpenInscriptions());
        Assert.assertFalse(tournament.getFinished());
        Assert.assertEquals(ID, tournament.getFormat_id());
    }

    @Test
    public void testFindByIdNotFound(){
        Optional<Tournament> found = tournamentJdbcDao.findById(-1L);

        Assert.assertFalse(found.isPresent());
    }

    @Test
    public void testFindGameTournaments(){
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tournament");
        SqlParameterSource values = new MapSqlParameterSource()
                .addValue("id",ID)
                .addValue("creator_id",ID)
                .addValue("name",NAME)
                .addValue("game_id",OTHER_ID)
                .addValue("region", REGION)
                .addValue("elo", ELO)
                .addValue("start_date", START_DATE)
                .addValue("end_date", END_DATE)
                .addValue("format", FORMAT)
                .addValue("structure", STRUCTURE)
                .addValue("max_participants", MAX_PARTICIPANTS)
                .addValue("image_id", ID)
                .addValue("open_inscriptions", true)
                .addValue("is_finished", false)
                .addValue("tournament_started", false)
                .addValue("format_id", ID);
        jdbcInsert.execute(values);
        values = new MapSqlParameterSource()
                .addValue("id",OTHER_ID+1)
                .addValue("creator_id",ID)
                .addValue("name",NAME)
                .addValue("game_id",ID)
                .addValue("region", REGION)
                .addValue("elo", ELO)
                .addValue("start_date", START_DATE)
                .addValue("end_date", END_DATE)
                .addValue("format", FORMAT)
                .addValue("structure", STRUCTURE)
                .addValue("max_participants", MAX_PARTICIPANTS)
                .addValue("image_id", ID)
                .addValue("open_inscriptions", true)
                .addValue("is_finished", false)
                .addValue("tournament_started", false)
                .addValue("format_id", ID);
        jdbcInsert.execute(values);
        List<Tournament> tournaments = tournamentJdbcDao.findGameTournaments(ID);

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(2, tournaments.size());
        Assert.assertTrue(tournaments.stream().allMatch(t -> t.getGame_id().equals(ID)));
    }

    @Test
    public void testFindGameTournamentsNone(){
        List<Tournament> tournaments = tournamentJdbcDao.findGameTournaments(-1L);

        Assert.assertNotNull(tournaments);
        Assert.assertTrue(tournaments.isEmpty());
    }

    @Test
    public void testHasNotTournamentStarted(){
        Boolean started = tournamentJdbcDao.isTournamentStarted(OTHER_ID);

        Assert.assertFalse(started);
    }

    @Test
    public void testHasTournamentStarted(){
        jdbcTemplate.update("update tournament set tournament_started = true where id = "+OTHER_ID);
        Boolean started = tournamentJdbcDao.isTournamentStarted(OTHER_ID);

        Assert.assertTrue(started);
    }

    @Test
    public void testHasNonexistentTournamentStarted(){
        Boolean started = tournamentJdbcDao.isTournamentStarted((long)-1);

        Assert.assertFalse(started);
    }

    @Test
    public void testSetIsGroupStage(){
        tournamentJdbcDao.setIsGroupStage(OTHER_ID, true);

        Boolean isGroupStage = jdbcTemplate.queryForObject("SELECT is_group_stage FROM tournament WHERE id = ?", Boolean.class, OTHER_ID);
        Assert.assertTrue(isGroupStage);
    }

    @Test
    public void testGetTournamentStructure(){
        Structure structure = tournamentJdbcDao.getTournamentStructure(OTHER_ID);

        Assert.assertEquals(STRUCTURE, structure);
    }

    @Test
    public void testGetTournamentStructureNotFound(){
        Structure structure = tournamentJdbcDao.getTournamentStructure(-1L);

        Assert.assertNull(structure);
    }

    @Test
    public void testFindByCreator(){
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tournament");
        SqlParameterSource values = new MapSqlParameterSource()
                .addValue("id",ID)
                .addValue("creator_id",OTHER_ID)
                .addValue("name",NAME)
                .addValue("game_id",OTHER_ID)
                .addValue("region", REGION)
                .addValue("elo", ELO)
                .addValue("start_date", START_DATE)
                .addValue("end_date", END_DATE)
                .addValue("format", FORMAT)
                .addValue("structure", STRUCTURE)
                .addValue("max_participants", MAX_PARTICIPANTS)
                .addValue("image_id", ID)
                .addValue("open_inscriptions", true)
                .addValue("is_finished", false)
                .addValue("tournament_started", false)
                .addValue("format_id", ID);
        jdbcInsert.execute(values);
        values = new MapSqlParameterSource()
                .addValue("id",OTHER_ID+1)
                .addValue("creator_id",ID)
                .addValue("name",NAME)
                .addValue("game_id",ID)
                .addValue("region", REGION)
                .addValue("elo", ELO)
                .addValue("start_date", START_DATE)
                .addValue("end_date", END_DATE)
                .addValue("format", FORMAT)
                .addValue("structure", STRUCTURE)
                .addValue("max_participants", MAX_PARTICIPANTS)
                .addValue("image_id", ID)
                .addValue("open_inscriptions", true)
                .addValue("is_finished", false)
                .addValue("tournament_started", false)
                .addValue("format_id", ID);
        jdbcInsert.execute(values);
        List<Tournament> tournaments = tournamentJdbcDao.findByCreator(ID, 1L);

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(2, tournaments.size());
        Assert.assertTrue(tournaments.stream().allMatch(t -> t.getCreator_id().equals(ID)));
    }

    @Test
    public void testFindByCreatorNone(){
        List<Tournament> tournaments = tournamentJdbcDao.findByCreator(-1L, 1L);

        Assert.assertNotNull(tournaments);
        Assert.assertTrue(tournaments.isEmpty());
    }

    @Test
    public void testSetFinished(){
        tournamentJdbcDao.setFinished(OTHER_ID);

        Boolean isFinished = jdbcTemplate.queryForObject("SELECT is_finished FROM tournament WHERE id = ?", Boolean.class, OTHER_ID);
        Assert.assertTrue(isFinished);
        LocalDate endDate = jdbcTemplate.queryForObject("SELECT end_date FROM tournament WHERE id = ?", LocalDate.class, OTHER_ID);
        Assert.assertEquals(LocalDate.now(), endDate);
    }

    @Test
    public void testCloseInscriptions(){
        tournamentJdbcDao.closeInscriptions(OTHER_ID);

        Boolean openInscriptions = jdbcTemplate.queryForObject("SELECT open_inscriptions FROM tournament WHERE id = ?", Boolean.class, OTHER_ID);
        Assert.assertFalse(openInscriptions);
    }

    @Test
    public void testSetTournamentWinner(){
        tournamentJdbcDao.setTournamentWinner(OTHER_ID, ID);

        Long tournamentWinner = jdbcTemplate.queryForObject("SELECT tournament_winner FROM tournament WHERE id = ?", Long.class, OTHER_ID);
        Assert.assertEquals(ID, tournamentWinner);
    }

    @Test
    public void testStartTournament(){
        tournamentJdbcDao.startTournament(OTHER_ID);

        Boolean started = jdbcTemplate.queryForObject("SELECT tournament_started FROM tournament WHERE id = ?", Boolean.class, OTHER_ID);
        Assert.assertTrue(started);
    }

    @Test
    public void testGetIsGroupStage(){
        jdbcTemplate.update("update tournament set is_group_stage = true");
        Boolean isGroupStage = tournamentJdbcDao.getIsGroupStage(OTHER_ID);

        Assert.assertTrue(isGroupStage);
    }

    @Test
    public void testGetTournamentParticipantsCount(){
        SimpleJdbcInsert participantInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("participant");
        participantInsert.execute(Map.of("id",ID,"user_id", ID, "tournament_id", OTHER_ID, "points", 0));
        participantInsert.execute(Map.of("id",OTHER_ID,"user_id", OTHER_ID, "tournament_id", OTHER_ID, "points", 0));
        int count = tournamentJdbcDao.getTournamentParticipantsCount(OTHER_ID);

        Assert.assertEquals(2, count);
    }

    @Test
    public void testUpdateTournamentInfo(){
        String newName = "Updated Name";
        LocalDate newStartDate = LocalDate.of(2025, 9, 30);
        LocalDate newEndDate = LocalDate.of(2026, 9, 30);
        Integer newMaxParticipants = 16;
        tournamentJdbcDao.updateTournamentInfo(OTHER_ID, newName, newStartDate, newEndDate, newMaxParticipants);
        Tournament updated = jdbcTemplate.queryForObject("SELECT * FROM tournament WHERE id = ?", ROW_MAPPER, OTHER_ID);

        Assert.assertNotNull(updated);
        Assert.assertEquals(newName, updated.getName());
        Assert.assertNotEquals(NAME,updated.getName());
        Assert.assertEquals(newStartDate, updated.getStart_date());
        Assert.assertNotEquals(START_DATE,updated.getStart_date());
        Assert.assertEquals(newEndDate, updated.getEnd_date());
        Assert.assertNotEquals(END_DATE,updated.getEnd_date());
        Assert.assertEquals(newMaxParticipants, updated.getMax_participants());
        Assert.assertNotEquals(MAX_PARTICIPANTS,updated.getMax_participants());
    }

    @Test
    public void testGetPageAmount(){
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("tournament");
        for (int i = 0; i < 4; i++) {
            SqlParameterSource values = new MapSqlParameterSource()
                    .addValue("id",OTHER_ID+1+i)
                    .addValue("creator_id",OTHER_ID)
                    .addValue("name",NAME+i)
                    .addValue("game_id",ID)
                    .addValue("region", REGION)
                    .addValue("elo", ELO)
                    .addValue("start_date", START_DATE)
                    .addValue("end_date", END_DATE)
                    .addValue("format", FORMAT)
                    .addValue("structure", STRUCTURE)
                    .addValue("max_participants", MAX_PARTICIPANTS)
                    .addValue("image_id", ID)
                    .addValue("open_inscriptions", true)
                    .addValue("is_finished", false)
                    .addValue("tournament_started", false)
                    .addValue("format_id", ID);
            jdbcInsert.execute(values);
        }
        TournamentFilter filter = new TournamentFilter();
        filter.setGame_id(ID);
        int pageSize = 2;

        Integer pageAmount = tournamentJdbcDao.getPageAmount(pageSize, filter);

        Assert.assertEquals(3, pageAmount.intValue());
    }

    @Test
    public void testSearchByName(){
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tournament");
        SqlParameterSource values = new MapSqlParameterSource()
                .addValue("id",ID)
                .addValue("creator_id",OTHER_ID)
                .addValue("name",NAME+ " 2")
                .addValue("game_id",OTHER_ID)
                .addValue("region", REGION)
                .addValue("elo", ELO)
                .addValue("start_date", START_DATE)
                .addValue("end_date", END_DATE)
                .addValue("format", FORMAT)
                .addValue("structure", STRUCTURE)
                .addValue("max_participants", MAX_PARTICIPANTS)
                .addValue("image_id", ID)
                .addValue("open_inscriptions", true)
                .addValue("is_finished", false)
                .addValue("tournament_started", false)
                .addValue("format_id", ID);
        jdbcInsert.execute(values);
        values = new MapSqlParameterSource()
                .addValue("id",OTHER_ID+1)
                .addValue("creator_id",ID)
                .addValue("name",FORMAT)
                .addValue("game_id",ID)
                .addValue("region", REGION)
                .addValue("elo", ELO)
                .addValue("start_date", START_DATE)
                .addValue("end_date", END_DATE)
                .addValue("format", FORMAT)
                .addValue("structure", STRUCTURE)
                .addValue("max_participants", MAX_PARTICIPANTS)
                .addValue("image_id", ID)
                .addValue("open_inscriptions", true)
                .addValue("is_finished", false)
                .addValue("tournament_started", false)
                .addValue("format_id", ID);
        jdbcInsert.execute(values);
        List<Tournament> tournaments = tournamentJdbcDao.searchByName(NAME);

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(2, tournaments.size());
        Assert.assertTrue(tournaments.stream().allMatch(t -> t.getName().toLowerCase().contains(NAME.toLowerCase())));
    }

    @Test
    public void testSearchByNameNone(){
        List<Tournament> tournaments = tournamentJdbcDao.searchByName("NonExistent");

        Assert.assertNotNull(tournaments);
        Assert.assertTrue(tournaments.isEmpty());
    }

    @Test
    public void testGetUnfilteredTournamentPages(){
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tournament");
        SqlParameterSource values;
        for (int i = 1; i <= 9; i++) {
            values = new MapSqlParameterSource()
                    .addValue("id",OTHER_ID+i*2)
                    .addValue("creator_id",OTHER_ID)
                    .addValue("name",NAME)
                    .addValue("game_id",ID)
                    .addValue("region", REGION)
                    .addValue("elo", ELO)
                    .addValue("start_date", START_DATE.plusDays(i))
                    .addValue("end_date", END_DATE.plusDays(i))
                    .addValue("format", FORMAT)
                    .addValue("structure", STRUCTURE)
                    .addValue("max_participants", MAX_PARTICIPANTS)
                    .addValue("image_id", ID)
                    .addValue("open_inscriptions", true)
                    .addValue("is_finished", false)
                    .addValue("tournament_started", false)
                    .addValue("format_id", ID);
            jdbcInsert.execute(values);
        }
        for (int i = 0; i <= 9; i++) {
            values = new MapSqlParameterSource()
                    .addValue("id",OTHER_ID+i*2+1)
                    .addValue("creator_id",OTHER_ID)
                    .addValue("name",NAME)
                    .addValue("game_id",OTHER_ID)
                    .addValue("region", REGION)
                    .addValue("elo", ELO)
                    .addValue("start_date", START_DATE.plusDays(i))
                    .addValue("end_date", END_DATE.plusDays(i))
                    .addValue("format", FORMAT)
                    .addValue("structure", STRUCTURE)
                    .addValue("max_participants", MAX_PARTICIPANTS)
                    .addValue("image_id", ID)
                    .addValue("open_inscriptions", true)
                    .addValue("is_finished", false)
                    .addValue("tournament_started", false)
                    .addValue("format_id", ID);
            jdbcInsert.execute(values);
        }
        Map<Long, List<Tournament>> pages = tournamentJdbcDao.getUnfilteredTournamentPages(0L);

        Assert.assertEquals(2, pages.size());
        for (List<Tournament> list : pages.values()) {
            Assert.assertEquals(9, list.size());
            Assert.assertTrue(list.stream().allMatch(Tournament::getOpenInscriptions));
        }
    }

    @Test
    public void testUpdateAllStartDates(){
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tournament");
        SqlParameterSource values = new MapSqlParameterSource()
                .addValue("id",ID)
                .addValue("creator_id",OTHER_ID)
                .addValue("name",NAME+ " 2")
                .addValue("game_id",OTHER_ID)
                .addValue("region", REGION)
                .addValue("elo", ELO)
                .addValue("start_date", START_DATE)
                .addValue("end_date", END_DATE)
                .addValue("format", FORMAT)
                .addValue("structure", STRUCTURE)
                .addValue("max_participants", MAX_PARTICIPANTS)
                .addValue("image_id", ID)
                .addValue("open_inscriptions", true)
                .addValue("is_finished", false)
                .addValue("tournament_started", false)
                .addValue("format_id", ID);
        jdbcInsert.execute(values);
        values = new MapSqlParameterSource()
                .addValue("id",OTHER_ID+1)
                .addValue("creator_id",ID)
                .addValue("name",FORMAT)
                .addValue("game_id",ID)
                .addValue("region", REGION)
                .addValue("elo", ELO)
                .addValue("start_date", LocalDate.now().plusDays(1))
                .addValue("end_date", END_DATE)
                .addValue("format", FORMAT)
                .addValue("structure", STRUCTURE)
                .addValue("max_participants", MAX_PARTICIPANTS)
                .addValue("image_id", ID)
                .addValue("open_inscriptions", true)
                .addValue("is_finished", false)
                .addValue("tournament_started", false)
                .addValue("format_id", ID);
        jdbcInsert.execute(values);
        tournamentJdbcDao.updateAllStartDates();

        List<LocalDate> startDates = jdbcTemplate.queryForList("SELECT start_date FROM tournament ORDER BY name", LocalDate.class);
        Assert.assertEquals(LocalDate.now(), startDates.get(0));
        Assert.assertNotEquals(START_DATE,startDates.get(0));
        Assert.assertEquals(LocalDate.now(), startDates.get(1));
        Assert.assertNotEquals(START_DATE,startDates.get(1));
        Assert.assertEquals(LocalDate.now().plusDays(1), startDates.get(2));
    }

    @Test
    public void testUpdateAllEndDates(){
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tournament");
        SqlParameterSource values = new MapSqlParameterSource()
                .addValue("id",ID)
                .addValue("creator_id",OTHER_ID)
                .addValue("name",NAME+ " 2")
                .addValue("game_id",OTHER_ID)
                .addValue("region", REGION)
                .addValue("elo", ELO)
                .addValue("start_date", START_DATE)
                .addValue("end_date", LocalDate.now().minusDays(1))
                .addValue("format", FORMAT)
                .addValue("structure", STRUCTURE)
                .addValue("max_participants", MAX_PARTICIPANTS)
                .addValue("image_id", ID)
                .addValue("open_inscriptions", true)
                .addValue("is_finished", false)
                .addValue("tournament_started", false)
                .addValue("format_id", ID);
        jdbcInsert.execute(values);
        values = new MapSqlParameterSource()
                .addValue("id",OTHER_ID+1)
                .addValue("creator_id",ID)
                .addValue("name",FORMAT)
                .addValue("game_id",ID)
                .addValue("region", REGION)
                .addValue("elo", ELO)
                .addValue("start_date", START_DATE)
                .addValue("end_date", LocalDate.now().minusDays(1))
                .addValue("format", FORMAT)
                .addValue("structure", STRUCTURE)
                .addValue("max_participants", MAX_PARTICIPANTS)
                .addValue("image_id", ID)
                .addValue("open_inscriptions", true)
                .addValue("is_finished", false)
                .addValue("tournament_started", false)
                .addValue("format_id", ID);
        jdbcInsert.execute(values);
        tournamentJdbcDao.updateAllEndDates();

        List<LocalDate> endDates = jdbcTemplate.queryForList("SELECT end_date FROM tournament ORDER BY name", LocalDate.class);
        Assert.assertEquals(LocalDate.now(), endDates.get(1));
        Assert.assertNotEquals(LocalDate.now().minusDays(1),endDates.get(1));
        Assert.assertEquals(LocalDate.now(), endDates.get(2));
        Assert.assertNotEquals(LocalDate.now().minusDays(1),endDates.get(2));
        Assert.assertEquals(END_DATE, endDates.get(0));
    }

    @Test
    public void testIsNotClosed(){
        boolean closed = tournamentJdbcDao.isClosed(OTHER_ID);

        Assert.assertFalse(closed);
    }

    @Test
    public void testIsClosed(){
        jdbcTemplate.update("update tournament set open_inscriptions = false");
        boolean closed = tournamentJdbcDao.isClosed(OTHER_ID);

        Assert.assertTrue(closed);
    }

    @Test
    public void testFindUserActiveTournaments(){
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tournament");
        SqlParameterSource values = new MapSqlParameterSource()
                .addValue("id",OTHER_ID+1)
                .addValue("creator_id",ID)
                .addValue("name",NAME)
                .addValue("game_id",ID)
                .addValue("region", REGION)
                .addValue("elo", ELO)
                .addValue("start_date", START_DATE)
                .addValue("end_date", END_DATE)
                .addValue("format", FORMAT)
                .addValue("structure", STRUCTURE)
                .addValue("max_participants", MAX_PARTICIPANTS)
                .addValue("image_id", ID)
                .addValue("open_inscriptions", true)
                .addValue("is_finished", true)
                .addValue("tournament_started", false)
                .addValue("format_id", ID);
        jdbcInsert.execute(values);
        SimpleJdbcInsert participantInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("participant");
        participantInsert.execute(Map.of("id",ID,"user_id", ID, "tournament_id", OTHER_ID, "points", 0));
        participantInsert.execute(Map.of("id",ID+1,"user_id", ID, "tournament_id", OTHER_ID+1, "points", 0));
        List<Tournament> list = tournamentJdbcDao.findUserActiveTournaments(ID, 1L);

        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(1,list.size());
        Assert.assertEquals(OTHER_ID,list.get(0).getId());
    }

    @Test
    public void testFindUserPastTournaments(){
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tournament");
        SqlParameterSource values = new MapSqlParameterSource()
                .addValue("id",OTHER_ID+1)
                .addValue("creator_id",ID)
                .addValue("name",NAME)
                .addValue("game_id",ID)
                .addValue("region", REGION)
                .addValue("elo", ELO)
                .addValue("start_date", START_DATE)
                .addValue("end_date", END_DATE)
                .addValue("format", FORMAT)
                .addValue("structure", STRUCTURE)
                .addValue("max_participants", MAX_PARTICIPANTS)
                .addValue("image_id", ID)
                .addValue("open_inscriptions", true)
                .addValue("is_finished", true)
                .addValue("tournament_started", false)
                .addValue("format_id", ID);
        jdbcInsert.execute(values);
        SimpleJdbcInsert participantInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("participant");
        participantInsert.execute(Map.of("id",ID,"user_id", ID, "tournament_id", OTHER_ID, "points", 0));
        participantInsert.execute(Map.of("id",ID+1,"user_id", ID, "tournament_id", OTHER_ID+1, "points", 0));
        List<Tournament> list = tournamentJdbcDao.findUserPastTournaments(ID, 1L);

        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(1,list.size());
        Assert.assertEquals(OTHER_ID+1,list.get(0).getId().longValue());
    }
}
