package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TeamDao;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

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
    public List<Team> getUserTeams(Long user_id) {
        return jdbcTemplate.query("SELECT DISTINCT t.* FROM team_member tm JOIN team t ON tm.team_id = t.id WHERE tm.user_id = ?", ROW_MAPPER, user_id);
    }

    @Override
    public void updateTeam(Long teamId, String name, Long pfpId, Long bannerId) {
        StringBuilder sql = new StringBuilder("UPDATE team SET ");
        List<Object> params = new ArrayList<>();

        sql.append("name = ?");
        params.add(name);

        if (pfpId != null) {
            sql.append(", profile_picture_id = ?");
            params.add(pfpId);
        }

        if (bannerId != null) {
            sql.append(", banner_id = ?");
            params.add(bannerId);
        }

        sql.append(" WHERE id = ?");
        params.add(teamId);

        jdbcTemplate.update(sql.toString(), params.toArray());
    }

    @Override
    public Boolean teamNameTaken(String name) {
        final String sql = "SELECT EXISTS(SELECT 1 FROM team WHERE name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    @Override
    public List<Team> searchByName(String name) {
        return jdbcTemplate.query("SELECT * FROM team WHERE LOWER(name) LIKE '%' || LOWER(?) || '%'", ROW_MAPPER, name);
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

    @Override
    public List<Team> getUserTeamsBySize(Long userId, Integer minSize) {
        String sql = """
        SELECT t.*
        FROM team t
        WHERE EXISTS (
            SELECT 1
            FROM team_member tm
            WHERE tm.team_id = t.id
              AND tm.user_id = ?
        )
        AND (
            SELECT COUNT(*)
            FROM team_member tm2
            WHERE tm2.team_id = t.id
        ) >= ?
        ORDER BY t.id
    """;
        return jdbcTemplate.query(sql, ROW_MAPPER, userId, minSize);
    }
}
