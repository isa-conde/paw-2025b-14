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
}
