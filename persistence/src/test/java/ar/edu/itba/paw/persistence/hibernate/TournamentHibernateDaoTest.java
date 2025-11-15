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
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
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
    private static final Long NO_ONE_ID = 0L;
    private static final Integer MAX_PARTICIPANTS = 4;
    private static final int PAGE_SIZE = 9;
    private static final int TOURNEYS_BY_ID = 20;
    private static final int TOURNEYS_WITH_ID = 22;
    private static final int OPEN_BY_ID = TOURNEYS_BY_ID / 2;
    private static final int OPEN_WITH_ID = TOURNEYS_WITH_ID / 2;
    private static final String SERVER_NAME = "Discord Channel";
    private static final String SERVER_PASSWORD = "Discord Password";
    private static final String SERVER_LINK = "Discord Link";

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
                ID,
                SERVER_NAME,
                SERVER_PASSWORD,
                SERVER_LINK);
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
        //TODO AGREGAR TESTOS AL SERVIDOR DE DISCORD
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
        Assert.assertEquals(NAME + " open x", tournament.getName());
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
        Optional<Tournament> found = tournamentHibernateDao.findById(NO_ONE_ID);

        Assert.assertFalse(found.isPresent());
    }

    @Test
    public void testHasNotTournamentStarted(){
        Boolean started = tournamentHibernateDao.isTournamentStarted(ID);

        Assert.assertFalse(started);
    }

    @Test
    public void testHasTournamentStarted(){
        Boolean started = tournamentHibernateDao.isTournamentStarted(ID+1);

        Assert.assertTrue(started);
    }

    @Test(expected = RuntimeException.class)
    public void testNoTournamentStarted(){
        tournamentHibernateDao.isTournamentStarted(NO_ONE_ID);
    }

    @Test
    public void testGetTournamentStructure(){
        Structure structure = tournamentHibernateDao.getTournamentStructure(ID);

        Assert.assertEquals(STRUCTURE, structure);
    }

    @Test(expected = RuntimeException.class)
    public void testGetTournamentStructureNotFound(){
        tournamentHibernateDao.getTournamentStructure(NO_ONE_ID);
    }

    @Test
    public void testFindByCreatorNone(){
        List<Tournament> tournaments = tournamentHibernateDao.findByCreator(NO_ONE_ID, NO_ONE_ID,false);

        Assert.assertNotNull(tournaments);
        Assert.assertTrue(tournaments.isEmpty());
    }

    @Test
    public void testFindByCreatorEmptyPageFalse(){
        List<Tournament> tournaments = tournamentHibernateDao.findByCreator(ID,2L,false);

        Assert.assertNotNull(tournaments);
        Assert.assertTrue(tournaments.isEmpty());
    }

    @Test
    public void testFindByCreatorEmptyPageTrue(){
        List<Tournament> tournaments = tournamentHibernateDao.findByCreator(ID,2L,true);

        Assert.assertNotNull(tournaments);
        Assert.assertTrue(tournaments.isEmpty());
    }

    @Test
    public void testFindByCreatorFullPageFalse(){
        List<Tournament> tournaments = tournamentHibernateDao.findByCreator(ID,NO_ONE_ID,false);

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(PAGE_SIZE,tournaments.size());
        Assert.assertTrue(tournaments.stream().allMatch((tournament -> Objects.equals(tournament.getCreatorId(), ID) && !tournament.getFinished())));
    }

    @Test
    public void testFindByCreatorFullPageTrue(){
        List<Tournament> tournaments = tournamentHibernateDao.findByCreator(ID,NO_ONE_ID,true);

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(PAGE_SIZE,tournaments.size());
        Assert.assertTrue(tournaments.stream().allMatch((tournament -> Objects.equals(tournament.getCreatorId(), ID) && tournament.getFinished())));
    }

    @Test
    public void testFindByCreatorLastPageFalse(){
        List<Tournament> tournaments = tournamentHibernateDao.findByCreator(ID,1L,false);

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(1L, tournaments.size());
        Assert.assertEquals(ID, tournaments.get(0).getCreatorId());
        Assert.assertFalse(tournaments.get(0).getFinished());
    }

    @Test
    public void testFindByCreatorLastPageTrue(){
        List<Tournament> tournaments = tournamentHibernateDao.findByCreator(ID,1L,true);

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(1L, tournaments.size());
        Assert.assertEquals(ID, tournaments.get(0).getCreatorId());
        Assert.assertTrue(tournaments.get(0).getFinished());
    }

    @Test
    public void testSetFinished(){
        tournamentHibernateDao.setFinished(ID);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "is_finished = true and id = " + ID));
    }

    @Test
    public void testSetFinishedRedundant(){
        tournamentHibernateDao.setFinished(ID+1);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "is_finished = true and id = " + (ID+1)));
    }

    @Test
    public void testCloseInscriptions(){
        tournamentHibernateDao.closeInscriptions(ID);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "open_inscriptions = false and id = " + ID));
    }

    @Test
    public void testCloseInscriptionsRedundant(){
        tournamentHibernateDao.closeInscriptions(ID+1);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "open_inscriptions = false and id = " + (ID+1)));
    }

    @Test
    public void testFindUserActiveTournamentsFullPage(){
        List<Tournament> tournaments = tournamentHibernateDao.findUserActiveTournaments(ID,0L);

        Assert.assertNotNull(tournaments);
        Assert.assertFalse(tournaments.isEmpty());
        Assert.assertEquals(PAGE_SIZE,tournaments.size());
        Assert.assertTrue(tournaments.stream().allMatch((tournament -> !tournament.getFinished() && tournament.getId() % 2 == 0 && tournament.getId() < 120)));
    }

    @Test
    public void testFindUserPastTournamentsFullPage(){
        List<Tournament> tournaments = tournamentHibernateDao.findUserPastTournaments(ID,0L);

        Assert.assertNotNull(tournaments);
        Assert.assertFalse(tournaments.isEmpty());
        Assert.assertEquals(PAGE_SIZE,tournaments.size());
        Assert.assertTrue(tournaments.stream().allMatch((tournament -> tournament.getFinished() && tournament.getId() % 2 == 1 && tournament.getId() < 120)));
    }

    @Test
    public void testFindUserActiveTournamentsEmptyPage(){
        List<Tournament> tournaments = tournamentHibernateDao.findUserActiveTournaments(ID,2L);

        Assert.assertNotNull(tournaments);
        Assert.assertTrue(tournaments.isEmpty());
    }

    @Test
    public void testFindUserPastTournamentsEmptyPage(){
        List<Tournament> tournaments = tournamentHibernateDao.findUserPastTournaments(ID,2L);

        Assert.assertNotNull(tournaments);
        Assert.assertTrue(tournaments.isEmpty());
    }

    @Test
    public void testFindUserActiveTournamentsLastPage(){
        List<Tournament> tournaments = tournamentHibernateDao.findUserActiveTournaments(ID,1L);

        Assert.assertNotNull(tournaments);
        Assert.assertFalse(tournaments.isEmpty());
        Assert.assertEquals(1,tournaments.size());
        Tournament tournament = tournaments.get(0);
        Assert.assertTrue(!tournament.getFinished() && tournament.getId() % 2 == 0 && tournament.getId() < 120);
    }

    @Test
    public void testFindUserPastTournamentsLastPage(){
        List<Tournament> tournaments = tournamentHibernateDao.findUserPastTournaments(ID,1L);

        Assert.assertNotNull(tournaments);
        Assert.assertFalse(tournaments.isEmpty());
        Assert.assertEquals(1,tournaments.size());
        Tournament tournament = tournaments.get(0);
        Assert.assertTrue(tournament.getFinished() && tournament.getId() % 2 == 1 && tournament.getId() < 120);
    }

    @Test
    public void testFindNoOnesActiveTournaments(){
        List<Tournament> tournaments = tournamentHibernateDao.findUserActiveTournaments(NO_ONE_ID,NO_ONE_ID);

        Assert.assertNotNull(tournaments);
        Assert.assertTrue(tournaments.isEmpty());
    }

    @Test
    public void testFindNoOnesPastTournaments(){
        List<Tournament> tournaments = tournamentHibernateDao.findUserPastTournaments(NO_ONE_ID,NO_ONE_ID);

        Assert.assertNotNull(tournaments);
        Assert.assertTrue(tournaments.isEmpty());
    }

    @Test
    public void testSearchByNameAll(){
        List<Tournament> tournaments = tournamentHibernateDao.searchByName("");

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(TOURNEYS_WITH_ID, tournaments.size());
        Assert.assertTrue(tournaments.stream().allMatch(t -> t.getName().toLowerCase().contains("".toLowerCase())));
    }

    @Test
    public void testSearchByNameSome(){
        List<Tournament> tournaments = tournamentHibernateDao.searchByName("open");

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(OPEN_WITH_ID, tournaments.size());
        Assert.assertTrue(tournaments.stream().allMatch(t -> t.getName().toLowerCase().contains("open".toLowerCase())));
    }

    @Test
    public void testSearchByNameOne(){
        List<Tournament> tournaments = tournamentHibernateDao.searchByName("open x");

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(1, tournaments.size());
        Tournament t = tournaments.get(0);
        Assert.assertTrue(t.getName().toLowerCase().contains("open x".toLowerCase()));
    }

    @Test
    public void testSearchByNameNone(){
        List<Tournament> tournaments = tournamentHibernateDao.searchByName("NonExistent");

        Assert.assertNotNull(tournaments);
        Assert.assertTrue(tournaments.isEmpty());
    }

    @Test
    public void testSetTournamentWinner(){
        tournamentHibernateDao.setTournamentWinner(ID, ID);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "id = " + ID + " and tournament_winner = id"));
    }

    @Test
    public void testStartTournament(){
        tournamentHibernateDao.startTournament(ID);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "id = " + ID + " and tournament_started = true"));
    }

    @Test
    public void testStartTournamentRedundant(){
        tournamentHibernateDao.startTournament(ID+1);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "id = " + (ID+1) + " and tournament_started = true"));
    }

    @Test
    public void testSetIsNotGroupStage(){
        tournamentHibernateDao.setIsGroupStage(ID, false);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "is_group_stage = false and id = " + ID));
    }

    @Test
    public void testSetIsNotGroupStageRedundant(){
        tournamentHibernateDao.setIsGroupStage(ID+1, false);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "is_group_stage = false and id = " + (ID+1)));
    }

    @Test
    public void testSetIsGroupStage(){
        tournamentHibernateDao.setIsGroupStage(ID+1, true);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "is_group_stage = true and id = " + (ID+1)));
    }

    @Test
    public void testSetIsGroupStageRedundant(){
        tournamentHibernateDao.setIsGroupStage(ID, true);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "is_group_stage = true and id = " + ID));
    }

    @Test
    public void testGetIsGroupStage(){
        Boolean isGroupStage = tournamentHibernateDao.getIsGroupStage(ID);

        Assert.assertTrue(isGroupStage);
    }

    @Test
    public void testGetIsNotGroupStage(){
        Boolean isGroupStage = tournamentHibernateDao.getIsGroupStage(ID+1);

        Assert.assertFalse(isGroupStage);
    }

    @Test
    public void testUpdateAllStartDates(){
        jdbcTemplate.update("update tournament set end_date = '2027-02-21' where end_date = '2025-04-20'");
        tournamentHibernateDao.updateAllStartDates();
        em.flush();

        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "tournament_started = false and start_date < '" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"));
        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "id = 102 and start_date = '" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"));
    }

    @Test
    public void testUpdateAllEndDates(){
        tournamentHibernateDao.updateAllEndDates();
        em.flush();

        Assert.assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "is_finished = false and end_date < '" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"));
        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "id = 102 and end_date = '" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"));
    }

    @Test
    public void testIsNotClosed(){
        boolean closed = tournamentHibernateDao.isClosed(ID);

        Assert.assertFalse(closed);
    }

    @Test
    public void testIsClosed(){
        boolean closed = tournamentHibernateDao.isClosed(ID+1);

        Assert.assertTrue(closed);
    }

    @Test
    public void testGetUserActiveTournaments(){
        int ans = tournamentHibernateDao.getUserActiveTournamentsPages(ID);

        Assert.assertEquals((int)Math.ceil(((double) OPEN_BY_ID/PAGE_SIZE)),ans);
    }

    @Test
    public void testGetUserPastTournaments(){
        int ans = tournamentHibernateDao.getUserPastTournamentsPages(ID);

        Assert.assertEquals((int)Math.ceil(((double) OPEN_BY_ID/PAGE_SIZE)),ans);
    }

    @Test
    public void testGetNoOnesActiveTournamentsPages(){
        int ans = tournamentHibernateDao.getUserActiveTournamentsPages(0L);

        Assert.assertEquals(0,ans);
    }

    @Test
    public void testGetNoOnesPastTournamentsPages(){
        int ans = tournamentHibernateDao.getUserPastTournamentsPages(0L);

        Assert.assertEquals(0,ans);
    }

    @Test
    public void testGetCreatedAndFinishedTournamentPages(){
        int ans = tournamentHibernateDao.getCreatedAndFinishedTournamentsPages(ID);

        Assert.assertEquals((int)Math.ceil(((double) OPEN_BY_ID/PAGE_SIZE)),ans);
    }

    @Test
    public void testGetCreatedAndOngoingTournamentPages(){
        int ans = tournamentHibernateDao.getCreatedAndOngoingTournamentsPages(ID);

        Assert.assertEquals((int)Math.ceil(((double) OPEN_BY_ID/PAGE_SIZE)),ans);
    }

    @Test
    public void testGetCreatedAndFinishedNothing(){
        int ans = tournamentHibernateDao.getCreatedAndFinishedTournamentsPages(0L);

        Assert.assertEquals(0,ans);
    }

    @Test
    public void testGetCreatedAndOngoingNothing(){
        int ans = tournamentHibernateDao.getCreatedAndOngoingTournamentsPages(0L);

        Assert.assertEquals(0,ans);
    }

    @Test
    public void testGetUsersWonTournamentsPages(){
        jdbcTemplate.update("update tournament set tournament_winner = id where id < 120");
        int ans = tournamentHibernateDao.getUserWonTournamentPages(ID);

        Assert.assertEquals((int)Math.ceil(((double) TOURNEYS_BY_ID/PAGE_SIZE)),ans);
    }

    @Test
    public void testGetNoWonTournamentsPages(){
        int ans = tournamentHibernateDao.getUserWonTournamentPages(ID);

        Assert.assertEquals(0,ans);
    }

    @Test
    public void testGetUsersWonTournamentsFullPage(){
        jdbcTemplate.update("update tournament set tournament_winner = id where id < 110");
        List<Tournament> ans = tournamentHibernateDao.getUserWonTournament(ID,NO_ONE_ID);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(PAGE_SIZE,ans.size());
        Assert.assertTrue(ans.stream().allMatch(t-> t.getWinner().getUser().getId() == ID));
    }

    @Test
    public void testGetUsersWonTournamentsLastPage(){
        jdbcTemplate.update("update tournament set tournament_winner = id where id < 110");
        List<Tournament> ans = tournamentHibernateDao.getUserWonTournament(ID,1L);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(1,ans.size());
        Tournament t = ans.get(0);
        Assert.assertEquals(ID.longValue(), t.getWinner().getUser().getId());
    }

    @Test
    public void testGetUsersWonTournamentsEmptyPage(){
        jdbcTemplate.update("update tournament set tournament_winner = id where id < 110");
        List<Tournament> ans = tournamentHibernateDao.getUserWonTournament(ID,2L);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testGetNoWonTournaments(){
        List<Tournament> ans = tournamentHibernateDao.getUserWonTournament(ID,NO_ONE_ID);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testGetNoOnesWonTournaments(){
        List<Tournament> ans = tournamentHibernateDao.getUserWonTournament(NO_ONE_ID,NO_ONE_ID);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testGetTournamentParticipantsCount(){
        int count = tournamentHibernateDao.getTournamentParticipantsCount(ID);

        Assert.assertEquals(2, count);
    }

    @Rollback
    @Test
    public void testGetEmptyTournamentsParticipant(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"participant");
        int count = tournamentHibernateDao.getTournamentParticipantsCount(ID);

        Assert.assertEquals(0,count);
    }

    @Test
    public void testGetNoTournamentsParticipants(){
        int count = tournamentHibernateDao.getTournamentParticipantsCount(NO_ONE_ID);

        Assert.assertEquals(0,count);
    }
    @Test
    public void testUpdateTournamentInfo(){
        String newName = "Updated Name";
        LocalDate newStartDate = LocalDate.of(2025, 9, 30);
        LocalDate newEndDate = LocalDate.of(2026, 9, 30);
        Integer newMaxParticipants = 16;
        tournamentHibernateDao.updateTournamentInfo(ID, newName, newStartDate, newEndDate, newMaxParticipants);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "id = " + ID + " and name = '" + newName + "' and max_participants = " + newMaxParticipants
        + " and start_date = '" + newStartDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "' and end_date = '"
                        + newEndDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"));
    }

    @Test
    public void testUpdateTournamentRating(){
        tournamentHibernateDao.updateTournamentRating(ID,4.0f);
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "id = " + ID + " and rating = 4.0"));
    }

    @Test
    public void testGetPageAmount(){
        TournamentFilter filter = new TournamentFilter();

        Integer pageAmount = tournamentHibernateDao.getPageAmount(PAGE_SIZE, filter);

        Assert.assertEquals(TOURNEYS_WITH_ID, pageAmount.intValue());
    }

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
//        Map<Long, List<Tournament>> pages = tournamentHibernateDao.getUnfilteredTournamentPages(NO_ONE_ID);
//
//        Assert.assertEquals(2, pages.size());
//        for (List<Tournament> list : pages.values()) {
//            Assert.assertEquals(9, list.size());
//            Assert.assertTrue(list.stream().allMatch(Tournament::getOpenInscriptions));
//        }
//    }

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
