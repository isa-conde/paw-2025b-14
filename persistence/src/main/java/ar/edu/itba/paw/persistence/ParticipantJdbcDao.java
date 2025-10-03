package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.ParticipantUserInfo;
import ar.edu.itba.paw.model.Tournament.Tournament;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ParticipantJdbcDao implements ParticipantDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public ParticipantJdbcDao (final DataSource ds){
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("participant_user");
    }

    private static final RowMapper<ParticipantUser> ROW_MAPPER = (rs, rowNum) -> new ParticipantUser(
            rs.getLong("user_id"),
            rs.getLong("tournament_id"),
            rs.getInt("points"),
            rs.getInt("group_number")
    );

    private static final RowMapper<ParticipantUserInfo> ROW_MAPPER_INFO = (rs, rowNum) -> new ParticipantUserInfo(rs.getLong("user_id"), rs.getString("username"), rs.getString("email"), rs.getInt("points"), rs.getInt("group_number"));

    @Override
    public List<ParticipantUserInfo> getTournamentsParticipantUsersInfo(Long tournament_id) {
        return jdbcTemplate.query("SELECT * FROM participant_user " +
                                      "INNER JOIN users ON participant_user.user_id = users.id " +
                                      "WHERE tournament_id = ?", ROW_MAPPER_INFO, tournament_id);
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
        return jdbcTemplate.query("SELECT * FROM participant_user WHERE tournament_id = ?", ROW_MAPPER, tournament_id);
    }

    @Override
    public ParticipantUser getTournamentParticipantByUserId(Long tournament_id, Long user_id) {
        return jdbcTemplate.query("SELECT * FROM participant_user " +
                                      "WHERE tournament_id = ? AND user_id = ?",
                                       ROW_MAPPER, tournament_id, user_id).stream().findFirst().orElse(null);
    }

    @Override
    public Boolean hasJoined(Long userId, Long tournamentId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM participant_user WHERE user_id = ? AND tournament_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, userId, tournamentId);
    }

    @Override
    public void leaveTournamentUser(Long user_id, Long tournament_id) {
        jdbcTemplate.update("DELETE FROM participant_user WHERE user_id = ? AND tournament_id = ?", user_id, tournament_id);
    }

    @Override
    public void updateGroupNumberForUsers(long tournamentId, int groupNumber, List<Long> userIds) {
        jdbcTemplate.update(con -> {
            Array arr = con.createArrayOf("bigint", userIds.toArray(new Long[0]));
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE participant_user " +
                            "SET group_number = ? " +
                            "WHERE tournament_id = ? AND user_id = ANY(?)"
            );
            ps.setInt(1, groupNumber);
            ps.setLong(2, tournamentId);
            ps.setArray(3, arr);
            return ps;
        });
    }

    @Override
    public void swapGroups(Long tournament_id, Long user1, Long user2, Integer group1, Integer group2){
        jdbcTemplate.update(
                "UPDATE participant_user " +
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
                "SELECT MAX(points) FROM participant_user WHERE tournament_id = ?",
                Integer.class, tournamentId
        );
    }

    @Override
    public Integer getTournamentSecondMaxPoints(Long tournamentId) {
        return jdbcTemplate.queryForObject(
                "SELECT MAX(points) FROM participant_user WHERE tournament_id = ? AND points < ?",
                Integer.class, tournamentId, getTournamentMaxPoints(tournamentId)
        );
    }

    @Override
    public Integer getGroupNumber(Long tournamentId, Long userId) {
        final String sql =
                "SELECT COALESCE((" +
                        "  SELECT group_number FROM participant_user WHERE tournament_id = ? AND user_id = ? LIMIT 1" +
                        "), 0)";
        return jdbcTemplate.queryForObject(sql, Integer.class, tournamentId, userId);
    }

    @Override
    public List<ParticipantUser> getTournamentParticipantsByPoints(
            Long tournamentId, Integer groupNumber, Integer points) {

        if (groupNumber == null) {
            return jdbcTemplate.query(
                    "SELECT * FROM participant_user WHERE tournament_id = ? AND points = ?",
                    ROW_MAPPER, tournamentId, points
            );
        } else {
            return jdbcTemplate.query(
                    "SELECT * FROM participant_user WHERE tournament_id = ? AND points = ? AND group_number = ?",
                    ROW_MAPPER, tournamentId, points, groupNumber
            );
        }
    }

    @Override
    public Integer getTournamentGroups(Long tournamentId){
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT group_number) FROM participant_user WHERE tournament_id = ?",
                Integer.class, tournamentId
        );
    }

    @Override
    public void sumPoints(Long tournamentId, Long userId, Integer points){
        jdbcTemplate.update("UPDATE participant_user SET points = points + ? WHERE user_id = ?", points, userId);
    }
}
