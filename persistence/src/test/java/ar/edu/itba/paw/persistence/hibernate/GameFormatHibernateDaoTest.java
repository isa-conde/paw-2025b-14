package ar.edu.itba.paw.persistence.hibernate;

import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.persistence.Hibernate.GameFormatHibernateDao;
import ar.edu.itba.paw.persistence.Hibernate.TeamMemberHibernateDao;
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
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class GameFormatHibernateDaoTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource ds;

    @Autowired
    private GameFormatHibernateDao gameFormatHibernateDao;

    private JdbcTemplate jdbcTemplate;
    private static Long ID = 100L;
    private static final String FORMAT = "formaggio";
    private static final int PPT = 4;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testInsertFormat(){
        Game game = new Game(ID,"Grand Theft Walrus MOBA", Genre.MOBA,ID.intValue());
        GameFormat gameFormat = new GameFormat();
        gameFormat.setGame(game);
        gameFormat.setName(FORMAT);
        gameFormat.setPlayersPerTeam(PPT);
        gameFormatHibernateDao.insertFormat(gameFormat);
        em.flush();

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"game_format",
                "game_id = " + ID + " and players_per_team = " + PPT + " and " +
                        "name = '" + FORMAT + "'"));
    }

    @Test
    public void testGetNoFormats(){
        List<GameFormat> ans = gameFormatHibernateDao.getFormats(ID);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testGetFormats(){
        List<GameFormat> ans = gameFormatHibernateDao.getFormats(ID+3);

        Assert.assertNotNull(ans);
        Assert.assertEquals(1,ans.size());
        Assert.assertEquals("formi",ans.get(0).getName());
        Assert.assertEquals(6,ans.get(0).getPlayersPerTeam().intValue());
        Assert.assertEquals(ID, ans.get(0).getId());
        Assert.assertEquals(ID+3,ans.get(0).getGame().getId().longValue());
    }

    @Test
    public void testFindById(){
        Optional<GameFormat> ans = gameFormatHibernateDao.findById(ID);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertEquals("formi",ans.get().getName());
        Assert.assertEquals(6,ans.get().getPlayersPerTeam().intValue());
        Assert.assertEquals(ID, ans.get().getId());
        Assert.assertEquals(ID+3,ans.get().getGame().getId().longValue());
    }

    @Test
    public void testFindNothing(){
        Optional<GameFormat> ans = gameFormatHibernateDao.findById(0L);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }
}
