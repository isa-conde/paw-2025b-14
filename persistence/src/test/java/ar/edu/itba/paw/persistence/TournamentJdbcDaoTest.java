package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
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
import java.time.LocalDate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
public class TournamentJdbcDaoTest {
    private static final String NAME = "Jerma Rumble";
    private static final Elo ELO = Elo.MID;
    private static final Region REGION = Region.LAS;
    private static final Structure STRUCTURE = Structure.LEAGUE;
    private static final LocalDate START_DATE = LocalDate.of(2003,2,21);
    private static final LocalDate END_DATE = LocalDate.of(2025, 2, 21);
    private static final String FORMAT = "some format";
    private static final byte[] IMAGE = {0x6f, 0x5d, 0x05, (byte) 0xf0,0x60 ,0x6a, 0x00 ,0x3c, 0x62, 0x07 };

    @Autowired
    private DataSource ds;

    @Autowired
    private TournamentJdbcDao tournamentJdbcDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"tournament");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "image");
    }

//    @Test
//    public void testCreate(){
//        final Tournament tournament = tournamentJdbcDao.create(1L, NAME, 1L, REGION, ELO, START_DATE, END_DATE, FORMAT, STRUCTURE, 8, IMAGE, true, false);
//
//        Assert.assertNotNull(tournament);
//        Assert.assertEquals(NAME, tournament.getName());
//        Assert.assertEquals(ELO, tournament.getElo());
//        Assert.assertEquals(REGION, tournament.getRegion());
//        Assert.assertEquals(STRUCTURE, tournament.getStructure());
//        Assert.assertEquals(START_DATE, tournament.getStart_date());
//        Assert.assertEquals(END_DATE, tournament.getEnd_date());
//        Assert.assertEquals(FORMAT, tournament.getFormat());
//        Assert.assertEquals(Integer.valueOf(8), tournament.getMax_participants());
//        Assert.assertEquals(Long.valueOf(1), tournament.getId());
//        Assert.assertEquals(Long.valueOf(1), tournament.getGame_id());
//        Assert.assertTrue(tournament.getOpenInscriptions());
//        Assert.assertFalse(tournament.getFinished());
//    }
}
