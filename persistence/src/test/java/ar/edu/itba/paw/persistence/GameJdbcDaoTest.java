package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.enums.Genre;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class GameJdbcDaoTest {
    private static final String NAME = "Sumo Slammers";
    private static final String[] OTHER_NAMES = {"Grand Theft Walrus", "F-MEGA", "Chimpokomon", "Lee Carvallo's Putting Challenge", "Bonestorm"};
    private static final Genre GENRE = Genre.MOBA;
    private List<Long> USED_IDS;
    private int rows;
    private static final RowMapper<Game> ROW_MAPPER = (rs, rowNum) -> new Game(rs.getLong("id"), rs.getString("name"), Genre.valueOf(rs.getString("genre")), rs.getInt("image_id"));

    @Autowired
    private DataSource ds;

    @Autowired
    private GameJdbcDao gameJdbcDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .usingGeneratedKeyColumns("id")
                .withTableName("game");
        USED_IDS = new ArrayList<>();
        Map<String,Object> values;
        for (Genre genre : Genre.values()){
            for (String otherName : OTHER_NAMES) {
                values = Map.of("name", otherName + " " + genre, "genre", genre, "image_id", 1);
                USED_IDS.add(jdbcInsert.executeAndReturnKey(values).longValue());
            }
        }
        rows = JdbcTestUtils.countRowsInTable(jdbcTemplate,"game");
    }

    @Test
    public void testCreate(){
        final Game game = gameJdbcDao.create(NAME, GENRE, 1);
        final List<Game> inserted = jdbcTemplate.query("select * from game where id = ?", ROW_MAPPER, game.getId());

        Assert.assertNotNull(inserted);
        Assert.assertFalse(inserted.isEmpty());
        Assert.assertEquals(1,inserted.size());
        Assert.assertNotNull(game);
        Assert.assertEquals(NAME, inserted.get(0).getName());
        Assert.assertEquals(NAME, game.getName());
        Assert.assertEquals(GENRE, inserted.get(0).getGenre());
        Assert.assertEquals(GENRE, game.getGenre());
        Assert.assertEquals(Integer.valueOf(1), inserted.get(0).getImage_id());
        Assert.assertEquals(Integer.valueOf(1), game.getImage_id());
        Assert.assertEquals(rows + 1,JdbcTestUtils.countRowsInTable(jdbcTemplate,"game"));
    }

    @Test(expected = Exception.class)
    public void testCreateNameless(){
        gameJdbcDao.create(null, GENRE, 1);
    }

    @Test(expected = Exception.class)
    public void testCreateGenreless(){
        gameJdbcDao.create(NAME, null, 1);
    }

    @Test
    public void testCheckNameExists(){
        boolean ans = gameJdbcDao.checkNameExists(OTHER_NAMES[0] + " " + GENRE);

        Assert.assertTrue(ans);
    }

    @Test
    public void testCheckNameDoesntExist(){
        boolean ans = gameJdbcDao.checkNameExists(NAME);

        Assert.assertFalse(ans);
    }

    @Test
    public void testFindWithId(){
        Optional<Game> ans = gameJdbcDao.findById(USED_IDS.get(0));

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertEquals(OTHER_NAMES[0] + " " + GENRE, ans.get().getName());
        Assert.assertEquals(GENRE, ans.get().getGenre());
        Assert.assertEquals(Integer.valueOf(1), ans.get().getImage_id());
        Assert.assertEquals(USED_IDS.get(0), ans.get().getId());
    }

    @Test
    public void testFindNonExistent(){
        Optional<Game> ans = gameJdbcDao.findById(-1);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testFindWithName(){
        Optional<Game> ans = gameJdbcDao.findByName(OTHER_NAMES[0] + " " + GENRE);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertEquals(OTHER_NAMES[0] + " " + GENRE, ans.get().getName());
        Assert.assertEquals(GENRE, ans.get().getGenre());
        Assert.assertEquals(Integer.valueOf(1), ans.get().getImage_id());
        Assert.assertEquals(USED_IDS.get(0), ans.get().getId());
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
        int i = 0;
        for (Genre genre : Genre.values()){
            for (String otherName : OTHER_NAMES) {
                expected.add(new Game(USED_IDS.get(i++),otherName + " " + genre, genre, 1));
            }
        }

        List<Game> games = gameJdbcDao.findAll();

        Assert.assertNotNull(games);
        Assert.assertFalse(games.isEmpty());
        Assert.assertEquals(expected.size(), games.size());
        for( i-- ; i>=0; i-- ){
            Assert.assertNotNull(games.get(i));
            Assert.assertEquals(expected.get(i).getName(), games.get(i).getName());
            Assert.assertEquals(expected.get(i).getGenre(), games.get(i).getGenre());
            Assert.assertEquals(Integer.valueOf(1),games.get(i).getImage_id());
            Assert.assertEquals(expected.get(i).getId(),games.get(i).getId());
        }
    }

    @Test
    public void testFindNothing(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"game");
        List<Game> games = gameJdbcDao.findAll();

        Assert.assertNotNull(games);
        Assert.assertTrue(games.isEmpty());
    }

    @Test
    public void testSearchByGenre(){
        List<Game> expected = new ArrayList<>();
        int j=0;
        for(String otherNames : OTHER_NAMES){
            expected.add(new Game(USED_IDS.get(j++), otherNames + " " + GENRE, GENRE, 1));
        }

        List<Game> games = gameJdbcDao.searchByGenre(GENRE);

        Assert.assertNotNull(games);
        Assert.assertFalse(games.isEmpty());
        Assert.assertEquals(expected.size(), games.size());
        for(j--; j >=0; j--){
            Assert.assertNotNull(games.get(j));
            Assert.assertEquals(expected.get(j).getName(), games.get(j).getName());
            Assert.assertEquals(expected.get(j).getGenre(), games.get(j).getGenre());
            Assert.assertEquals(Integer.valueOf(1),games.get(j).getImage_id());
            Assert.assertEquals(expected.get(j).getId(),games.get(j).getId());
        }
    }

    @Test
    public void testSearchByGenreNothing(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"game");
        List<Game> games = gameJdbcDao.searchByGenre(GENRE);

        Assert.assertNotNull(games);
        Assert.assertTrue(games.isEmpty());
    }

    @Test
    public void testSearchByName(){
        List<Game> expected = new ArrayList<>();
        int j=0;
        for(Genre genre : Genre.values()){
            expected.add(new Game(USED_IDS.get(j),OTHER_NAMES[0] + " " + genre, genre, 1));
            j+=OTHER_NAMES.length;
        }

        List<Game> games = gameJdbcDao.searchByName(OTHER_NAMES[0]);

        Assert.assertNotNull(games);
        Assert.assertFalse(games.isEmpty());
        Assert.assertEquals(expected.size(), games.size());
        for(int i=0; i < expected.size(); i++){
            Assert.assertNotNull(games.get(i));
            Assert.assertEquals(expected.get(i).getName(), games.get(i).getName());
            Assert.assertEquals(expected.get(i).getGenre(), games.get(i).getGenre());
            Assert.assertEquals(Integer.valueOf(1),games.get(i).getImage_id());
            Assert.assertEquals(expected.get(i).getId(),games.get(i).getId());
        }
    }

    @Test
    public void testSearchByNameNoOne(){
        List<Game> games = gameJdbcDao.searchByName(NAME);

        Assert.assertNotNull(games);
        Assert.assertTrue(games.isEmpty());
    }

    @Test
    public void testSearchNameless(){
        List<Game> expected=new ArrayList<>();
        int i = 0;
        for (Genre genre : Genre.values()){
            for (String otherName : OTHER_NAMES) {
                expected.add(new Game(USED_IDS.get(i++),otherName + " " + genre, genre, 1));
            }
        }

        List<Game> games = gameJdbcDao.searchByName("");

        Assert.assertNotNull(games);
        Assert.assertFalse(games.isEmpty());
        Assert.assertEquals(expected.size(), games.size());
        for( i-- ; i>=0; i-- ){
            Assert.assertNotNull(games.get(i));
            Assert.assertEquals(expected.get(i).getName(), games.get(i).getName());
            Assert.assertEquals(expected.get(i).getGenre(), games.get(i).getGenre());
            Assert.assertEquals(Integer.valueOf(1),games.get(i).getImage_id());
            Assert.assertEquals(expected.get(i).getId(),games.get(i).getId());
        }
    }

    @Test
    public void testGetFormats(){
        SimpleJdbcInsert formatJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .usingGeneratedKeyColumns("id")
                .withTableName("game_format");
        for (int i = 0; i < OTHER_NAMES.length; i++) {
            formatJdbcInsert.executeAndReturnKey(Map.of("name", NAME, "players_per_team", i, "game_id", 1));
        }
        List<GameFormat> expected = new ArrayList<>();
        for (int i = 0; i < OTHER_NAMES.length; i++) {
            expected.add(new GameFormat((long) i, NAME, i, 1L));
        }

        List<GameFormat> formats = gameJdbcDao.getFormats(1L);

        Assert.assertNotNull(formats);
        Assert.assertFalse(formats.isEmpty());
        Assert.assertEquals(expected.size(), formats.size());
        for (int i = 0; i < OTHER_NAMES.length; i++) {
            Assert.assertNotNull(formats.get(i));
            Assert.assertEquals(expected.get(i).getGame_id(), formats.get(i).getGame_id());
            Assert.assertEquals(expected.get(i).getId(), formats.get(i).getId());
            Assert.assertEquals(expected.get(i).getName(), formats.get(i).getName());
            Assert.assertEquals(expected.get(i).getPlayers_per_team(), formats.get(i).getPlayers_per_team());
        }
    }

    @Test
    public void testAddFavourites(){
        gameJdbcDao.addFavourite(1L, USED_IDS.get(0));

        Long ans = jdbcTemplate.queryForObject("select user_id from user_favourites where game_id = ?",Long.class,USED_IDS.get(0));

        Assert.assertNotNull(ans);
        Assert.assertEquals(Long.valueOf(1), ans);
    }

    @Test
    public void testGetFavourites(){
        SimpleJdbcInsert favouritesJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("user_favourites");
        for (Long game : USED_IDS) {
            favouritesJdbcInsert.execute(Map.of("user_id",1L,"game_id", game));
        }

        List<Game> favourites = gameJdbcDao.getFavourites(1L);

        Assert.assertNotNull(favourites);
        Assert.assertFalse(favourites.isEmpty());
        Assert.assertEquals(USED_IDS.size(), favourites.size());
        for (int i = 0; i < USED_IDS.size(); i++){
            Assert.assertNotNull(favourites.get(i));
            Assert.assertEquals(USED_IDS.get(i), favourites.get(i).getId());
        }
    }

    @Test
    public void testGetPlayersPerTeam(){
        SimpleJdbcInsert formatJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .usingGeneratedKeyColumns("id")
                .withTableName("game_format");
        Long id = formatJdbcInsert.executeAndReturnKey(Map.of("name", NAME, "players_per_team", 1, "game_id", 1)).longValue();
        Integer ans = gameJdbcDao.getPlayersPerTeam(id);

        Assert.assertNotNull(ans);
        Assert.assertEquals(1,ans.intValue());
    }
}