package ar.edu.itba.paw.persistence.Jdbc;

import ar.edu.itba.paw.interfaces.persistence.TeamDao;
import ar.edu.itba.paw.model.Team;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

public class TeamJdbcDao implements TeamDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public TeamJdbcDao (DataSource ds){
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("team")
                .usingGeneratedKeyColumns("id");
    }

    private static final RowMapper<Team> ROW_MAPPER = (rs, rowNum) -> new Team(rs.getLong("id"), rs.getString("name"), rs.getLong("profile_picture_id"), rs.getLong("banner_id"));

    @Override
    public Team create(String name, Long pfp_id, Long banner_id, Long owner_id) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("profile_picture_id", pfp_id);
        values.put("banner_id", banner_id);
        values.put("owner_id", owner_id);

        Number id = jdbcInsert.executeAndReturnKey(values);
        return new Team(id.longValue(), name, pfp_id, banner_id);
    }

    @Override
    public Optional<Team> getById(Long id) {
        return jdbcTemplate.query("SELECT * FROM team WHERE id = ?", ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Long> getActiveTournaments(Long teamId, Integer page) {
        return findTeamTournamentIds(teamId, false, page);
    }

    @Override
    public Long getActivePages(Long team_id) {
        return countTeamTournaments(team_id, false);
    }

    @Override
    public Long getPastPages(Long team_id) {
        return countTeamTournaments(team_id, true);
    }

    private Long countTeamTournaments(Long teamId, Boolean isFinished) {
        String sql = """
        SELECT COUNT(*)
        FROM tournament t
        INNER JOIN participant p ON p.tournament_id = t.id
        WHERE p.team_id = ? AND t.is_finished = ? AND p.user_id IS NULL
    """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, teamId, isFinished);
        return (long) Math.ceil((double) count / 9L);
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
        final String sql = "SELECT id FROM team WHERE name = ? LIMIT 1";
        try {
            jdbcTemplate.queryForObject(sql, Long.class, name);
        }catch (EmptyResultDataAccessException e){
            return false;
        }
        return true;
    }

    @Override
    public List<Team> searchByName(String name) {
        return jdbcTemplate.query("SELECT * FROM team WHERE LOWER(name) LIKE '%' || LOWER(?) || '%'", ROW_MAPPER, name);
    }

    @Override
    public List<Long> getPastTournaments(Long teamId, Integer page) {
        return findTeamTournamentIds(teamId, true, page);
    }

    private List<Long> findTeamTournamentIds(Long teamId, Boolean isFinished, Integer page) {
        String sql = """
        SELECT t.id
        FROM tournament t
        INNER JOIN participant p ON p.tournament_id = t.id
        WHERE p.team_id = ? AND t.is_finished = ? AND p.user_id IS NULL
        ORDER BY t.id DESC
        LIMIT ? OFFSET ?
    """;
        return jdbcTemplate.queryForList(sql, Long.class, teamId, isFinished, 9, page * 9);
    }

    @Override
    public List<Team> getUserTeamsBySizeNotInTournament(Long userId, Long tournamentId, Long minSize) {
        final long required = (minSize == null ? 1 : minSize);
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
          AND NOT EXISTS (
            SELECT 1
            FROM participant p
            WHERE p.tournament_id = ?
              AND p.team_id = t.id
          )
        ORDER BY t.id
        """;
        return jdbcTemplate.query(sql, ROW_MAPPER, userId, required, tournamentId);
    }
}
