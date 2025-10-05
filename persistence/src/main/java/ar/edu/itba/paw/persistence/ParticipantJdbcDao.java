package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.ParticipantInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ParticipantJdbcDao implements ParticipantDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public ParticipantJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("participant")
                .usingGeneratedKeyColumns("id");
    }

    private static final RowMapper<ParticipantUser> ROW_MAPPER = (rs, rowNum) -> new ParticipantUser(
            rs.getLong("user_id"),
            rs.getLong("tournament_id"),
            rs.getInt("points"),
            rs.getInt("group_number")
    );

    private static final RowMapper<ParticipantInfo> ROW_MAPPER_USER_INFO = (rs, rowNum) -> new ParticipantInfo(rs.getLong("user_id"), rs.getString("username"), rs.getInt("points"), rs.getInt("group_number"));

    private static final RowMapper<ParticipantInfo> ROW_MAPPER_TEAM_INFO = (rs, rowNum) -> new ParticipantInfo(rs.getLong("team_id"), rs.getString("name"), rs.getInt("points"), rs.getInt("group_number"));

    @Override
    public List<ParticipantInfo> getTournamentsParticipantUsersInfo(Long tournament_id) {
        return jdbcTemplate.query("SELECT * FROM participant " +
                                      "INNER JOIN users ON participant.user_id = users.id " +
                                      "WHERE tournament_id = ?", ROW_MAPPER_USER_INFO, tournament_id);
    }

    @Override
    public List<ParticipantInfo> getTournamentsParticipantTeamsInfo(Long tournament_id) {
        return jdbcTemplate.query("SELECT * FROM participant " +
                "INNER JOIN team ON participant.team_id = team.id " +
                "WHERE tournament_id = ?", ROW_MAPPER_TEAM_INFO, tournament_id);
    }

    @Override
    public void joinTournamentUser(Long user_id, Long tournament_id) {
        Map<String, Object> values = new HashMap<>();

        values.put("user_id", user_id);
        values.put("tournament_id", tournament_id);
        values.put("points", 0);

        jdbcInsert.execute(values);
    }

    @Override
    public List<ParticipantUser> getTournamentParticipantUsers(Long tournament_id) {
        return jdbcTemplate.query("SELECT * FROM participant WHERE tournament_id = ?", ROW_MAPPER, tournament_id);
    }

    @Override
    public ParticipantUser getTournamentParticipantByUserId(Long tournament_id, Long user_id) {
        return jdbcTemplate.query("SELECT * FROM participant " +
                                      "WHERE tournament_id = ? AND user_id = ?",
                                       ROW_MAPPER, tournament_id, user_id).stream().findFirst().orElse(null);
    }

    @Override
    public Boolean hasJoined(Long userId, Long tournamentId) {
        String sql = "SELECT 1 FROM participant WHERE user_id = ? AND tournament_id = ? LIMIT 1";
        return !jdbcTemplate.query(sql, (rs, rowNum) -> 1, userId, tournamentId).isEmpty();
    }

    @Override
    public void leaveTournamentUser(Long user_id, Long tournament_id) {
        jdbcTemplate.update("DELETE FROM participant WHERE user_id = ? AND tournament_id = ?", user_id, tournament_id);
    }

    @Override
    public void updateGroupNumberForUsers(long tournamentId, int groupNumber, List<Long> userIds) {
        String inClause = userIds.stream()
                .map(ignore -> "?")
                .collect(Collectors.joining(", "));

        String sql = "UPDATE participant " +
                "SET group_number = ? " +
                "WHERE tournament_id = ? AND user_id IN (" + inClause + ")";

        Object[] params = new Object[userIds.size() + 2];
        params[0] = groupNumber;
        params[1] = tournamentId;
        for (int i = 0; i < userIds.size(); i++) {
            params[i + 2] = userIds.get(i);
        }

        jdbcTemplate.update(sql, params);
    }

    @Override
    public void swapGroups(Long tournament_id, Long user1, Long user2, Integer group1, Integer group2){
        jdbcTemplate.update(
                "UPDATE participant " +
                        "SET group_number = CASE " +
                        "  WHEN user_id = ? THEN ? " +
                        "  WHEN user_id = ? THEN ? " +
                        "  ELSE group_number END " +
                        "WHERE tournament_id = ? AND user_id IN (?, ?)",
                user1, group2,
                user2, group1,
                tournament_id, user1, user2
        );
    }

    @Override
    public Integer getTournamentMaxPoints(Long tournamentId){
        return jdbcTemplate.queryForObject(
                "SELECT MAX(points) FROM participant WHERE tournament_id = ?",
                Integer.class, tournamentId
        );
    }

    @Override
    public Integer getTournamentSecondMaxPoints(Long tournamentId) {
        return jdbcTemplate.queryForObject(
                "SELECT MAX(points) FROM participant WHERE tournament_id = ? AND points < ?",
                Integer.class, tournamentId, getTournamentMaxPoints(tournamentId)
        );
    }

    @Override
    public Integer getGroupNumber(Long tournamentId, Long userId) {
        final String sql = "SELECT group_number FROM participant " +
                "WHERE tournament_id = ? AND user_id = ? LIMIT 1";
        List<Integer> results = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("group_number"), tournamentId, userId);
        return results.isEmpty() ? 0 : results.get(0);
    }

    @Override
    public List<ParticipantUser> getTournamentParticipantsByPoints(
            Long tournamentId, Integer groupNumber, Integer points) {

        if (groupNumber == null) {
            return jdbcTemplate.query(
                    "SELECT * FROM participant WHERE tournament_id = ? AND points = ?",
                    ROW_MAPPER, tournamentId, points
            );
        } else {
            return jdbcTemplate.query(
                    "SELECT * FROM participant WHERE tournament_id = ? AND points = ? AND group_number = ?",
                    ROW_MAPPER, tournamentId, points, groupNumber
            );
        }
    }

    @Override
    public Integer getTournamentGroups(Long tournamentId){
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT group_number) FROM participant WHERE tournament_id = ?",
                Integer.class, tournamentId
        );
    }

    @Override
    public void sumPoints(Long tournamentId, Long userId, Integer points){
        jdbcTemplate.update("UPDATE participant SET points = points + ? WHERE user_id = ?", points, userId);
    }
}
