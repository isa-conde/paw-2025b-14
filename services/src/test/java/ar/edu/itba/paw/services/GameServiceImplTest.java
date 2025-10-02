package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.NameAlreadyUsedException;
import ar.edu.itba.paw.interfaces.persistence.GameDao;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.enums.Genre;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceImplTest {
    private static final String NAME = "Sumo Slammers";
    private static final Genre GENRE = Genre.Fighting;

    @Mock
    private GameDao mockDao;

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
        Assert.assertEquals(Integer.valueOf(1), maybeGame.getImage_id());
        Assert.assertEquals(Long.valueOf(1), maybeGame.getId());
    }

    @Test(expected = NameAlreadyUsedException.class)
    public void testCreateNameExists(){
        Mockito.when(mockDao.checkNameExists(NAME)).thenReturn(true);

        gameService.create(NAME,GENRE,1);
    }

    @Test
    public void testFindById(){
        Mockito.when(mockDao.findById(1)).thenReturn(Optional.of(new Game(1L, NAME,GENRE,1)) );

        Optional<Game> maybeGame = gameService.findById(1);

        Assert.assertNotNull(maybeGame);
        Assert.assertTrue(maybeGame.isPresent());
        Assert.assertEquals(NAME, maybeGame.get().getName());
        Assert.assertEquals(GENRE, maybeGame.get().getGenre());
        Assert.assertEquals(Integer.valueOf(1), maybeGame.get().getImage_id());
        Assert.assertEquals(Long.valueOf(1), maybeGame.get().getId());
    }

    @Test
    public void testFindByIdNothing(){
        Mockito.when(mockDao.findById(1)).thenReturn(Optional.empty() );

        Optional<Game> maybeGame = gameService.findById(1);

        Assert.assertNotNull(maybeGame);
        Assert.assertTrue(maybeGame.isEmpty());
    }

    @Test
    public void testFindByName(){
        Mockito.when(mockDao.findByName(NAME)).thenReturn(Optional.of(new Game(1L, NAME,GENRE,1)) );

        Optional<Game> maybeGame = gameService.findByName(NAME);

        Assert.assertNotNull(maybeGame);
        Assert.assertTrue(maybeGame.isPresent());
        Assert.assertEquals(NAME, maybeGame.get().getName());
        Assert.assertEquals(GENRE, maybeGame.get().getGenre());
        Assert.assertEquals(Integer.valueOf(1), maybeGame.get().getImage_id());
        Assert.assertEquals(Long.valueOf(1), maybeGame.get().getId());
    }

    @Test
    public void testFindByNameNothing(){
        Mockito.when(mockDao.findByName(NAME)).thenReturn(Optional.empty() );

        Optional<Game> maybeGame = gameService.findByName(NAME);

        Assert.assertNotNull(maybeGame);
        Assert.assertTrue(maybeGame.isEmpty());
    }

    @Test
    public void testFindNothing(){
        Mockito.when(mockDao.findAll()).thenReturn(new ArrayList<>());

        List<Game> games = gameService.findAll();

        Assert.assertNotNull(games);
        Assert.assertTrue(games.isEmpty());
    }

    @Test
    public void testSearchByUnusedGenre(){
        Mockito.when(mockDao.searchByGenre(GENRE)).thenReturn(new ArrayList<>());

        List<Game> games = gameService.searchByGenre(GENRE);

        Assert.assertNotNull(games);
        Assert.assertTrue(games.isEmpty());
    }

//    @Test
//    public void testSearchByUnusedName(){
//        Mockito.when(mockDao.searchByName(NAME)).thenReturn(new ArrayList<>());
//
//        List<Game> games = gameService.searchByName(NAME);
//
//        Assert.assertNotNull(games);
//        Assert.assertTrue(games.isEmpty());
//    }

    @Test
    public void testFindAll(){
        String[] gameNames = {NAME, "Grand Theft Walrus", "F-MEGA", "Chimpokomon", "Lee Carvallo's Putting Challenge", "Bonestorm"};
        List<Game> expected = new ArrayList<>();
        long idCounter = 1;
        for ( String name : gameNames){
            for( Genre genre : Genre.values()){
                expected.add(new Game(idCounter++, genre + " " + name, genre, 1));
            }
        }
        Mockito.when(mockDao.findAll()).thenReturn(expected);

        List<Game> games = gameService.findAll();

        Assert.assertNotNull(games);
        Assert.assertEquals(expected.size(), games.size());
        for(int i=0; i<games.size(); i++){
            Assert.assertEquals(expected.get(i).getName(), games.get(i).getName());
            Assert.assertEquals(expected.get(i).getGenre(), games.get(i).getGenre());
            Assert.assertEquals(expected.get(i).getImage_id(), games.get(i).getImage_id());
            Assert.assertEquals(expected.get(i).getId(), games.get(i).getId());
        }
    }

//    @Test
//    public void testSearchByName(){
//        List<Game> expected = new ArrayList<>();
//        long idCounter = 1;
//        for ( int i=1; i<=5; i++){
//            for( Genre genre : Genre.values()){
//                expected.add(new Game(idCounter++, genre + " " + NAME + " " + i, genre, 1));
//            }
//        }
//        Mockito.when(mockDao.searchByName(NAME)).thenReturn(expected);
//
//        List<Game> games = gameService.searchByName(NAME);
//
//        Assert.assertNotNull(games);
//        Assert.assertEquals(expected.size(), games.size());
//        for(int i=0; i<games.size(); i++){
//            Assert.assertEquals(expected.get(i).getName(), games.get(i).getName());
//            Assert.assertEquals(expected.get(i).getGenre(), games.get(i).getGenre());
//            Assert.assertEquals(expected.get(i).getImage_id(), games.get(i).getImage_id());
//            Assert.assertEquals(expected.get(i).getId(), games.get(i).getId());
//        }
//    }

    @Test
    public void testSearchByGenre(){
        String[] gameNames = {NAME, "Grand Theft Walrus", "F-MEGA", "Chimpokomon", "Lee Carvallo's Putting Challenge", "Bonestorm"};
        List<Game> expected = new ArrayList<>();
        long idCounter = 1;
        for ( String name : gameNames){
            expected.add(new Game(idCounter++, GENRE + " " + name, GENRE, 1));
        }
        Mockito.when(mockDao.searchByGenre(GENRE)).thenReturn(expected);

        List<Game> games = gameService.searchByGenre(GENRE);

        Assert.assertNotNull(games);
        Assert.assertEquals(expected.size(), games.size());
        for(int i=0; i<games.size(); i++){
            Assert.assertEquals(expected.get(i).getName(), games.get(i).getName());
            Assert.assertEquals(expected.get(i).getGenre(), games.get(i).getGenre());
            Assert.assertEquals(expected.get(i).getImage_id(), games.get(i).getImage_id());
            Assert.assertEquals(expected.get(i).getId(), games.get(i).getId());
        }
    }
}
