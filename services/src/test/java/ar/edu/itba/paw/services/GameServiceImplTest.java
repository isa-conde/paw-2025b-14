package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.GameFormatNotFoundException;
import ar.edu.itba.paw.interfaces.exception.NameAlreadyUsedException;
import ar.edu.itba.paw.interfaces.persistence.GameDao;
import ar.edu.itba.paw.interfaces.persistence.GameFormatDao;
import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.enums.Genre;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceImplTest {
    private static final String NAME = "Sumo Slammers";
    private static final Genre GENRE = Genre.Fighting;
    private static final byte[] IMAGE = "9".repeat(64).getBytes(StandardCharsets.UTF_8);
    private static final Long ID = 100L;

    @Mock
    private GameDao mockDao;

    @Mock
    private ImageDao imageDao;

    @Mock
    private GameFormatDao gameFormatDao;

    @InjectMocks
    private GameServiceImpl gameService;

    @Test
    public void testCreate(){
        Mockito.when(mockDao.create(Mockito.eq(NAME), Mockito.eq(GENRE), Mockito.eq(1))).thenReturn(new Game(1L, NAME, GENRE, 1));
        Mockito.when(mockDao.checkNameExists(NAME)).thenReturn(false);

        Game maybeGame = gameService.create(NAME, GENRE, 1);

        Assert.assertNotNull(maybeGame);
        Assert.assertEquals(NAME, maybeGame.getName());
        Assert.assertEquals(GENRE, maybeGame.getGenre());
        Assert.assertEquals(Integer.valueOf(1), maybeGame.getImageId());
        Assert.assertEquals(Long.valueOf(1), maybeGame.getId());
    }

    @Test(expected = NameAlreadyUsedException.class)
    public void testCreateNameExists(){
        Mockito.when(mockDao.checkNameExists(NAME)).thenReturn(true);

        gameService.create(NAME,GENRE,1);
    }

    @Test(expected = GameFormatNotFoundException.class)
    public void testFormatNotFound(){
        Mockito.when(gameFormatDao.findById(ID)).thenReturn(Optional.empty());

        gameService.findFormatById(ID);
    }
}
