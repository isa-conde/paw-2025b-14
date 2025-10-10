package ar.edu.itba.paw.persistence.Jdbc;

import ar.edu.itba.paw.interfaces.persistence.TeamMemberDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
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

    @Override
    public Boolean isMember(Long team_id, Long user_id) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM team_member WHERE user_id = ? AND team_id = ?", Integer.class , user_id, team_id);
        return count != null && count > 0;
    }

    @Override
    public List<Long> getTeamMembers(Long team_id) {
        return jdbcTemplate.queryForList("SELECT user_id FROM team_member WHERE team_id = ?", Long.class, team_id);
    }
}
