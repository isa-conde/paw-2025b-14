package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Token;
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
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class TokenJdbcDaoTest {
    private List<Long> USED_IDS;
    private static final RowMapper<Token> ROW_MAPPER_TOKEN = (rs, rowNum) -> new Token(rs.getLong("id"), rs.getLong("user_id"), rs.getLong("token"), LocalDate.parse(rs.getString("expiry_date"),DateTimeFormatter.ISO_LOCAL_DATE) );

    @Autowired
    private DataSource ds;

    @Autowired
    private TokenJdbcDao tokenJdbcDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .usingGeneratedKeyColumns("id")
                .withTableName("tokens");
        USED_IDS = new ArrayList<>();
        USED_IDS.add(jdbcInsert.executeAndReturnKey(Map.of("user_id",1L,
                "token",1L,
                "expiry_date", LocalDate.now().minusDays(1).toString(),
                "used",false))
                .longValue());
        USED_IDS.add(jdbcInsert.executeAndReturnKey(Map.of("user_id",2L,
                "token",2L,
                "expiry_date", LocalDate.now().plusDays(1).toString(),
                "used",true))
                .longValue());
    }

    @Test
    public void testCreate(){
        final Token ans = tokenJdbcDao.create(3L,3L,LocalDate.now().plusDays(1));
        final List<Token> inserted = jdbcTemplate.query(
                "select * from tokens where id = ?",
                ROW_MAPPER_TOKEN, ans.getId()
        );

        Assert.assertNotNull(inserted);
        Assert.assertFalse(inserted.isEmpty());
        Assert.assertEquals(1,inserted.size());
        Assert.assertNotNull(ans);
        Assert.assertEquals(Long.valueOf(3),inserted.get(0).getToken());
        Assert.assertEquals(Long.valueOf(3),ans.getToken());
        Assert.assertEquals(Long.valueOf(3),inserted.get(0).getUser_id());
        Assert.assertEquals(Long.valueOf(3),ans.getUser_id());
        Assert.assertEquals(LocalDate.now().plusDays(1),inserted.get(0).getExpiry_date());
        Assert.assertEquals(LocalDate.now().plusDays(1),ans.getExpiry_date());
        Assert.assertFalse(inserted.get(0).isUsed());
        Assert.assertFalse(ans.isUsed());
    }
}
