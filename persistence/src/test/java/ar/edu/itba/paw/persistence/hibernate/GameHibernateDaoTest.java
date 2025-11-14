//package ar.edu.itba.paw.persistence.hibernate;
//
//import ar.edu.itba.paw.model.Game.Game;
//import ar.edu.itba.paw.model.Game.GameFormat;
//import ar.edu.itba.paw.model.Participant;
//import ar.edu.itba.paw.model.Tournament;
//import ar.edu.itba.paw.model.User;
//import ar.edu.itba.paw.model.enums.Elo;
//import ar.edu.itba.paw.model.enums.Genre;
//import ar.edu.itba.paw.model.enums.Region;
//import ar.edu.itba.paw.model.enums.Structure;
//import ar.edu.itba.paw.persistence.Hibernate.GameHibernateDao;
//import ar.edu.itba.paw.persistence.TestConfig;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.jdbc.JdbcTestUtils;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.sql.DataSource;
//import java.time.LocalDate;
//import java.util.*;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TestConfig.class)
//@Sql("classpath:db/init.sql")
//@Transactional
//@Rollback
//public class GameHibernateDaoTest {
//    @PersistenceContext
//    private EntityManager em;
//
//    @Autowired
//    private DataSource ds;
//
//    @Autowired
//    private GameHibernateDao gameHibernateDao;
//
//    private JdbcTemplate jdbcTemplate;
//
//    private static final String NAME = "Sumo Slammers";
//    private static final String[] OTHER_NAMES = {"Grand Theft Walrus", "F-MEGA", "Chimpokomon", "Lee Carvallo's Putting Challenge", "Bonestorm"};
//    private static final Genre GENRE = Genre.MOBA;
//    private List<Long> USED_IDS;
//    private int rows;
//    private static final RowMapper<Game> ROW_MAPPER = (rs, rowNum) -> new Game(rs.getLong("id"), rs.getString("name"), Genre.valueOf(rs.getString("genre")), rs.getInt("imageId"));
//
//    @Before
//    public void setUp(){
//        jdbcTemplate = new JdbcTemplate(ds);
//        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .usingGeneratedKeyColumns("id")
//                .withTableName("game");
//        USED_IDS = new ArrayList<>();
//        for (Genre genre : Genre.values()){
//            for (String otherName : OTHER_NAMES) {
//                em.persist(new Game(otherName + " " + genre, genre,1));
//            }
//        }
//        em.flush();
//        rows = JdbcTestUtils.countRowsInTable(jdbcTemplate,"game");
//    }
//
//    @Test
//    public void testCreate(){
//        final Game game = gameHibernateDao.create(NAME, GENRE, 1);
//        em.flush();
//
//        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"game","name = '" + NAME + "' and genre = '" + GENRE + "' and imageId = 1"));
//        Assert.assertNotNull(game);
//        Assert.assertEquals(NAME, game.getName());
//        Assert.assertEquals(GENRE, game.getGenre());
//        Assert.assertEquals(Integer.valueOf(1), game.getImageId());
//        Assert.assertEquals(rows + 1,JdbcTestUtils.countRowsInTable(jdbcTemplate,"game"));
//    }
//
//    @Test(expected = Exception.class)
//    public void testCreateNameless(){
//        gameHibernateDao.create(null, GENRE, 1);
//    }
//
////    @Test(expected = Exception.class)
////    public void testCreateGenreless(){
////        gameHibernateDao.create(NAME, null, 1);
////        em.flush();
////    }
//
//    @Test
//    public void testCheckNameExists(){
//        boolean ans = gameHibernateDao.checkNameExists(OTHER_NAMES[0] + " " + GENRE);
//
//        Assert.assertTrue(ans);
//    }
//
//    @Test
//    public void testCheckNameDoesntExist(){
//        boolean ans = gameHibernateDao.checkNameExists(NAME);
//
//        Assert.assertFalse(ans);
//    }
//
//    @Test
//    public void testFindWithId(){
//        Optional<Game> ans = gameHibernateDao.findById(1);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans.isPresent());
//        Assert.assertEquals(OTHER_NAMES[0] + " " + GENRE, ans.get().getName());
//        Assert.assertEquals(GENRE, ans.get().getGenre());
//        Assert.assertEquals(Integer.valueOf(1), ans.get().getImageId());
//        Assert.assertEquals(1L, ans.get().getId().longValue());
//    }
//
//    @Test
//    public void testFindNonExistent(){
//        Optional<Game> ans = gameHibernateDao.findById(-1);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans.isEmpty());
//    }
//
//    @Test
//    public void testFindAll(){
//        List<Game> expected=new ArrayList<>();
//        long i = 0;
//        for (Genre genre : Genre.values()){
//            for (String otherName : OTHER_NAMES) {
//                expected.add(new Game(++i,otherName + " " + genre, genre, 1));
//            }
//        }
//
//        List<Game> games = gameHibernateDao.findAll();
//
//        Assert.assertNotNull(games);
//        Assert.assertFalse(games.isEmpty());
//        Assert.assertEquals(expected.size(), games.size());
//        for( int j = (int) i-1 ; j>=0; j-- ){
//            Assert.assertNotNull(games.get(j));
//            Assert.assertEquals(expected.get(j).getName(), games.get(j).getName());
//            Assert.assertEquals(expected.get(j).getGenre(), games.get(j).getGenre());
//            Assert.assertEquals(Integer.valueOf(1),games.get(j).getImageId());
//            Assert.assertEquals(expected.get(j).getId(),games.get(j).getId());
//        }
//    }
//
//    @Test
//    public void testFindNothing(){
//        JdbcTestUtils.deleteFromTables(jdbcTemplate,"game");
//        List<Game> games = gameHibernateDao.findAll();
//
//        Assert.assertNotNull(games);
//        Assert.assertTrue(games.isEmpty());
//    }
//
//    @Test
//    public void testSearchByGenre(){
//        List<Game> expected = new ArrayList<>();
//        long i=0;
//        for(String otherNames : OTHER_NAMES){
//            expected.add(new Game(++i, otherNames + " " + GENRE, GENRE, 1));
//        }
//
//        List<Game> games = gameHibernateDao.searchByGenre(GENRE);
//
//        Assert.assertNotNull(games);
//        Assert.assertFalse(games.isEmpty());
//        Assert.assertEquals(expected.size(), games.size());
//        for(int j = (int) i-1; j >=0; j--){
//            Assert.assertNotNull(games.get(j));
//            Assert.assertEquals(expected.get(j).getName(), games.get(j).getName());
//            Assert.assertEquals(expected.get(j).getGenre(), games.get(j).getGenre());
//            Assert.assertEquals(Integer.valueOf(1),games.get(j).getImageId());
//            Assert.assertEquals(expected.get(j).getId(),games.get(j).getId());
//        }
//    }
//
//    @Test
//    public void testSearchByGenreNothing(){
//        JdbcTestUtils.deleteFromTables(jdbcTemplate,"game");
//        List<Game> games = gameHibernateDao.searchByGenre(GENRE);
//
//        Assert.assertNotNull(games);
//        Assert.assertTrue(games.isEmpty());
//    }
//
//    @Test
//    public void testSearchByName(){
//        List<Game> expected = new ArrayList<>();
//        long j=1;
//        for(Genre genre : Genre.values()){
//            expected.add(new Game(j,OTHER_NAMES[0] + " " + genre, genre, 1));
//            j+=OTHER_NAMES.length;
//        }
//
//        List<Game> games = gameHibernateDao.searchByName(OTHER_NAMES[0]);
//
//        Assert.assertNotNull(games);
//        Assert.assertFalse(games.isEmpty());
//        Assert.assertEquals(expected.size(), games.size());
//        Comparator<Game> cmp = (a,b)-> Math.toIntExact((a.getId() - b.getId()));
//        expected.sort(cmp);
//        games.sort(cmp);
//        for(int i=0; i < expected.size(); i++){
//            Assert.assertNotNull(games.get(i));
//            Assert.assertEquals(expected.get(i).getName(), games.get(i).getName());
//            Assert.assertEquals(expected.get(i).getGenre(), games.get(i).getGenre());
//            Assert.assertEquals(Integer.valueOf(1),games.get(i).getImageId());
//            Assert.assertEquals(expected.get(i).getId(),games.get(i).getId());
//        }
//    }
//
//    @Test
//    public void testSearchByNameNoOne(){
//        List<Game> games = gameHibernateDao.searchByName(NAME);
//
//        Assert.assertNotNull(games);
//        Assert.assertTrue(games.isEmpty());
//    }
//
//    @Test
//    public void testSearchNameless(){
//        List<Game> expected=new ArrayList<>();
//        long j = 0;
//        for (Genre genre : Genre.values()){
//            for (String otherName : OTHER_NAMES) {
//                expected.add(new Game(++j,otherName + " " + genre, genre, 1));
//            }
//        }
//
//        List<Game> games = gameHibernateDao.searchByName("");
//
//        Assert.assertNotNull(games);
//        Assert.assertFalse(games.isEmpty());
//        Assert.assertEquals(expected.size(), games.size());
//        Comparator<Game> cmp = (a,b)-> Math.toIntExact((a.getId() - b.getId()));
//        expected.sort(cmp);
//        games.sort(cmp);
//        for( int i = (int) j - 1 ; i>=0; i-- ){
//            Assert.assertNotNull(games.get(i));
//            Assert.assertEquals(expected.get(i).getName(), games.get(i).getName());
//            Assert.assertEquals(expected.get(i).getGenre(), games.get(i).getGenre());
//            Assert.assertEquals(Integer.valueOf(1),games.get(i).getImageId());
//            Assert.assertEquals(expected.get(i).getId(),games.get(i).getId());
//        }
//    }
//
////    @Test
////    public void testGetFormats(){
////        SimpleJdbcInsert formatJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
////                .usingGeneratedKeyColumns("id")
////                .withTableName("game_format");
////        for (int i = 0; i < OTHER_NAMES.length; i++) {
////            formatJdbcInsert.executeAndReturnKey(Map.of("name", NAME, "players_per_team", i, "game_id", 1));
////        }
////        List<GameFormat> expected = new ArrayList<>();
////        for (int i = 0; i < OTHER_NAMES.length; i++) {
////            expected.add(new GameFormat((long) i, NAME, i));
////        }
////
////        List<GameFormat> formats = gameHibernateDao.getFormats(1L);
////
////        Assert.assertNotNull(formats);
////        Assert.assertFalse(formats.isEmpty());
////        Assert.assertEquals(expected.size(), formats.size());
////        for (int i = 0; i < OTHER_NAMES.length; i++) {
////            Assert.assertNotNull(formats.get(i));
////            Assert.assertEquals(expected.get(i).getGame_id(), formats.get(i).getGame_id());
////            Assert.assertEquals(expected.get(i).getId(), formats.get(i).getId());
////            Assert.assertEquals(expected.get(i).getName(), formats.get(i).getName());
////            Assert.assertEquals(expected.get(i).getPlayers_per_team(), formats.get(i).getPlayers_per_team());
////        }
////    }
//
//    @Test
//    public void testAddFavourites(){
//        User user = new User(NAME,NAME,NAME);
//        em.persist(user);
//        em.flush();
//        gameHibernateDao.addFavourite(1L, 1L);
//        em.flush();
//
//        Long ans = jdbcTemplate.queryForObject("select user_id from user_favourites where game_id = ?",Long.class,1);
//
//        Assert.assertNotNull(ans);
//        Assert.assertEquals(Long.valueOf(1), ans);
//    }
//
//    @Test
//    public void testGetFavourites(){
//        User user = new User(NAME,NAME,NAME);
//        em.persist(user);
//        em.flush();
//        SimpleJdbcInsert favouritesJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("user_favourites");
//        SimpleJdbcInsert tournamentJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("tournament");
//        SimpleJdbcInsert participantJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("participant");
//        for (long i = 1; i <= rows; i++) {
//            tournamentJdbcInsert.execute(Map.of("id",i,"creator_id",1L,"name",NAME+i,"game_id",i,"maxParticipants",8));
//            participantJdbcInsert.execute(Map.of("id",i,"user_id",1L,"tournament_id",i,"points",0));
//            favouritesJdbcInsert.execute(Map.of("user_id",1L,"game_id", i));
//        }
//
//        List<Game> favourites = gameHibernateDao.getFavourites(1L);
//
//        Assert.assertNotNull(favourites);
//        Assert.assertFalse(favourites.isEmpty());
//        Assert.assertEquals(6, favourites.size());
//        for (int i = 0; i < 6; i++){
//            Assert.assertNotNull(favourites.get(i));
//            Assert.assertEquals(0, favourites.get(i).getId() % 5); // por orden alfabético, sé cual juego toca siempre
//        }
//        JdbcTestUtils.deleteFromTables(jdbcTemplate,"user_favourites","tournament","participant");
//    }
//
////    @Test
////    public void testGetPlayersPerTeam(){
////        SimpleJdbcInsert formatJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
////                .usingGeneratedKeyColumns("id")
////                .withTableName("game_format");
////        Long id = formatJdbcInsert.executeAndReturnKey(Map.of("name", NAME, "players_per_team", 1, "game_id", 1)).longValue();
////        Integer ans = gameHibernateDao.getPlayersPerTeam(id);
////
////        Assert.assertNotNull(ans);
////        Assert.assertEquals(1,ans.intValue());
////    }
//}
