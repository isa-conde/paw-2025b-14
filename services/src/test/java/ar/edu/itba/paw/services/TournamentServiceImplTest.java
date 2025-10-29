package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Tournament.Tournament;
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
import java.util.ArrayList;
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
    private static final Long ID = 1L;
    private static final Long OTHER_ID = 2L;
    private static final Integer MAX_PARTICIPANTS = 8;
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

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    @Test
    public void testCreate(){
        User fakeUser = new User(ID,NAME,NAME,NAME,true,null,null,null,"");
        Tournament fakeTournament = createFakeTournament(fakeUser);
        Mockito.when(imageDao.insertImage(IMAGE)).thenReturn(ID);
        Mockito.when(mockDao.create(ID,NAME,ID,REGION,ELO,START_DATE,END_DATE,FORMAT,STRUCTURE,MAX_PARTICIPANTS,ID,true,false,ID, null))
                .thenReturn(fakeTournament);
        Mockito.when(userDao.findById(ID))
                .thenReturn(Optional.of(fakeUser));

        Tournament ans = tournamentService.create(ID,NAME,ID,REGION,ELO,START_DATE,END_DATE,FORMAT,STRUCTURE,MAX_PARTICIPANTS,IMAGE,true,false,ID, null);

        Assert.assertNotNull(ans);
        Assert.assertEquals(ID,ans.getId());
        Assert.assertEquals(ID,ans.getCreator_id());
        Assert.assertEquals(NAME,ans.getName());
        Assert.assertEquals(ID,ans.getGame_id());
        Assert.assertEquals(REGION,ans.getRegion());
        Assert.assertEquals(ELO,ans.getElo());
        Assert.assertEquals(START_DATE,ans.getStart_date());
        Assert.assertEquals(END_DATE,ans.getEnd_date());
        Assert.assertEquals(FORMAT,ans.getFormat());
        Assert.assertEquals(STRUCTURE,ans.getStructure());
        Assert.assertEquals(MAX_PARTICIPANTS,ans.getMax_participants());
        Assert.assertEquals(ID,ans.getImage_id());
        Assert.assertTrue(ans.getOpenInscriptions());
        Assert.assertFalse(ans.getFinished());
        Assert.assertNull(ans.getTournament_winner());
        Assert.assertNull(ans.getIs_group_stage());
        Assert.assertFalse(ans.getTournamentStarted());
        Assert.assertEquals(ID,ans.getFormat_id());
    }

    private Tournament createFakeTournament(User fakeUser){
        GameFormat fakeFormat = new GameFormat(ID,NAME,8);
        Game fakeGame = new Game(NAME,GENRE,ID.intValue());
        fakeGame.setId(ID);
        Tournament fakeTournament = new Tournament(fakeUser,NAME,fakeGame,REGION,START_DATE,END_DATE,FORMAT,STRUCTURE,MAX_PARTICIPANTS,ID,true,false,fakeFormat);
        fakeTournament.setCreator(fakeUser);
        fakeTournament.setId(ID);
        fakeTournament.setElo(ELO);
        fakeTournament.setTournament_started(false);
        return fakeTournament;
    }
}
