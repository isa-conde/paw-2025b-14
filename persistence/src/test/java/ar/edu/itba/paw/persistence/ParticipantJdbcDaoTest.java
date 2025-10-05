package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.ParticipantUser;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class ParticipantJdbcDaoTest {
    private static final RowMapper<ParticipantUser> ROW_MAPPER = (rs, rowNum) -> new ParticipantUser(
            rs.getLong("user_id"),
            rs.getLong("tournament_id"),
            rs.getInt("points"),
            rs.getInt("group_number")
    );

    @Autowired
    private DataSource ds;

    @Autowired
    private ParticipantJdbcDao participantJdbcDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }
}
