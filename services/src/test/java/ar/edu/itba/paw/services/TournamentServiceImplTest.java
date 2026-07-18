package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.exception.TournamentInscriptionsOpenException;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.Match.PointsPair;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Rules;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class TournamentServiceImplTest {
    private static final Genre GENRE = Genre.MOBA;
    private static final String NAME = "Jerma Rumble";
    private static final Elo ELO = Elo.MID;
    private static final Region REGION = Region.LAS;
    private static final Structure STRUCTURE = Structure.LEAGUE;
    private static final LocalDate START_DATE = LocalDate.of(2025, 2, 21);
    private static final LocalDate END_DATE = LocalDate.of(2026, 2, 21);
    private static final String FORMAT = "some format";
    private static final long ID = 1L;
    private static final long OTHER_ID = 2L;
    private static final int MAX_PARTICIPANTS = 8;
    private static final byte[] IMAGE = "0".repeat(16).getBytes(StandardCharsets.UTF_8);

    @Mock
    private TournamentDao mockDao;

    @Mock
    private ImageDao imageDao;

    @Mock
    private UserDao userDao;

    @Mock
    private MailService ms;

    @Mock
    private ParticipantDao participantDao;

    @Mock
    private MatchDao matchDao;

    @Mock
    private GameDao gameDao;

    @Mock
    private GameFormatDao gameFormatDao;

    @Mock
    private RulesDao rulesDao;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    @Test
    public void testCreate(){
        User fakeUser = new User(ID,NAME,NAME,NAME,true,null,null,null,"");
        Tournament fakeTournament = createFakeTournament(fakeUser);
        Mockito.when(imageDao.insertImage(IMAGE)).thenReturn(ID);
        Mockito.when(rulesDao.insertRules(IMAGE)).thenReturn(new Rules(ID));
        Mockito.when(mockDao.create(ID,NAME,ID,REGION,ELO,START_DATE,END_DATE,FORMAT,STRUCTURE,MAX_PARTICIPANTS,ID,true,false,ID, ID,NAME,NAME,NAME))
                .thenReturn(fakeTournament);
        Mockito.when(userDao.findById(ID))
                .thenReturn(Optional.of(fakeUser));

        Tournament ans = tournamentService.create(ID,NAME,ID,REGION,ELO,START_DATE,END_DATE,FORMAT,STRUCTURE,MAX_PARTICIPANTS,IMAGE,true,false,ID, IMAGE,NAME,NAME,NAME);

        Assert.assertNotNull(ans);
        Assert.assertEquals(ID,ans.getId().longValue());
        Assert.assertEquals(ID,ans.getCreatorId().longValue());
        Assert.assertEquals(NAME,ans.getName());
        Assert.assertEquals(ID,ans.getGameId());
        Assert.assertEquals(REGION,ans.getRegion());
        Assert.assertEquals(ELO,ans.getElo());
        Assert.assertEquals(START_DATE,ans.getStartDate());
        Assert.assertEquals(END_DATE,ans.getEndDate());
        Assert.assertEquals(FORMAT,ans.getFormat());
        Assert.assertEquals(STRUCTURE,ans.getStructure());
        Assert.assertEquals(MAX_PARTICIPANTS,ans.getMaxParticipants());
        Assert.assertEquals(ID,ans.getImageId().longValue());
        Assert.assertEquals(ID,ans.getRules().getId().longValue());
        Assert.assertTrue(ans.getOpenInscriptions());
        Assert.assertFalse(ans.getFinished());
        Assert.assertNull(ans.getTournamentWinner());
        Assert.assertNull(ans.getIsGroupStage());
        Assert.assertFalse(ans.getTournamentStarted());
        Assert.assertEquals(ID,ans.getFormatId().longValue());
        Assert.assertEquals(NAME,ans.getServerPassword());
        Assert.assertEquals(NAME,ans.getServerName());
        Assert.assertEquals(NAME,ans.getDiscordChannel());
    }

    @Test
    public void testCloseInscriptionsLeagueGeneratesRoundRobin() {
        Tournament tournament = createFakeTournament(fakeUser());
        tournament.setStructure(Structure.LEAGUE);
        List<Participant> participants = participants(1L, 2L, 3L, 4L);

        Mockito.when(mockDao.findById(ID)).thenReturn(Optional.of(tournament));
        Mockito.when(participantDao.getTournamentParticipantUsers(ID)).thenReturn(participants);

        tournamentService.closeInscriptions(ID);

        Mockito.verify(matchDao, Mockito.times(6)).insertMatch(
                Mockito.anyLong(),
                Mockito.eq(ID),
                Mockito.anyLong(),
                Mockito.anyLong(),
                Mockito.anyInt(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull()
        );
        Mockito.verify(mockDao).closeInscriptions(ID);
        Mockito.verify(ms).sendListEmail(ID, participants);
    }

    @Test
    public void testCloseInscriptionsEliminationGeneratesBracket() {
        Tournament tournament = createFakeTournament(fakeUser());
        tournament.setStructure(Structure.ELIMINATION);
        List<Participant> participants = participants(1L, 2L, 3L, 4L);

        Mockito.when(mockDao.findById(ID)).thenReturn(Optional.of(tournament));
        Mockito.when(participantDao.getTournamentParticipantUsers(ID)).thenReturn(participants);
        Mockito.when(matchDao.getTournamentMaxStage(ID)).thenReturn(0);

        tournamentService.closeInscriptions(ID);

        Mockito.verify(matchDao, Mockito.times(3)).insertMatch(
                Mockito.anyLong(),
                Mockito.eq(ID),
                Mockito.any(),
                Mockito.any(),
                Mockito.anyInt(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull()
        );
        Mockito.verify(mockDao).closeInscriptions(ID);
    }

    @Test
    public void testCloseInscriptionsHybridWithFewParticipantsGeneratesDirectBracket() {
        Tournament tournament = createFakeTournament(fakeUser());
        tournament.setStructure(Structure.HYBRID);
        List<Participant> participants = participants(1L, 2L, 3L, 4L);

        Mockito.when(mockDao.findById(ID)).thenReturn(Optional.of(tournament));
        Mockito.when(participantDao.getTournamentParticipantUsers(ID)).thenReturn(participants);
        Mockito.when(matchDao.getTournamentMaxStage(ID)).thenReturn(0);

        tournamentService.closeInscriptions(ID);

        Mockito.verify(mockDao).setIsGroupStage(ID, false);
        Mockito.verify(matchDao, Mockito.times(3)).insertMatch(
                Mockito.anyLong(),
                Mockito.eq(ID),
                Mockito.any(),
                Mockito.any(),
                Mockito.anyInt(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.eq(false)
        );
    }

    @Test
    public void testCloseInscriptionsHybridWithEnoughParticipantsAssignsGroups() {
        Tournament tournament = createFakeTournament(fakeUser());
        tournament.setStructure(Structure.HYBRID);
        List<Participant> participants = participants(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);

        Mockito.when(mockDao.findById(ID)).thenReturn(Optional.of(tournament));
        Mockito.when(participantDao.getTournamentParticipantUsers(ID)).thenReturn(participants);

        tournamentService.closeInscriptions(ID);

        Mockito.verify(mockDao).setIsGroupStage(ID, true);
        Mockito.verify(participantDao, Mockito.times(3)).updateGroupNumberForUsers(
                Mockito.eq(ID),
                Mockito.anyInt(),
                Mockito.argThat(ids -> ids.size() == 3)
        );
        Mockito.verify(matchDao, Mockito.never()).insertMatch(
                Mockito.anyLong(),
                Mockito.anyLong(),
                Mockito.any(),
                Mockito.any(),
                Mockito.anyInt(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
        );
    }

    @Test
    public void testStartTournamentHybridWithGroupsGeneratesGroupMatches() {
        User user = fakeUser();
        Tournament tournament = createFakeTournament(user);
        tournament.setStructure(Structure.HYBRID);
        tournament.setOpenInscriptions(false);
        tournament.setIsGroupStage(true);
        tournament.setFormatEntity(new GameFormat(ID, NAME, 1));
        List<Participant> participants = participantsWithGroups(1, 1L, 2L, 3L);
        participants.addAll(participantsWithGroups(2, 4L, 5L, 6L));

        Mockito.when(mockDao.findById(ID)).thenReturn(Optional.of(tournament));
        Mockito.when(participantDao.getTournamentParticipantUsers(ID)).thenReturn(participants);
        Mockito.when(userDao.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        tournamentService.startTournament(ID);

        Mockito.verify(matchDao, Mockito.times(6)).insertMatch(
                Mockito.anyLong(),
                Mockito.eq(ID),
                Mockito.anyLong(),
                Mockito.anyLong(),
                Mockito.anyInt(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.eq(true)
        );
        Mockito.verify(mockDao).startTournament(ID);
    }

    @Test(expected = TournamentInscriptionsOpenException.class)
    public void testStartTournamentWithOpenInscriptionsFails() {
        Tournament tournament = createFakeTournament(fakeUser());
        tournament.setOpenInscriptions(true);

        Mockito.when(mockDao.findById(ID)).thenReturn(Optional.of(tournament));

        tournamentService.startTournament(ID);
    }

    @Test
    public void testSetFinishedLeagueTieGeneratesTiebreaker() {
        Tournament tournament = createFakeTournament(fakeUser());
        tournament.setStructure(Structure.LEAGUE);
        GameFormat format = new GameFormat(ID, NAME, 1);
        List<Participant> tiedParticipants = participants(1L, 2L);
        PointsPair pointsPair = new PointsPair(9, 0);

        Mockito.when(mockDao.findById(ID)).thenReturn(Optional.of(tournament));
        Mockito.when(participantDao.getTournamentMaxPointsPairGroup(ID, null)).thenReturn(pointsPair);
        Mockito.when(gameFormatDao.findById(ID)).thenReturn(format);
        Mockito.when(participantDao.getTournamentParticipantsByPointsPair(ID, null, pointsPair, 1)).thenReturn(tiedParticipants);
        Mockito.when(matchDao.getMaxMatchId(ID)).thenReturn(6L);
        Mockito.when(matchDao.getTournamentMaxStage(ID)).thenReturn(3);

        tournamentService.setFinished(ID, 6L);

        Mockito.verify(matchDao).insertMatch(7L, ID, 1L, 2L, 4, null, null, null, null);
        Mockito.verify(mockDao, Mockito.never()).setFinished(ID);
    }

    private Tournament createFakeTournament(User fakeUser){
        GameFormat fakeFormat = new GameFormat(ID,NAME,8);
        Game fakeGame = new Game(NAME,GENRE, (int) ID);
        fakeGame.setId(ID);
        Tournament fakeTournament = new Tournament(fakeUser,NAME,fakeGame,REGION,START_DATE,END_DATE,FORMAT,STRUCTURE,MAX_PARTICIPANTS,ID,true,false,fakeFormat,NAME,NAME,NAME);
        fakeTournament.setCreator(fakeUser);
        fakeTournament.setId(ID);
        fakeTournament.setElo(ELO);
        fakeTournament.setTournamentStarted(false);
        fakeTournament.setRules(new Rules(ID));
        return fakeTournament;
    }

    private User fakeUser() {
        return new User(ID, NAME, NAME, NAME, true, null, null, null, "");
    }

    private List<Participant> participants(Long... ids) {
        return java.util.Arrays.stream(ids)
                .map(id -> new Participant(id, "participant-" + id, 0, 0, null, null, false))
                .toList();
    }

    private List<Participant> participantsWithGroups(int group, Long... ids) {
        return java.util.Arrays.stream(ids)
                .map(id -> {
                    Participant participant = new Participant(id, "participant-" + id, 0, 0, group, null, false);
                    participant.setUser(new User(id, "user-" + id, "user-" + id + "@mail.com", "password", true, null, null, null, ""));
                    return participant;
                })
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
    }
}
