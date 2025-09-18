package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.enums.Genre;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
public class GameJdbcDaoTest {
    private static final String NAME = "Sumo Slammers";
    private static final Genre GENRE = Genre.Fighting;

    @Autowired
    private DataSource ds;

    @Autowired
    private GameJdbcDao gameJdbcDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"game");
    }

    @Test
    public void testCreate(){
        final Game game = gameJdbcDao.create(NAME, GENRE, 1);

        Assert.assertNotNull(game);
        Assert.assertEquals(NAME, game.getName());
        Assert.assertEquals(GENRE, game.getGenre());
        Assert.assertEquals(Integer.valueOf(1), game.getImage_id());
        Assert.assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate,"game"));
    }

    @Test(expected = Exception.class)
    public void testCreateNameless(){
        final Game game = gameJdbcDao.create(null, GENRE, 1);
    }

    @Test
    public void testFindWithId(){
        final Game game = gameJdbcDao.create(NAME, GENRE, 1);
        Optional<Game> ans = gameJdbcDao.findById(game.getId());

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertEquals(game.getName(), ans.get().getName());
        Assert.assertEquals(game.getGenre(), ans.get().getGenre());
        Assert.assertEquals(game.getImage_id(), ans.get().getImage_id());
        Assert.assertEquals(game.getId(), ans.get().getId());
    }

    @Test
    public void testFindNonExistent(){
        Optional<Game> ans = gameJdbcDao.findById(3);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testFindWithName(){
        final Game game = gameJdbcDao.create(NAME, GENRE, 1);
        Optional<Game> ans = gameJdbcDao.findByName(NAME);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertEquals(NAME, ans.get().getName());
        Assert.assertEquals(game.getGenre(), ans.get().getGenre());
        Assert.assertEquals(game.getImage_id(), ans.get().getImage_id());
        Assert.assertEquals(game.getId(), ans.get().getId());
    }

    @Test
    public void testFindNameless(){
        Optional<Game> ans = gameJdbcDao.findByName(NAME);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testFindAll(){
        List<Game> expected=new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            for(Genre genre : Genre.values()){
                expected.add(gameJdbcDao.create(NAME + " " + i + " " + genre, genre, 1));
            }
        }

        List<Game> games = gameJdbcDao.findAll();

        Assert.assertNotNull(games);
        Assert.assertFalse(games.isEmpty());
        Assert.assertEquals(expected.size(), games.size());
        for( int i=0; i<10; i++ ){
            for(Genre genre : Genre.values()){
                Assert.assertNotNull(games.get(i));
                Assert.assertEquals(expected.get(i).getName(), games.get(i).getName());
                Assert.assertEquals(expected.get(i).getGenre(), games.get(i).getGenre());
                Assert.assertEquals(Integer.valueOf(1),games.get(i).getImage_id());
                Assert.assertEquals(expected.get(i).getId(),games.get(i).getId());
            }
        }
    }

    @Test
    public void testFindNothing(){
        List<Game> games = gameJdbcDao.findAll();

        Assert.assertNotNull(games);
        Assert.assertTrue(games.isEmpty());
    }

//    @Test
//    public void testSearchByName(){
//        List<Game> expected = new ArrayList<>();
//        for( int i=1; i <= 5; i++){
//            for(Genre genre : Genre.values()){
//                expected.add(gameJdbcDao.create(genre + " " + NAME + " " + i, genre, 1));
//            }
//        }
//        String[] otherGames = {"Grand Theft Walrus", "F-MEGA", "Chimpokomon", "Lee Carvallo's Putting Challenge", "Bonestorm"};
//        for(String name : otherGames){
//            for(Genre genre : Genre.values()){
//                gameJdbcDao.create(genre + " " + name, genre, 1);
//            }
//        }
//
//        List<Game> games = gameJdbcDao.searchByName(NAME);
//
//        Assert.assertNotNull(games);
//        Assert.assertFalse(games.isEmpty());
//        Assert.assertEquals(expected.size(), games.size());
//        for(int i = 0; i < 5; i++){
//            for(Genre genre : Genre.values()){
//                Assert.assertNotNull(games.get(i));
//                Assert.assertEquals(expected.get(i).getName(), games.get(i).getName());
//                Assert.assertEquals(expected.get(i).getGenre(), games.get(i).getGenre());
//                Assert.assertEquals(Integer.valueOf(1),games.get(i).getImage_id());
//                Assert.assertEquals(expected.get(i).getId(),games.get(i).getId());
//            }
//        }
//    }
//
//    @Test
//    public void testSearchByNameNoOne(){
//        String[] otherGames = {"Grand Theft Walrus", "F-MEGA", "Chimpokomon", "Lee Carvallo's Putting Challenge", "Bonestorm"};
//        for(String name : otherGames){
//            for(Genre genre : Genre.values()){
//                gameJdbcDao.create(genre + " " + name, genre, 1);
//            }
//        }
//
//        List<Game> games = gameJdbcDao.searchByName(NAME);
//
//        Assert.assertNotNull(games);
//        Assert.assertTrue(games.isEmpty());
//    }
//
//    @Test
//    public void testSearchNameless(){
//        List<Game> expected=new ArrayList<>();
//        for (int i = 1; i <= 10; i++) {
//            for(Genre genre : Genre.values()){
//                expected.add(gameJdbcDao.create(NAME + " " + i + " " + genre, genre, 1));
//            }
//        }
//
//        List<Game> games = gameJdbcDao.searchByName("");
//
//        Assert.assertNotNull(games);
//        Assert.assertFalse(games.isEmpty());
//        Assert.assertEquals(expected.size(), games.size());
//        for( int i=0; i<10; i++ ){
//            for(Genre genre : Genre.values()){
//                Assert.assertNotNull(games.get(i));
//                Assert.assertEquals(expected.get(i).getName(), games.get(i).getName());
//                Assert.assertEquals(expected.get(i).getGenre(), games.get(i).getGenre());
//                Assert.assertEquals(Integer.valueOf(1),games.get(i).getImage_id());
//                Assert.assertEquals(expected.get(i).getId(),games.get(i).getId());
//            }
//        }
//    }

    @Test
    public void testSearchByGenre(){
        List<Game> expected = new ArrayList<>();
        for( int i=1; i <= 5; i++){
            for(Genre genre : Genre.values()){
                if(genre == GENRE){
                    expected.add(gameJdbcDao.create(genre + " " + NAME + " " + i, genre, 1));
                }else{
                    gameJdbcDao.create(genre + " " + NAME + " " + i, genre, 1);
                }
            }
        }
        String[] otherGames = {"Grand Theft Walrus", "F-MEGA", "Chimpokomon", "Lee Carvallo's Putting Challenge", "Bonestorm"};
        for(String name : otherGames){
            for(Genre genre : Genre.values()){
                if(genre == GENRE){
                    expected.add(gameJdbcDao.create(genre + " " + name, genre, 1));
                }else{
                    gameJdbcDao.create(genre + " " + name, genre, 1);
                }
            }
        }

        List<Game> games = gameJdbcDao.searchByGenre(GENRE);

        Assert.assertNotNull(games);
        Assert.assertFalse(games.isEmpty());
        Assert.assertEquals(expected.size(), games.size());
        for(int i = 0; i < 5; i++){
            for(Genre genre : Genre.values()){
                Assert.assertNotNull(games.get(i));
                Assert.assertEquals(expected.get(i).getName(), games.get(i).getName());
                Assert.assertEquals(expected.get(i).getGenre(), games.get(i).getGenre());
                Assert.assertEquals(Integer.valueOf(1),games.get(i).getImage_id());
                Assert.assertEquals(expected.get(i).getId(),games.get(i).getId());
            }
        }
    }

    @Test
    public void testSearchByGenreNothing(){
        for(int i = 1; i <= 10; i++){
            for(Genre genre : Genre.values()){
                if(genre != GENRE)
                    gameJdbcDao.create(genre + " " + NAME + " " + i, genre, 1);
            }
        }

        List<Game> games = gameJdbcDao.searchByGenre(GENRE);

        Assert.assertNotNull(games);
        Assert.assertTrue(games.isEmpty());
    }
}
