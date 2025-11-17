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
import java.util.*;

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
    private static final int DISTINCT_GAMES = 4;
    private static final Integer MAX_PARTICIPANTS = 4;
    private static final int PAGE_SIZE = 9;
    private static final int MAX_GAMES = 3;
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
        Assert.assertEquals(SERVER_LINK, tournament.getDiscordChannel());
        Assert.assertEquals(SERVER_NAME, tournament.getServerName());
        Assert.assertEquals(SERVER_PASSWORD, tournament.getServerPassword());
        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "id = " + tournament.getId() + " and name = '" + NAME + "' and creator_id = " + ID
         + " and game_id = creator_id and image_id = game_id and format_id = game_id and rules_id = game_id and "
        + "format = '" + FORMAT + "' and region = '" + REGION + "' and elo = '" + ELO + "' and structure = '"
        + STRUCTURE + "' and max_participants = " + MAX_PARTICIPANTS + " and is_finished = false and " +
        " open_inscriptions = true and start_date = '" + START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE)
        + "' and end_date = '" + END_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE) + "' and server_name = '"
        + SERVER_NAME + "' and server_password = '" + SERVER_PASSWORD + "' and discord_channel = '" + SERVER_LINK + "'"));
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
//
//    @Test
//    public void testFindByCreatorNone(){
//        List<Tournament> tournaments = tournamentHibernateDao.findByCreator(NO_ONE_ID, NO_ONE_ID,false);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertTrue(tournaments.isEmpty());
//    }
//
//    @Test
//    public void testFindByCreatorEmptyPageFalse(){
//        List<Tournament> tournaments = tournamentHibernateDao.findByCreator(ID,2L,false);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertTrue(tournaments.isEmpty());
//    }
//
//    @Test
//    public void testFindByCreatorEmptyPageTrue(){
//        List<Tournament> tournaments = tournamentHibernateDao.findByCreator(ID,2L,true);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertTrue(tournaments.isEmpty());
//    }
//
//    @Test
//    public void testFindByCreatorFullPageFalse(){
//        List<Tournament> tournaments = tournamentHibernateDao.findByCreator(ID,NO_ONE_ID,false);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertEquals(PAGE_SIZE,tournaments.size());
//        Assert.assertTrue(tournaments.stream().allMatch((tournament -> Objects.equals(tournament.getCreatorId(), ID) && !tournament.getFinished())));
//    }
//
//    @Test
//    public void testFindByCreatorFullPageTrue(){
//        List<Tournament> tournaments = tournamentHibernateDao.findByCreator(ID,NO_ONE_ID,true);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertEquals(PAGE_SIZE,tournaments.size());
//        Assert.assertTrue(tournaments.stream().allMatch((tournament -> Objects.equals(tournament.getCreatorId(), ID) && tournament.getFinished())));
//    }
//
//    @Test
//    public void testFindByCreatorLastPageFalse(){
//        List<Tournament> tournaments = tournamentHibernateDao.findByCreator(ID,1L,false);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertEquals(1L, tournaments.size());
//        Assert.assertEquals(ID, tournaments.get(0).getCreatorId());
//        Assert.assertFalse(tournaments.get(0).getFinished());
//    }
//
//    @Test
//    public void testFindByCreatorLastPageTrue(){
//        List<Tournament> tournaments = tournamentHibernateDao.findByCreator(ID,1L,true);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertEquals(1L, tournaments.size());
//        Assert.assertEquals(ID, tournaments.get(0).getCreatorId());
//        Assert.assertTrue(tournaments.get(0).getFinished());
//    }
//
//    @Test
//    public void testSetFinished(){
//        tournamentHibernateDao.setFinished(ID);
//        em.flush();
//
//        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
//                "is_finished = true and id = " + ID));
//    }

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
//
//    @Test
//    public void testFindUserActiveTournamentsFullPage(){
//        List<Tournament> tournaments = tournamentHibernateDao.findUserActiveTournaments(ID,0L);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertFalse(tournaments.isEmpty());
//        Assert.assertEquals(PAGE_SIZE,tournaments.size());
//        Assert.assertTrue(tournaments.stream().allMatch((tournament -> !tournament.getFinished() && tournament.getId() % 2 == 0 && tournament.getId() < 120)));
//    }
//
//    @Test
//    public void testFindUserPastTournamentsFullPage(){
//        List<Tournament> tournaments = tournamentHibernateDao.findUserPastTournaments(ID,0L);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertFalse(tournaments.isEmpty());
//        Assert.assertEquals(PAGE_SIZE,tournaments.size());
//        Assert.assertTrue(tournaments.stream().allMatch((tournament -> tournament.getFinished() && tournament.getId() % 2 == 1 && tournament.getId() < 120)));
//    }
//
//    @Test
//    public void testFindUserActiveTournamentsEmptyPage(){
//        List<Tournament> tournaments = tournamentHibernateDao.findUserActiveTournaments(ID,2L);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertTrue(tournaments.isEmpty());
//    }
//
//    @Test
//    public void testFindUserPastTournamentsEmptyPage(){
//        List<Tournament> tournaments = tournamentHibernateDao.findUserPastTournaments(ID,2L);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertTrue(tournaments.isEmpty());
//    }
//
//    @Test
//    public void testFindUserActiveTournamentsLastPage(){
//        List<Tournament> tournaments = tournamentHibernateDao.findUserActiveTournaments(ID,1L);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertFalse(tournaments.isEmpty());
//        Assert.assertEquals(1,tournaments.size());
//        Tournament tournament = tournaments.get(0);
//        Assert.assertTrue(!tournament.getFinished() && tournament.getId() % 2 == 0 && tournament.getId() < 120);
//    }
//
//    @Test
//    public void testFindUserPastTournamentsLastPage(){
//        List<Tournament> tournaments = tournamentHibernateDao.findUserPastTournaments(ID,1L);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertFalse(tournaments.isEmpty());
//        Assert.assertEquals(1,tournaments.size());
//        Tournament tournament = tournaments.get(0);
//        Assert.assertTrue(tournament.getFinished() && tournament.getId() % 2 == 1 && tournament.getId() < 120);
//    }
//
//    @Test
//    public void testFindNoOnesActiveTournaments(){
//        List<Tournament> tournaments = tournamentHibernateDao.findUserActiveTournaments(NO_ONE_ID,NO_ONE_ID);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertTrue(tournaments.isEmpty());
//    }
//
//    @Test
//    public void testFindNoOnesPastTournaments(){
//        List<Tournament> tournaments = tournamentHibernateDao.findUserPastTournaments(NO_ONE_ID,NO_ONE_ID);
//
//        Assert.assertNotNull(tournaments);
//        Assert.assertTrue(tournaments.isEmpty());
//    }

    @Test
    public void testSearchByNameAll(){
        List<Tournament> tournaments = tournamentHibernateDao.searchByName("", 0L);

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(OPEN_WITH_ID, tournaments.size());
        Assert.assertTrue(tournaments.stream().allMatch(t -> t.getName().toLowerCase().contains("".toLowerCase())));
    }

    @Test
    public void testSearchByNameSome(){
        List<Tournament> tournaments = tournamentHibernateDao.searchByName("open", 0L);

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(OPEN_WITH_ID, tournaments.size());
        Assert.assertTrue(tournaments.stream().allMatch(t -> t.getName().toLowerCase().contains("open".toLowerCase())));
    }

    @Test
    public void testSearchByNameOne(){
        List<Tournament> tournaments = tournamentHibernateDao.searchByName("open x", 0L);

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(1, tournaments.size());
        Tournament t = tournaments.get(0);
        Assert.assertTrue(t.getName().toLowerCase().contains("open x".toLowerCase()));
    }

    @Test
    public void testSearchByNameNone(){
        List<Tournament> tournaments = tournamentHibernateDao.searchByName("NonExistent", 0L);

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
//
//    @Test
//    public void testGetUserActiveTournaments(){
//        int ans = tournamentHibernateDao.getUserActiveTournamentsPages(ID);
//
//        Assert.assertEquals((int)Math.ceil(((double) OPEN_BY_ID/PAGE_SIZE)),ans);
//    }
//
//    @Test
//    public void testGetUserPastTournaments(){
//        int ans = tournamentHibernateDao.getUserPastTournamentsPages(ID);
//
//        Assert.assertEquals((int)Math.ceil(((double) OPEN_BY_ID/PAGE_SIZE)),ans);
//    }
//
//    @Test
//    public void testGetNoOnesActiveTournamentsPages(){
//        int ans = tournamentHibernateDao.getUserActiveTournamentsPages(0L);
//
//        Assert.assertEquals(0,ans);
//    }
//
//    @Test
//    public void testGetNoOnesPastTournamentsPages(){
//        int ans = tournamentHibernateDao.getUserPastTournamentsPages(0L);
//
//        Assert.assertEquals(0,ans);
//    }
//
//    @Test
//    public void testGetCreatedAndFinishedTournamentPages(){
//        int ans = tournamentHibernateDao.getCreatedAndFinishedTournamentsPages(ID);
//
//        Assert.assertEquals((int)Math.ceil(((double) OPEN_BY_ID/PAGE_SIZE)),ans);
//    }
//
//    @Test
//    public void testGetCreatedAndOngoingTournamentPages(){
//        int ans = tournamentHibernateDao.getCreatedAndOngoingTournamentsPages(ID);
//
//        Assert.assertEquals((int)Math.ceil(((double) OPEN_BY_ID/PAGE_SIZE)),ans);
//    }
//
//    @Test
//    public void testGetCreatedAndFinishedNothing(){
//        int ans = tournamentHibernateDao.getCreatedAndFinishedTournamentsPages(0L);
//
//        Assert.assertEquals(0,ans);
//    }
//
//    @Test
//    public void testGetCreatedAndOngoingNothing(){
//        int ans = tournamentHibernateDao.getCreatedAndOngoingTournamentsPages(0L);
//
//        Assert.assertEquals(0,ans);
//    }
//
//    @Test
//    public void testGetUsersWonTournamentsPages(){
//        jdbcTemplate.update("update tournament set tournament_winner = id where id < 120");
//        int ans = tournamentHibernateDao.getUserWonTournamentPages(ID);
//
//        Assert.assertEquals((int)Math.ceil(((double) TOURNEYS_BY_ID/PAGE_SIZE)),ans);
//    }
//
//    @Test
//    public void testGetNoWonTournamentsPages(){
//        int ans = tournamentHibernateDao.getUserWonTournamentPages(ID);
//
//        Assert.assertEquals(0,ans);
//    }
//
//    @Test
//    public void testGetUsersWonTournamentsFullPage(){
//        jdbcTemplate.update("update tournament set tournament_winner = id where id < 110");
//        List<Tournament> ans = tournamentHibernateDao.getUserWonTournament(ID,NO_ONE_ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertFalse(ans.isEmpty());
//        Assert.assertEquals(PAGE_SIZE,ans.size());
//        Assert.assertTrue(ans.stream().allMatch(t-> t.getWinner().getUser().getId() == ID));
//    }
//
//    @Test
//    public void testGetUsersWonTournamentsLastPage(){
//        jdbcTemplate.update("update tournament set tournament_winner = id where id < 110");
//        List<Tournament> ans = tournamentHibernateDao.getUserWonTournament(ID,1L);
//
//        Assert.assertNotNull(ans);
//        Assert.assertFalse(ans.isEmpty());
//        Assert.assertEquals(1,ans.size());
//        Tournament t = ans.get(0);
//        Assert.assertEquals(ID.longValue(), t.getWinner().getUser().getId());
//    }
//
//    @Test
//    public void testGetUsersWonTournamentsEmptyPage(){
//        jdbcTemplate.update("update tournament set tournament_winner = id where id < 110");
//        List<Tournament> ans = tournamentHibernateDao.getUserWonTournament(ID,2L);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans.isEmpty());
//    }
//
//    @Test
//    public void testGetNoWonTournaments(){
//        List<Tournament> ans = tournamentHibernateDao.getUserWonTournament(ID,NO_ONE_ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans.isEmpty());
//    }
//
//    @Test
//    public void testGetNoOnesWonTournaments(){
//        List<Tournament> ans = tournamentHibernateDao.getUserWonTournament(NO_ONE_ID,NO_ONE_ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans.isEmpty());
//    }

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
        tournamentHibernateDao.updateTournamentInfo(ID, newName, newStartDate, newEndDate, newMaxParticipants, SERVER_NAME, SERVER_PASSWORD, SERVER_LINK);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "id = " + ID + " and name = '" + newName + "' and max_participants = " + newMaxParticipants
        + " and start_date = '" + newStartDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "' and end_date = '"
                        + newEndDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "' and server_name = '" + SERVER_NAME
        + "' and server_password = '" + SERVER_PASSWORD + "' and discord_channel = '" + SERVER_LINK + "'"));
    }

    @Test
    public void testUpdateTournamentRating(){
        tournamentHibernateDao.updateTournamentRating(ID,4.0f);
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"tournament",
                "id = " + ID + " and rating = 4.0"));
    }

    @Test
    public void testGetPageAmountNoFilter(){
        TournamentFilter filter = new TournamentFilter();

        Integer pageAmount = tournamentHibernateDao.getPageAmount(MAX_GAMES, filter);

        Assert.assertEquals((int) Math.ceil((double) DISTINCT_GAMES / MAX_GAMES), pageAmount.intValue());
    }

    @Test
    public void testGetPageAmountFilter(){
        TournamentFilter filter = new TournamentFilter();
        filter.setElo(ELO);

        Integer pageAmount = tournamentHibernateDao.getPageAmount(PAGE_SIZE, filter);

        Assert.assertEquals((int)Math.ceil((double)OPEN_WITH_ID/PAGE_SIZE), pageAmount.intValue());
    }

    @Test
    public void testGetUnfilteredTournamentFullPage(){
        Map<Long, List<Tournament>> pages = tournamentHibernateDao.getUnfilteredTournamentPages(NO_ONE_ID);

        Assert.assertEquals(MAX_GAMES, pages.size());
        List<Integer> comp = new ArrayList<>();
        comp.add(7);
        comp.add(2);
        comp.add(1);
        int i = 0;
        for (List<Tournament> list : pages.values()) {
            Assert.assertEquals(comp.get(i++).intValue(), list.size());
            Assert.assertTrue(list.stream().allMatch(Tournament::getOpenInscriptions));
        }
    }

    @Test
    public void testGetUnfilteredTournamentLastPage(){
        Map<Long, List<Tournament>> pages = tournamentHibernateDao.getUnfilteredTournamentPages(1L);

        Assert.assertEquals(1, pages.size());
        for (List<Tournament> list : pages.values()) {
            Assert.assertEquals(1,list.size());
            Assert.assertTrue(list.stream().allMatch(Tournament::getOpenInscriptions));
        }
    }

    @Test
    public void testGetUnfilteredTournamentEmptyPage(){
        Map<Long, List<Tournament>> pages = tournamentHibernateDao.getUnfilteredTournamentPages(2L);

        Assert.assertTrue(pages.isEmpty());
    }

    @Test
    public void testFindTournamentsWithNameAndFormatFilter() {
        TournamentFilter filter = new TournamentFilter();
        filter.setName(NAME + " open x");
        filter.setFormat(FORMAT);
        List<Tournament> tournaments = tournamentHibernateDao.findTournaments(filter, 0L);

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(1, tournaments.size());
        Assert.assertEquals(NAME + " open x", tournaments.get(0).getName());
        Assert.assertEquals(FORMAT, tournaments.get(0).getFormat());
    }

    @Test
    public void testFindTournamentsWithGameIdAndEloFilter() {
        TournamentFilter filter = new TournamentFilter();
        filter.setGameId(ID+3);
        filter.setElo(ELO);
        List<Tournament> tournaments = tournamentHibernateDao.findTournaments(filter, 0L);

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(1, tournaments.size());
        Assert.assertEquals(ID+3, tournaments.get(0).getGameId().longValue());
        Assert.assertEquals(ELO, tournaments.get(0).getElo());
    }

    @Test
    public void testFindTournamentsWithRegionAndStructureFilter() {
        TournamentFilter filter = new TournamentFilter();
        filter.setRegion(Region.LAN);
        filter.setStructure(STRUCTURE);
        List<Tournament> tournaments = tournamentHibernateDao.findTournaments(filter, 0L);

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(1, tournaments.size());
        Assert.assertEquals(Region.LAN, tournaments.get(0).getRegion());
        Assert.assertEquals(STRUCTURE, tournaments.get(0).getStructure());
    }

    @Test
    public void testFindTournamentsWithGenreAndPlayersPerTeamFilter() {
        TournamentFilter filter = new TournamentFilter();
        filter.setGenre(Genre.TPS);
        filter.setPlayersPerTeam(6);
        List<Tournament> tournaments = tournamentHibernateDao.findTournaments(filter, 0L);

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(1, tournaments.size());
        Assert.assertEquals(Genre.TPS, tournaments.get(0).getGame().getGenre());
        Assert.assertEquals(6,tournaments.get(0).getFormatEntity().getPlayersPerTeam().intValue());
    }

    @Test
    public void testFindTournamentsWithDateFilters() {
        TournamentFilter filter = new TournamentFilter();
        filter.setStartDate(LocalDate.of(2025,2,21));
        filter.setEndDate(LocalDate.of(2025,4,20));
        List<Tournament> tournaments = tournamentHibernateDao.findTournaments(filter, 0L);

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(1, tournaments.size());
        Assert.assertEquals(LocalDate.of(2025,2,21), tournaments.get(0).getStartDate());
        Assert.assertEquals(LocalDate.of(2025,4,20), tournaments.get(0).getEndDate());
    }

    @Test
    public void testFindTournamentsPagination() {
        TournamentFilter filter = new TournamentFilter();
        filter.setRegion(REGION);
        List<Tournament> firstPage = tournamentHibernateDao.findTournaments(filter, 0L);
        List<Tournament> secondPage = tournamentHibernateDao.findTournaments(filter, 1L);
        List<Tournament> thirdPage = tournamentHibernateDao.findTournaments(filter, 2L);

        Assert.assertNotNull(firstPage);
        Assert.assertNotNull(secondPage);
        Assert.assertNotNull(thirdPage);
        Assert.assertEquals(9, firstPage.size());
        Assert.assertEquals(1, secondPage.size());
        Assert.assertTrue(thirdPage.isEmpty());
    }

    @Test
    public void testFindTournamentsNoResults() {
        TournamentFilter filter = new TournamentFilter();
        filter.setName("NonExistent");
        List<Tournament> tournaments = tournamentHibernateDao.findTournaments(filter, 0L);

        Assert.assertNotNull(tournaments);
        Assert.assertTrue(tournaments.isEmpty());
    }

    @Test
    public void testFindNoFilter() {
        TournamentFilter filter = new TournamentFilter();
        List<Tournament> tournaments1 = tournamentHibernateDao.findTournaments(filter, 0L);
        List<Tournament> tournaments2 = tournamentHibernateDao.findTournaments(filter, 1L);

        Assert.assertNotNull(tournaments1);
        Assert.assertFalse(tournaments1.isEmpty());
        Assert.assertEquals(PAGE_SIZE,tournaments1.size());
        Assert.assertNotNull(tournaments2);
        Assert.assertFalse(tournaments2.isEmpty());
        Assert.assertEquals(OPEN_WITH_ID - PAGE_SIZE,tournaments2.size());
    }

    @Test
    public void testFindTournamentsAllFilters() {
        TournamentFilter filter = new TournamentFilter();
        filter.setName(NAME + " open x");
        filter.setGameId(ID);
        filter.setElo(ELO);
        filter.setRegion(REGION);
        filter.setFormat(FORMAT);
        filter.setStructure(STRUCTURE);
        filter.setStartDate(START_DATE);
        filter.setEndDate(END_DATE);
        filter.setPlayersPerTeam(6);
        filter.setGenre(GENRE);
        List<Tournament> tournaments = tournamentHibernateDao.findTournaments(filter, 0L);

        Assert.assertNotNull(tournaments);
        Assert.assertEquals(1, tournaments.size());
        Tournament tournament = tournaments.get(0);
        Assert.assertEquals(NAME + " open x", tournament.getName());
        Assert.assertEquals(ID, tournament.getGameId());
        Assert.assertEquals(ELO, tournament.getElo());
        Assert.assertEquals(REGION, tournament.getRegion());
        Assert.assertEquals(FORMAT, tournament.getFormat());
        Assert.assertEquals(STRUCTURE, tournament.getStructure());
        Assert.assertEquals(START_DATE, tournaments.get(0).getStartDate());
        Assert.assertEquals(END_DATE, tournaments.get(0).getEndDate());
        Assert.assertEquals(GENRE, tournaments.get(0).getGame().getGenre());
        Assert.assertEquals(6,tournaments.get(0).getFormatEntity().getPlayersPerTeam().intValue());
    }
}
