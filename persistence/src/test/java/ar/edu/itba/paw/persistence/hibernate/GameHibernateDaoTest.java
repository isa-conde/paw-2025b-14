package ar.edu.itba.paw.persistence.hibernate;

import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.persistence.Hibernate.GameHibernateDao;
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
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class GameHibernateDaoTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource ds;

    @Autowired
    private GameHibernateDao gameHibernateDao;

    private JdbcTemplate jdbcTemplate;

    private static final String NAME = "Sumo Slammers";
    private static final String[] OTHER_NAMES = {"Grand Theft Walrus", "F-MEGA", "Chimpokomon", "Lee Carvallo's Putting Challenge", "Bonestorm"};
    private static final Genre GENRE = Genre.MOBA;
    private final Long firstUsedId = 100L;
    private static final int GRID_PAGE_SIZE = 9;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate(){
        final Game game = gameHibernateDao.create(NAME, GENRE, 1);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"game","name = '" + NAME + "' and genre = '" + GENRE + "' and image_id = 1"));
        Assert.assertNotNull(game);
        Assert.assertEquals(NAME, game.getName());
        Assert.assertEquals(GENRE, game.getGenre());
        Assert.assertEquals(Integer.valueOf(1), game.getImageId());
    }

    @Test(expected = Exception.class)
    public void testCreateNameless(){
        gameHibernateDao.create(null, GENRE, 1);
    }

    @Test
    public void testCheckNameExists(){
        boolean ans = gameHibernateDao.checkNameExists(OTHER_NAMES[0] + " " + GENRE);

        Assert.assertTrue(ans);
    }

    @Test
    public void testCheckNameDoesntExist(){
        boolean ans = gameHibernateDao.checkNameExists(NAME);

        Assert.assertFalse(ans);
    }

    @Test
    public void testFindWithId(){
        Optional<Game> ans = gameHibernateDao.findById(firstUsedId);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertEquals(OTHER_NAMES[0] + " " + GENRE, ans.get().getName());
        Assert.assertEquals(GENRE, ans.get().getGenre());
        Assert.assertEquals(firstUsedId.intValue(), ans.get().getImageId().intValue());
        Assert.assertEquals(firstUsedId, ans.get().getId());
    }

    @Test
    public void testFindNonExistent(){
        Optional<Game> ans = gameHibernateDao.findById(1);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testFindAll(){
        List<Game> expected=new ArrayList<>();
        int i = 0;
        for (Genre genre : Genre.values()){
            for (int k=0; k<OTHER_NAMES.length; k++) {
                expected.add(new Game(firstUsedId + k*10 + i,OTHER_NAMES[k] + " " + genre, genre, firstUsedId.intValue()));
            }
            i++;
        }

        List<Game> games = gameHibernateDao.findAll();

        Assert.assertNotNull(games);
        Assert.assertFalse(games.isEmpty());
        Assert.assertEquals(expected.size(), games.size());
        Comparator<Game> cmp = (a,b)-> Math.toIntExact((a.getId() - b.getId()));
        expected.sort(cmp);
        games.sort(cmp);
        for( int j = 0 ; j<expected.size(); j++ ){
            Assert.assertNotNull(games.get(j));
            Assert.assertEquals(expected.get(j).getName(), games.get(j).getName());
            Assert.assertEquals(expected.get(j).getGenre(), games.get(j).getGenre());
            Assert.assertEquals(Integer.valueOf(firstUsedId.intValue()),games.get(j).getImageId());
            Assert.assertEquals(expected.get(j).getId(),games.get(j).getId());
        }
    }

    @Rollback
    @Test
    public void testFindNothing(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"game");
        List<Game> games = gameHibernateDao.findAll();

        Assert.assertNotNull(games);
        Assert.assertTrue(games.isEmpty());
    }

    @Test
    public void testSearchByGenre(){
        List<Game> expected = new ArrayList<>();
        for(int i = 0; i<OTHER_NAMES.length; i++){
            expected.add(new Game(firstUsedId + i*10, OTHER_NAMES[i] + " " + GENRE, GENRE, 1));
        }

        List<Game> games = gameHibernateDao.searchByGenre(GENRE);

        Assert.assertNotNull(games);
        Assert.assertFalse(games.isEmpty());
        Assert.assertEquals(expected.size(), games.size());
        for(int j = 0; j < expected.size(); j++){
            Assert.assertNotNull(games.get(j));
            Assert.assertEquals(expected.get(j).getName(), games.get(j).getName());
            Assert.assertEquals(expected.get(j).getGenre(), games.get(j).getGenre());
            Assert.assertEquals(Integer.valueOf(firstUsedId.intValue()),games.get(j).getImageId());
            Assert.assertEquals(expected.get(j).getId(),games.get(j).getId());
        }
    }

    @Rollback
    @Test
    public void testSearchByGenreNothing(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"game");
        List<Game> games = gameHibernateDao.searchByGenre(GENRE);

        Assert.assertNotNull(games);
        Assert.assertTrue(games.isEmpty());
    }

    @Test
    public void testSearchByName(){
        List<Game> expected = new ArrayList<>();
        long j=0;
        for(Genre genre : Genre.values()){
            expected.add(new Game(firstUsedId + j,OTHER_NAMES[0] + " " + genre, genre, 1));
            j++;
        }

        List<Game> games = gameHibernateDao.searchByName(OTHER_NAMES[0]);

        Assert.assertNotNull(games);
        Assert.assertFalse(games.isEmpty());
        Assert.assertEquals(expected.size(), games.size());
        Comparator<Game> cmp = (a,b)-> Math.toIntExact((a.getId() - b.getId()));
        expected.sort(cmp);
        games.sort(cmp);
        for(int i=0; i < expected.size(); i++){
            Assert.assertNotNull(games.get(i));
            Assert.assertEquals(expected.get(i).getName(), games.get(i).getName());
            Assert.assertEquals(expected.get(i).getGenre(), games.get(i).getGenre());
            Assert.assertEquals(Integer.valueOf(firstUsedId.intValue()),games.get(i).getImageId());
            Assert.assertEquals(expected.get(i).getId(),games.get(i).getId());
        }
    }

    @Rollback
    @Test
    public void testSearchByNameNoOne(){
        List<Game> games = gameHibernateDao.searchByName(NAME);

        Assert.assertNotNull(games);
        Assert.assertTrue(games.isEmpty());
    }

    @Test
    public void testSearchNameless(){
        List<Game> expected=new ArrayList<>();
        int i=0;
        for (Genre genre : Genre.values()){
            for (int k=0; k<OTHER_NAMES.length; k++) {
                expected.add(new Game(firstUsedId + k*10 + i,OTHER_NAMES[k] + " " + genre, genre, firstUsedId.intValue()));
            }
            i++;
        }

        List<Game> games = gameHibernateDao.searchByName("");

        Assert.assertNotNull(games);
        Assert.assertFalse(games.isEmpty());
        Assert.assertEquals(expected.size(), games.size());
        Comparator<Game> cmp = (a,b)-> Math.toIntExact((a.getId() - b.getId()));
        expected.sort(cmp);
        games.sort(cmp);
        for( int j = 0 ; j<expected.size(); j++ ){
            Assert.assertNotNull(games.get(j));
            Assert.assertEquals(expected.get(j).getName(), games.get(j).getName());
            Assert.assertEquals(expected.get(j).getGenre(), games.get(j).getGenre());
            Assert.assertEquals(Integer.valueOf(firstUsedId.intValue()),games.get(j).getImageId());
            Assert.assertEquals(expected.get(j).getId(),games.get(j).getId());
        }
    }

    @Test
    public void testGetFavourites(){
        List<Game> favourites = gameHibernateDao.getFavourites(firstUsedId);

        Assert.assertNotNull(favourites);
        Assert.assertFalse(favourites.isEmpty());
        Assert.assertEquals(1, favourites.size());
        Assert.assertNotNull(favourites.get(0));
        Assert.assertEquals(firstUsedId, favourites.get(0).getId());
    }

    @Test
    public void testGetPageAmount(){
        long expected = (long) Math.ceil((double) (OTHER_NAMES.length * Genre.values().length) /GRID_PAGE_SIZE);

        Long ans = gameHibernateDao.getPageAmount();

        Assert.assertEquals(expected,ans.longValue());
    }

    @Test
    public void testFindAllPaged(){
        List<Game> expected = new ArrayList<>();
        long j=0;
        for(Genre genre : Genre.values()){
            expected.add(new Game(firstUsedId + j,OTHER_NAMES[0] + " " + genre, genre, 1));
            j++;
        }

        List<Game> games = gameHibernateDao.findAllPaged(0L);

        Assert.assertNotNull(games);
        Assert.assertFalse(games.isEmpty());
        Assert.assertEquals(expected.size(), games.size());
        Comparator<Game> cmp = (a,b)-> Math.toIntExact((a.getId() - b.getId()));
        expected.sort(cmp);
        games.sort(cmp);
        for(int i=0; i < GRID_PAGE_SIZE; i++){
            Assert.assertNotNull(games.get(i));
            Assert.assertEquals(expected.get(i).getName(), games.get(i).getName());
            Assert.assertEquals(expected.get(i).getGenre(), games.get(i).getGenre());
            Assert.assertEquals(Integer.valueOf(firstUsedId.intValue()),games.get(i).getImageId());
            Assert.assertEquals(expected.get(i).getId(),games.get(i).getId());
        }
    }

    @Test
    public void testFindEmptyPage(){
        List<Game> games = gameHibernateDao.findAllPaged(100L);

        Assert.assertTrue(games.isEmpty());
    }
}
