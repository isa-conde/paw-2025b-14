package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.Tournament.Tournament;
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
public class ParticipantJdbcDao implements ParticipantDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public ParticipantJdbcDao (final DataSource ds){
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("participant_user");
    }

    private static final RowMapper<ParticipantUser> ROW_MAPPER = (rs, rowNum) -> {
        ParticipantUser participant = new ParticipantUser(rs.getLong("user_id"), rs.getLong("tournament_id"));
        participant.setPoints(rs.getInt("points"));
        return participant;
    };

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
}
