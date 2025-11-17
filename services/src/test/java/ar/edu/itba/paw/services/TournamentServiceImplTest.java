package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
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
    private GameDao gameDao;

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
}
