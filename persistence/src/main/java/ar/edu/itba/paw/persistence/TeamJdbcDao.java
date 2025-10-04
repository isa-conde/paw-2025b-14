package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TeamDao;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TeamJdbcDao implements TeamDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public TeamJdbcDao (DataSource ds){
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("team")
                .usingGeneratedKeyColumns("id");
    }

    private static final RowMapper<Team> ROW_MAPPER = (rs, rowNum) -> new Team(rs.getLong("id"), rs.getString("name"), rs.getLong("profile_picture_id"), rs.getLong("banner_id"), rs.getLong("owner_id"));

    @Override
    public Team create(String name, Long pfp_id, Long banner_id, Long owner_id) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("profile_picture_id", pfp_id);
        values.put("banner_id", banner_id);
        values.put("owner_id", owner_id);

        Number id = jdbcInsert.executeAndReturnKey(values);
        return new Team(id.longValue(), name, pfp_id, banner_id, owner_id);
    }

    @Override
    public Optional<Team> getById(Long id) {
        return jdbcTemplate.query("SELECT * FROM team WHERE id = ?", ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Long> getActiveTournaments(Long teamId) {
        return findTeamTournamentIds(teamId, false);
    }

    @Override
    public List<Long> getPastTournaments(Long teamId) {
        return findTeamTournamentIds(teamId, true);
    }

    private List<Long> findTeamTournamentIds(Long teamId, Boolean isFinished) {
        String sql = """
        SELECT t.id
        FROM tournament t
        INNER JOIN participant p ON p.tournament_id = t.id
        WHERE p.team_id = ? AND t.is_finished = ?
    """;
        return jdbcTemplate.queryForList(sql, Long.class, teamId, isFinished);
    }
}
