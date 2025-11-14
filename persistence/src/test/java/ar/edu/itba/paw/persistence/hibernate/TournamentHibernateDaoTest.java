package ar.edu.itba.paw.persistence.hibernate;

import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.persistence.Hibernate.TournamentHibernateDao;
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
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class TournamentHibernateDaoTest {
    @PersistenceContext
    private EntityManager em;
    
    @Autowired
    private DataSource ds;

    @Autowired
    private TournamentHibernateDao tournamentHibernateDao;

    private JdbcTemplate jdbcTemplate;

    private static final Genre GENRE = Genre.MOBA;
    private static final String NAME = "Jerma Rumble";
    private static final Elo ELO = Elo.LOW;
    private static final Region REGION = Region.LAS;
    private static final Structure STRUCTURE = Structure.LEAGUE;
    private static final LocalDate START_DATE = LocalDate.of(2026, 2, 21);
    private static final LocalDate END_DATE = LocalDate.of(2027, 2, 21);
    private static final String FORMAT = "some format";
    private static final Long ID = 100L;
    private static final Long OTHER_ID = 2L;
    private static final Integer MAX_PARTICIPANTS = 4;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate(){
        Tournament tournament = tournamentHibernateDao.create(
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
                ID,
                ID);
        em.flush();

        Assert.assertNotNull(tournament);
        Assert.assertEquals(ID.longValue(),tournament.getCreator().getId());
        Assert.assertEquals(NAME, tournament.getName());
        Assert.assertEquals(ID,tournament.getGameId());
        Assert.assertEquals(REGION, tournament.getRegion());
        Assert.assertEquals(ELO, tournament.getElo());
        Assert.assertEquals(START_DATE, tournament.getStartDate());
        Assert.assertEquals(END_DATE, tournament.getEndDate());
        Assert.assertEquals(FORMAT, tournament.getFormat());
        Assert.assertEquals(STRUCTURE, tournament.getStructure());
        Assert.assertEquals(MAX_PARTICIPANTS, tournament.getMaxParticipants());
        Assert.assertEquals(ID,tournament.getImageId());
        Assert.assertTrue(tournament.getOpenInscriptions());
        Assert.assertFalse(tournament.getFinished());
        Assert.assertEquals(ID,tournament.getFormatId());
        Assert.assertEquals(ID,tournament.getRules().getId());
        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "id = " + tournament.getId() + " and name = '" + NAME + "' and creator_id = " + ID
         + " and game_id = creator_id and image_id = game_id and format_id = game_id and rules_id = game_id and "
        + "format = '" + FORMAT + "' and region = '" + REGION + "' and elo = '" + ELO + "' and structure = '"
        + STRUCTURE + "' and max_participants = " + MAX_PARTICIPANTS + " and is_finished = false and " +
        " open_inscriptions = true and start_date = '" + START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE)
        + "' and end_date = '" + END_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"));
    }

    @Test
    public void testFindById(){
        Optional<Tournament> found = tournamentHibernateDao.findById(ID);

        Assert.assertNotNull(found);
        Assert.assertTrue(found.isPresent());
        Tournament tournament = found.get();
        Assert.assertEquals(ID, tournament.getId());
        Assert.assertEquals(ID.longValue()  , tournament.getCreator().getId());
        Assert.assertEquals(NAME, tournament.getName());
        Assert.assertEquals(ID, tournament.getGame().getId());
        Assert.assertEquals(REGION, tournament.getRegion());
        Assert.assertEquals(ELO, tournament.getElo());
        Assert.assertEquals(START_DATE, tournament.getStartDate());
        Assert.assertEquals(END_DATE, tournament.getEndDate());
        Assert.assertEquals(FORMAT, tournament.getFormat());
        Assert.assertEquals(STRUCTURE, tournament.getStructure());
        Assert.assertEquals(MAX_PARTICIPANTS, tournament.getMaxParticipants());
        Assert.assertEquals(ID, tournament.getImageId());
        Assert.assertTrue(tournament.getOpenInscriptions());
        Assert.assertFalse(tournament.getFinished());
        Assert.assertEquals(ID, tournament.getFormatId());
    }

    @Test
    public void testFindByIdNotFound(){
        Optional<Tournament> found = tournamentHibernateDao.findById(0L);

        Assert.assertFalse(found.isPresent());
    }

//    @Test
//    public void testFindGameTournaments(){
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("tournament");
//        SqlParameterSource values = new MapSqlParameterSource()
//                .addValue("id",ID)
//                .addValue("creator_id",ID)
//                .addValue("name",NAME)
//                .addValue("game_id",OTHER_ID)
//                .addValue("region", REGION)
//                .addValue("elo", ELO)
//                .addValue("startDate", START_DATE)
//                .addValue("endDate", END_DATE)
//                .addValue("format", FORMAT)
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", ID);
//        jdbcInsert.execute(values);
//        values = new MapSqlParameterSource()
//                .addValue("id",OTHER_ID+1)
//                .addValue("creator_id",ID)
//                .addValue("name",NAME)
//                .addValue("game_id",ID)
//                .addValue("region", REGION)
//                .addValue("elo", ELO)
//                .addValue("startDate", START_DATE)
//                .addValue("endDate", END_DATE)
//                .addValue("format", FORMAT)
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", ID);
//        jdbcInsert.execute(values);
//        List<Tournament> tournaments = tournamentHibernateDao.findGameTournaments(ID);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertEquals(2, tournaments.size());
//        Assert.assertTrue(tournaments.stream().allMatch(t -> t.getGame_id().equals(ID)));
//    }
//
//    @Test
//    public void testFindGameTournamentsNone(){
//        List<Tournament> tournaments = tournamentHibernateDao.findGameTournaments(-1L);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertTrue(tournaments.isEmpty());
//    }
//
//    @Test
//    public void testHasNotTournamentStarted(){
//        Boolean started = tournamentHibernateDao.isTournamentStarted(OTHER_ID);
//
//        Assert.assertFalse(started);
//    }
//
//    @Test
//    public void testHasTournamentStarted(){
//        jdbcTemplate.update("update tournament set tournament_started = true where id = "+OTHER_ID);
//        Boolean started = tournamentHibernateDao.isTournamentStarted(OTHER_ID);
//
//        Assert.assertTrue(started);
//    }
//
//    @Test
//    public void testHasNonexistentTournamentStarted(){
//        Boolean started = tournamentHibernateDao.isTournamentStarted((long)-1);
//
//        Assert.assertFalse(started);
//    }
//
//    @Test
//    public void testSetIsGroupStage(){
//        tournamentHibernateDao.setIsGroupStage(OTHER_ID, true);
//
//        Boolean isGroupStage = jdbcTemplate.queryForObject("SELECT is_group_stage FROM tournament WHERE id = ?", Boolean.class, OTHER_ID);
//        Assert.assertTrue(isGroupStage);
//    }
//
//    @Test
//    public void testGetTournamentStructure(){
//        Structure structure = tournamentHibernateDao.getTournamentStructure(OTHER_ID);
//
//        Assert.assertEquals(STRUCTURE, structure);
//    }
//
//    @Test
//    public void testGetTournamentStructureNotFound(){
//        Structure structure = tournamentHibernateDao.getTournamentStructure(-1L);
//
//        Assert.assertNull(structure);
//    }
//
//    @Test
//    public void testFindByCreatorNone(){
//        List<Tournament> tournaments = tournamentHibernateDao.findByCreator(-1L, 1L);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertTrue(tournaments.isEmpty());
//    }
//
//    @Test
//    public void testSetFinished(){
//        tournamentHibernateDao.setFinished(OTHER_ID);
//
//        Boolean isFinished = jdbcTemplate.queryForObject("SELECT is_finished FROM tournament WHERE id = ?", Boolean.class, OTHER_ID);
//        Assert.assertTrue(isFinished);
//        LocalDate endDate = jdbcTemplate.queryForObject("SELECT endDate FROM tournament WHERE id = ?", LocalDate.class, OTHER_ID);
//        Assert.assertEquals(LocalDate.now(), endDate);
//    }
//
//    @Test
//    public void testCloseInscriptions(){
//        tournamentHibernateDao.closeInscriptions(OTHER_ID);
//
//        Boolean openInscriptions = jdbcTemplate.queryForObject("SELECT open_inscriptions FROM tournament WHERE id = ?", Boolean.class, OTHER_ID);
//        Assert.assertFalse(openInscriptions);
//    }
//
//    @Test
//    public void testSetTournamentWinner(){
//        tournamentHibernateDao.setTournamentWinner(OTHER_ID, ID);
//
//        Long tournamentWinner = jdbcTemplate.queryForObject("SELECT tournament_winner FROM tournament WHERE id = ?", Long.class, OTHER_ID);
//        Assert.assertEquals(ID, tournamentWinner);
//    }
//
//    @Test
//    public void testStartTournament(){
//        tournamentHibernateDao.startTournament(OTHER_ID);
//
//        Boolean started = jdbcTemplate.queryForObject("SELECT tournament_started FROM tournament WHERE id = ?", Boolean.class, OTHER_ID);
//        Assert.assertTrue(started);
//    }
//
//    @Test
//    public void testGetIsGroupStage(){
//        jdbcTemplate.update("update tournament set is_group_stage = true");
//        Boolean isGroupStage = tournamentHibernateDao.getIsGroupStage(OTHER_ID);
//
//        Assert.assertTrue(isGroupStage);
//    }
//
//    @Test
//    public void testGetTournamentParticipantsCount(){
//        SimpleJdbcInsert participantInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("participant");
//        participantInsert.execute(Map.of("id",ID,"user_id", ID, "tournament_id", OTHER_ID, "points", 0));
//        participantInsert.execute(Map.of("id",OTHER_ID,"user_id", OTHER_ID, "tournament_id", OTHER_ID, "points", 0));
//        int count = tournamentHibernateDao.getTournamentParticipantsCount(OTHER_ID);
//
//        Assert.assertEquals(2, count);
//    }
//
//    @Test
//    public void testUpdateTournamentInfo(){
//        String newName = "Updated Name";
//        LocalDate newStartDate = LocalDate.of(2025, 9, 30);
//        LocalDate newEndDate = LocalDate.of(2026, 9, 30);
//        Integer newMaxParticipants = 16;
//        tournamentHibernateDao.updateTournamentInfo(OTHER_ID, newName, newStartDate, newEndDate, newMaxParticipants);
//        Tournament updated = jdbcTemplate.queryForObject("SELECT * FROM tournament WHERE id = ?", ROW_MAPPER, OTHER_ID);
//
//        Assert.assertNotNull(updated);
//        Assert.assertEquals(newName, updated.getName());
//        Assert.assertNotEquals(NAME,updated.getName());
//        Assert.assertEquals(newStartDate, updated.getStartDate());
//        Assert.assertNotEquals(START_DATE,updated.getStartDate());
//        Assert.assertEquals(newEndDate, updated.getEndDate());
//        Assert.assertNotEquals(END_DATE,updated.getEndDate());
//        Assert.assertEquals(newMaxParticipants, updated.getMaxParticipants());
//        Assert.assertNotEquals(MAX_PARTICIPANTS,updated.getMaxParticipants());
//    }
//
//    @Test
//    public void testGetPageAmount(){
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//            .withTableName("tournament");
//        for (int i = 0; i < 4; i++) {
//            SqlParameterSource values = new MapSqlParameterSource()
//                    .addValue("id",OTHER_ID+1+i)
//                    .addValue("creator_id",OTHER_ID)
//                    .addValue("name",NAME+i)
//                    .addValue("game_id",ID)
//                    .addValue("region", REGION)
//                    .addValue("elo", ELO)
//                    .addValue("startDate", START_DATE)
//                    .addValue("endDate", END_DATE)
//                    .addValue("format", FORMAT)
//                    .addValue("structure", STRUCTURE)
//                    .addValue("maxParticipants", MAX_PARTICIPANTS)
//                    .addValue("image_id", ID)
//                    .addValue("open_inscriptions", true)
//                    .addValue("is_finished", false)
//                    .addValue("tournament_started", false)
//                    .addValue("format_id", ID);
//            jdbcInsert.execute(values);
//        }
//        TournamentFilter filter = new TournamentFilter();
//        filter.setGame_id(ID);
//        int pageSize = 2;
//
//        Integer pageAmount = tournamentHibernateDao.getPageAmount(pageSize, filter);
//
//        Assert.assertEquals(3, pageAmount.intValue());
//    }
//
//    @Test
//    public void testSearchByName(){
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("tournament");
//        SqlParameterSource values = new MapSqlParameterSource()
//                .addValue("id",ID)
//                .addValue("creator_id",OTHER_ID)
//                .addValue("name",NAME+ " 2")
//                .addValue("game_id",OTHER_ID)
//                .addValue("region", REGION)
//                .addValue("elo", ELO)
//                .addValue("startDate", START_DATE)
//                .addValue("endDate", END_DATE)
//                .addValue("format", FORMAT)
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", ID);
//        jdbcInsert.execute(values);
//        values = new MapSqlParameterSource()
//                .addValue("id",OTHER_ID+1)
//                .addValue("creator_id",ID)
//                .addValue("name",FORMAT)
//                .addValue("game_id",ID)
//                .addValue("region", REGION)
//                .addValue("elo", ELO)
//                .addValue("startDate", START_DATE)
//                .addValue("endDate", END_DATE)
//                .addValue("format", FORMAT)
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", ID);
//        jdbcInsert.execute(values);
//        List<Tournament> tournaments = tournamentHibernateDao.searchByName(NAME);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertEquals(2, tournaments.size());
//        Assert.assertTrue(tournaments.stream().allMatch(t -> t.getName().toLowerCase().contains(NAME.toLowerCase())));
//    }
//
//    @Test
//    public void testSearchByNameNone(){
//        List<Tournament> tournaments = tournamentHibernateDao.searchByName("NonExistent");
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertTrue(tournaments.isEmpty());
//    }
//
//    @Test
//    public void testGetUnfilteredTournamentPages(){
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("tournament");
//        SqlParameterSource values;
//        for (int i = 1; i <= 9; i++) {
//            values = new MapSqlParameterSource()
//                    .addValue("id",OTHER_ID+i*2)
//                    .addValue("creator_id",OTHER_ID)
//                    .addValue("name",NAME)
//                    .addValue("game_id",ID)
//                    .addValue("region", REGION)
//                    .addValue("elo", ELO)
//                    .addValue("startDate", START_DATE.plusDays(i))
//                    .addValue("endDate", END_DATE.plusDays(i))
//                    .addValue("format", FORMAT)
//                    .addValue("structure", STRUCTURE)
//                    .addValue("maxParticipants", MAX_PARTICIPANTS)
//                    .addValue("image_id", ID)
//                    .addValue("open_inscriptions", true)
//                    .addValue("is_finished", false)
//                    .addValue("tournament_started", false)
//                    .addValue("format_id", ID);
//            jdbcInsert.execute(values);
//        }
//        for (int i = 0; i <= 9; i++) {
//            values = new MapSqlParameterSource()
//                    .addValue("id",OTHER_ID+i*2+1)
//                    .addValue("creator_id",OTHER_ID)
//                    .addValue("name",NAME)
//                    .addValue("game_id",OTHER_ID)
//                    .addValue("region", REGION)
//                    .addValue("elo", ELO)
//                    .addValue("startDate", START_DATE.plusDays(i))
//                    .addValue("endDate", END_DATE.plusDays(i))
//                    .addValue("format", FORMAT)
//                    .addValue("structure", STRUCTURE)
//                    .addValue("maxParticipants", MAX_PARTICIPANTS)
//                    .addValue("image_id", ID)
//                    .addValue("open_inscriptions", true)
//                    .addValue("is_finished", false)
//                    .addValue("tournament_started", false)
//                    .addValue("format_id", ID);
//            jdbcInsert.execute(values);
//        }
//        Map<Long, List<Tournament>> pages = tournamentHibernateDao.getUnfilteredTournamentPages(0L);
//
//        Assert.assertEquals(2, pages.size());
//        for (List<Tournament> list : pages.values()) {
//            Assert.assertEquals(9, list.size());
//            Assert.assertTrue(list.stream().allMatch(Tournament::getOpenInscriptions));
//        }
//    }
//
//    @Test
//    public void testUpdateAllStartDates(){
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("tournament");
//        SqlParameterSource values = new MapSqlParameterSource()
//                .addValue("id",ID)
//                .addValue("creator_id",OTHER_ID)
//                .addValue("name",NAME+ " 2")
//                .addValue("game_id",OTHER_ID)
//                .addValue("region", REGION)
//                .addValue("elo", ELO)
//                .addValue("startDate", START_DATE)
//                .addValue("endDate", END_DATE)
//                .addValue("format", FORMAT)
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", ID);
//        jdbcInsert.execute(values);
//        values = new MapSqlParameterSource()
//                .addValue("id",OTHER_ID+1)
//                .addValue("creator_id",ID)
//                .addValue("name",FORMAT)
//                .addValue("game_id",ID)
//                .addValue("region", REGION)
//                .addValue("elo", ELO)
//                .addValue("startDate", LocalDate.now().plusDays(1))
//                .addValue("endDate", END_DATE)
//                .addValue("format", FORMAT)
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", ID);
//        jdbcInsert.execute(values);
//        tournamentHibernateDao.updateAllStartDates();
//
//        List<LocalDate> startDates = jdbcTemplate.queryForList("SELECT startDate FROM tournament ORDER BY name", LocalDate.class);
//        Assert.assertEquals(LocalDate.now(), startDates.get(0));
//        Assert.assertNotEquals(START_DATE,startDates.get(0));
//        Assert.assertEquals(LocalDate.now(), startDates.get(1));
//        Assert.assertNotEquals(START_DATE,startDates.get(1));
//        Assert.assertEquals(LocalDate.now().plusDays(1), startDates.get(2));
//    }
//
//    @Test
//    public void testUpdateAllEndDates(){
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("tournament");
//        SqlParameterSource values = new MapSqlParameterSource()
//                .addValue("id",ID)
//                .addValue("creator_id",OTHER_ID)
//                .addValue("name",NAME+ " 2")
//                .addValue("game_id",OTHER_ID)
//                .addValue("region", REGION)
//                .addValue("elo", ELO)
//                .addValue("startDate", START_DATE)
//                .addValue("end_date", LocalDate.now().minusDays(1))
//                .addValue("format", FORMAT)
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", ID);
//        jdbcInsert.execute(values);
//        values = new MapSqlParameterSource()
//                .addValue("id",OTHER_ID+1)
//                .addValue("creator_id",ID)
//                .addValue("name",FORMAT)
//                .addValue("game_id",ID)
//                .addValue("region", REGION)
//                .addValue("elo", ELO)
//                .addValue("startDate", START_DATE)
//                .addValue("endDate", LocalDate.now().minusDays(1))
//                .addValue("format", FORMAT)
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", ID);
//        jdbcInsert.execute(values);
//        tournamentHibernateDao.updateAllEndDates();
//
//        List<LocalDate> endDates = jdbcTemplate.queryForList("SELECT endDate FROM tournament ORDER BY name", LocalDate.class);
//        Assert.assertEquals(LocalDate.now(), endDates.get(1));
//        Assert.assertNotEquals(LocalDate.now().minusDays(1),endDates.get(1));
//        Assert.assertEquals(LocalDate.now(), endDates.get(2));
//        Assert.assertNotEquals(LocalDate.now().minusDays(1),endDates.get(2));
//        Assert.assertEquals(END_DATE, endDates.get(0));
//    }
//
//    @Test
//    public void testIsNotClosed(){
//        boolean closed = tournamentHibernateDao.isClosed(OTHER_ID);
//
//        Assert.assertFalse(closed);
//    }
//
//    @Test
//    public void testIsClosed(){
//        jdbcTemplate.update("update tournament set open_inscriptions = false");
//        boolean closed = tournamentHibernateDao.isClosed(OTHER_ID);
//
//        Assert.assertTrue(closed);
//    }
//
//    @Test
//    public void testFindTournamentsWithNameAndFormatFilter() {
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("tournament");
//        SqlParameterSource values = new MapSqlParameterSource()
//                .addValue("id", OTHER_ID + 1)
//                .addValue("creator_id", ID)
//                .addValue("name", "Different Tournament")
//                .addValue("game_id", ID)
//                .addValue("region", REGION)
//                .addValue("elo", ELO)
//                .addValue("startDate", START_DATE)
//                .addValue("endDate", END_DATE)
//                .addValue("format", FORMAT)
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", ID);
//        jdbcInsert.execute(values);
//        values = new MapSqlParameterSource()
//                .addValue("id", ID)
//                .addValue("creator_id", ID)
//                .addValue("name", NAME + " 2")
//                .addValue("game_id", ID)
//                .addValue("region", REGION)
//                .addValue("elo", ELO)
//                .addValue("startDate", START_DATE)
//                .addValue("endDate", END_DATE)
//                .addValue("format", "otro")
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", ID);
//        jdbcInsert.execute(values);
//
//        TournamentFilter filter = new TournamentFilter();
//        filter.setName(NAME);
//        filter.setFormat(FORMAT);
//        List<Tournament> tournaments = tournamentHibernateDao.findTournaments(filter, 0L);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertEquals(1, tournaments.size());
//        Assert.assertEquals(NAME, tournaments.get(0).getName());
//        Assert.assertEquals(FORMAT, tournaments.get(0).getFormat());
//    }
//
//    @Test
//    public void testFindTournamentsWithGameIdAndEloFilter() {
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("tournament");
//        SqlParameterSource values = new MapSqlParameterSource()
//                .addValue("id", OTHER_ID + 1)
//                .addValue("creator_id", ID)
//                .addValue("name", NAME)
//                .addValue("game_id", OTHER_ID)
//                .addValue("region", REGION)
//                .addValue("elo", ELO)
//                .addValue("startDate", START_DATE)
//                .addValue("endDate", END_DATE)
//                .addValue("format", FORMAT)
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", ID);
//        jdbcInsert.execute(values);
//        values = new MapSqlParameterSource()
//                .addValue("id", ID)
//                .addValue("creator_id", ID)
//                .addValue("name", NAME)
//                .addValue("game_id", ID)
//                .addValue("region", REGION)
//                .addValue("elo", Elo.HIGH)
//                .addValue("startDate", START_DATE)
//                .addValue("endDate", END_DATE)
//                .addValue("format", FORMAT)
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", ID);
//        jdbcInsert.execute(values);
//
//        TournamentFilter filter = new TournamentFilter();
//        filter.setGame_id(ID);
//        filter.setElo(ELO);
//        List<Tournament> tournaments = tournamentHibernateDao.findTournaments(filter, 0L);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertEquals(1, tournaments.size());
//        Assert.assertEquals(ID, tournaments.get(0).getGame_id());
//        Assert.assertEquals(ELO, tournaments.get(0).getElo());
//    }
//
//    @Test
//    public void testFindTournamentsWithRegionAndStructureFilter() {
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("tournament");
//        SqlParameterSource values = new MapSqlParameterSource()
//                .addValue("id", OTHER_ID + 1)
//                .addValue("creator_id", ID)
//                .addValue("name", NAME)
//                .addValue("game_id", ID)
//                .addValue("region", Region.LAN)
//                .addValue("elo", ELO)
//                .addValue("startDate", START_DATE)
//                .addValue("endDate", END_DATE)
//                .addValue("format", FORMAT)
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", ID);
//        jdbcInsert.execute(values);
//        values = new MapSqlParameterSource()
//                .addValue("id", ID)
//                .addValue("creator_id", ID)
//                .addValue("name", NAME)
//                .addValue("game_id", ID)
//                .addValue("region", REGION)
//                .addValue("elo", ELO)
//                .addValue("startDate", START_DATE)
//                .addValue("endDate", END_DATE)
//                .addValue("format", FORMAT)
//                .addValue("structure", Structure.HYBRID)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", ID);
//        jdbcInsert.execute(values);
//
//        TournamentFilter filter = new TournamentFilter();
//        filter.setRegion(REGION);
//        filter.setStructure(STRUCTURE);
//        List<Tournament> tournaments = tournamentHibernateDao.findTournaments(filter, 0L);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertEquals(1, tournaments.size());
//        Assert.assertEquals(REGION, tournaments.get(0).getRegion());
//        Assert.assertEquals(STRUCTURE, tournaments.get(0).getStructure());
//    }
//
//    @Test
//    public void testFindTournamentsWithGenreAndPlayersPerTeamFilter() {
//        SimpleJdbcInsert formatInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("game_format");
//        formatInsert.execute(Map.of("id", OTHER_ID, "name", FORMAT, "players_per_team", 4, "game_id", OTHER_ID+1));
//        formatInsert.execute(Map.of("id", OTHER_ID+1, "name", FORMAT, "players_per_team", 6, "game_id", OTHER_ID+1));
//        SimpleJdbcInsert gameInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("game");
//        gameInsert.execute(Map.of("id",OTHER_ID+1,"name",NAME+"b","genre",Genre.FPS,"image_id",ID));
//
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("tournament");
//        SqlParameterSource values = new MapSqlParameterSource()
//                .addValue("id", OTHER_ID + 1)
//                .addValue("creator_id", ID)
//                .addValue("name", NAME)
//                .addValue("game_id", OTHER_ID+1)
//                .addValue("region", REGION)
//                .addValue("elo", ELO)
//                .addValue("startDate", START_DATE)
//                .addValue("endDate", END_DATE)
//                .addValue("format", FORMAT)
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", OTHER_ID);
//        jdbcInsert.execute(values);
//        values = new MapSqlParameterSource()
//                .addValue("id", ID)
//                .addValue("creator_id", ID)
//                .addValue("name", NAME)
//                .addValue("game_id", OTHER_ID+1)
//                .addValue("region", REGION)
//                .addValue("elo", ELO)
//                .addValue("startDate", START_DATE)
//                .addValue("endDate", END_DATE)
//                .addValue("format", FORMAT)
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", OTHER_ID+1);
//        jdbcInsert.execute(values);
//
//        TournamentFilter filter = new TournamentFilter();
//        filter.setGenre(Genre.FPS);
//        filter.setPlayersPerTeam(6);
//        List<Tournament> tournaments = tournamentHibernateDao.findTournaments(filter, 0L);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertTrue(tournaments.stream().allMatch((tournament -> Objects.equals(tournament.getGame_id(), OTHER_ID+1))));
//        Assert.assertTrue(tournaments.stream().allMatch((tournament -> Objects.equals(tournament.getFormat_id(), OTHER_ID+1))));
//        Assert.assertEquals(1, tournaments.size());
//    }
//
//    @Test
//    public void testFindTournamentsWithDateFilters() {
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("tournament");
//        SqlParameterSource values = new MapSqlParameterSource()
//                .addValue("id", OTHER_ID + 1)
//                .addValue("creator_id", ID)
//                .addValue("name", NAME)
//                .addValue("game_id", ID)
//                .addValue("region", REGION)
//                .addValue("elo", ELO)
//                .addValue("startDate", START_DATE.plusDays(1))
//                .addValue("endDate", END_DATE)
//                .addValue("format", FORMAT)
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", ID);
//        jdbcInsert.execute(values);
//        values = new MapSqlParameterSource()
//                .addValue("id", ID)
//                .addValue("creator_id", ID)
//                .addValue("name", NAME)
//                .addValue("game_id", ID)
//                .addValue("region", REGION)
//                .addValue("elo", ELO)
//                .addValue("startDate", START_DATE)
//                .addValue("endDate", END_DATE.plusDays(1))
//                .addValue("format", FORMAT)
//                .addValue("structure", STRUCTURE)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", ID);
//        jdbcInsert.execute(values);
//
//        TournamentFilter filter = new TournamentFilter();
//        filter.setStartDate(START_DATE);
//        filter.setEndDate(END_DATE);
//        List<Tournament> tournaments = tournamentHibernateDao.findTournaments(filter, 0L);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertEquals(1, tournaments.size());
//        Assert.assertEquals(START_DATE, tournaments.get(0).getStartDate());
//    }
//
//    @Test
//    public void testFindTournamentsPagination() {
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("tournament");
//        for (int i = 1; i <= 10; i++) {
//            SqlParameterSource values = new MapSqlParameterSource()
//                    .addValue("id", OTHER_ID + i)
//                    .addValue("creator_id", ID)
//                    .addValue("name", NAME + i)
//                    .addValue("game_id", ID)
//                    .addValue("region", REGION)
//                    .addValue("elo", ELO)
//                    .addValue("startDate", START_DATE)
//                    .addValue("endDate", END_DATE)
//                    .addValue("format", FORMAT)
//                    .addValue("structure", STRUCTURE)
//                    .addValue("maxParticipants", MAX_PARTICIPANTS)
//                    .addValue("image_id", ID)
//                    .addValue("open_inscriptions", true)
//                    .addValue("is_finished", false)
//                    .addValue("tournament_started", false)
//                    .addValue("format_id", ID);
//            jdbcInsert.execute(values);
//        }
//
//        TournamentFilter filter = new TournamentFilter();
//        filter.setGame_id(ID);
//        List<Tournament> firstPage = tournamentHibernateDao.findTournaments(filter, 0L);
//        List<Tournament> secondPage = tournamentHibernateDao.findTournaments(filter, 1L);
//
//        Assert.assertNotNull(firstPage);
//        Assert.assertNotNull(secondPage);
//        Assert.assertEquals(9, firstPage.size());
//        Assert.assertEquals(2, secondPage.size());
//    }
//
//    @Test
//    public void testFindTournamentsNoResults() {
//        TournamentFilter filter = new TournamentFilter();
//        filter.setName("NonExistent");
//        List<Tournament> tournaments = tournamentHibernateDao.findTournaments(filter, 0L);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertTrue(tournaments.isEmpty());
//    }
//
//    @Test
//    public void testFindNoFilter() {
//        TournamentFilter filter = new TournamentFilter();
//        List<Tournament> tournaments = tournamentHibernateDao.findTournaments(filter, 0L);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertFalse(tournaments.isEmpty());
//        Assert.assertEquals(1,tournaments.size());
//        Assert.assertEquals(OTHER_ID,tournaments.get(0).getId());
//    }
//
//    @Test
//    public void testFindTournamentsAllFilters() {
//        SimpleJdbcInsert gameInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("game");
//        gameInsert.execute(Map.of("id", OTHER_ID + 1, "name", "Other Game", "genre", Genre.FPS, "image_id", ID));
//        SimpleJdbcInsert formatInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("game_format");
//        formatInsert.execute(Map.of("id", OTHER_ID + 1, "name", "other format", "players_per_team", 4, "game_id", OTHER_ID + 1));
//
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("tournament");
//        SqlParameterSource values = new MapSqlParameterSource()
//                .addValue("id", OTHER_ID + 1)
//                .addValue("creator_id", ID)
//                .addValue("name", "the One")
//                .addValue("game_id", OTHER_ID + 1)
//                .addValue("region", Region.NA)
//                .addValue("elo", Elo.HIGH)
//                .addValue("startDate", START_DATE.minusDays(1))
//                .addValue("endDate", END_DATE.minusDays(1))
//                .addValue("format", "other format")
//                .addValue("structure", Structure.HYBRID)
//                .addValue("maxParticipants", MAX_PARTICIPANTS)
//                .addValue("image_id", ID)
//                .addValue("open_inscriptions", true)
//                .addValue("is_finished", false)
//                .addValue("tournament_started", false)
//                .addValue("format_id", OTHER_ID + 1);
//        jdbcInsert.execute(values);
//
//        TournamentFilter filter = new TournamentFilter();
//        filter.setName("The One");
//        filter.setGame_id(OTHER_ID + 1);
//        filter.setElo(Elo.HIGH);
//        filter.setRegion(Region.NA);
//        filter.setFormat("other format");
//        filter.setStructure(Structure.HYBRID);
//        filter.setStartDate(START_DATE.minusDays(1));
//        filter.setEndDate(END_DATE.minusDays(1));
//        filter.setPlayersPerTeam(4);
//        filter.setGenre(Genre.FPS);
//        List<Tournament> tournaments = tournamentHibernateDao.findTournaments(filter, 0L);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertEquals(1, tournaments.size());
//        Tournament tournament = tournaments.get(0);
//        Assert.assertEquals("the One", tournament.getName());
//        Assert.assertEquals(OTHER_ID + 1, tournament.getGame_id().longValue());
//        Assert.assertEquals(Elo.HIGH, tournament.getElo());
//        Assert.assertEquals(Region.NA, tournament.getRegion());
//        Assert.assertEquals("other format", tournament.getFormat());
//        Assert.assertEquals(Structure.HYBRID, tournament.getStructure());
//    }
}
