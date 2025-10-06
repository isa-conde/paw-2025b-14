package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TeamMemberDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;

@Repository
public class TeamMemberJdbcDao implements TeamMemberDao {

    JdbcTemplate jdbcTemplate;
    SimpleJdbcInsert jdbcInsert;

    public TeamMemberJdbcDao(DataSource ds){
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("team_member");
    }


    @Override
    public void addMember(Long team_id, Long user_id) {
        Map<String, Object> values = Map.of("team_id", team_id, "user_id", user_id);
        jdbcInsert.execute(values);
    }
}
