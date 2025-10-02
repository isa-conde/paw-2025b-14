package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.MatchDao;
import ar.edu.itba.paw.model.Match;
import ar.edu.itba.paw.model.MatchWithPlayers;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.enums.Structure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MatchJdbcDao implements MatchDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public MatchJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("match");
    }

    public static final RowMapper<Match> ROW_MAPPER_MATCH = (rs, rowNum) -> new Match(
            rs.getLong("id"),
            rs.getLong("tournament_id"),
            rs.getObject("local_id") != null ? rs.getLong("local_id") : null,
            rs.getObject("visitor_id") != null ? rs.getLong("visitor_id") : null,
            rs.getObject("local_score") != null ? rs.getInt("local_score") : null,
            rs.getObject("visitor_score") != null ? rs.getInt("visitor_score") : null,
            rs.getObject("winner") != null ? rs.getInt("winner") : null,
            rs.getInt("stage")
    );

    @Override
    public void insertMatch(Long id, Long tournamentId, Long localId, Long visitorId, Integer stage, Integer localScore, Integer visitorScore, Integer winner) {
        Map<String, Object> values = new HashMap<>();
        values.put("id", id);
        values.put("tournament_id", tournamentId);
        values.put("local_id", localId);
        values.put("visitor_id", visitorId);
        values.put("local_score", localScore);
        values.put("visitor_score", visitorScore);
        values.put("winner", winner);
        values.put("stage", stage);
        jdbcInsert.execute(values);
    }

    @Override
    public Long getMatchWinner(Long tournamentId, Long matchId) {
        return jdbcTemplate.queryForObject(
                "SELECT ( " +
                        "  SELECT CASE WHEN winner = 1 THEN local_id " +
                        "              WHEN winner = 2 THEN visitor_id " +
                        "              ELSE NULL END " +
                        "  FROM match WHERE tournament_id = ? AND id = ? " +
                        ")",
                Long.class, tournamentId, matchId
        );
    }

    @Override
    public List<MatchWithPlayers> getTournamentMatches(Long tournament_id) {
        String sql = "SELECT m.id, m.tournament_id, m.local_id, m.visitor_id, " +
                "COALESCE(local_user.username, 'TBD') as local_player_name, " +
                "COALESCE(visitor_user.username, 'TBD') as visitor_player_name, " +
                "m.local_score, m.visitor_score, m.winner, m.stage " +
                "FROM match m " +
                "LEFT JOIN users local_user ON m.local_id = local_user.id " +
                "LEFT JOIN users visitor_user ON m.visitor_id = visitor_user.id " +
                "WHERE m.tournament_id = ? " +
                "ORDER BY m.stage, m.id";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new MatchWithPlayers(
                rs.getLong("id"),
                rs.getLong("tournament_id"),
                rs.getObject("local_id") != null ? rs.getLong("local_id") : null,
                rs.getObject("visitor_id") != null ? rs.getLong("visitor_id") : null,
                rs.getString("local_player_name"),
                rs.getString("visitor_player_name"),
                rs.getObject("local_score") != null ? rs.getInt("local_score") : null,
                rs.getObject("visitor_score") != null ? rs.getInt("visitor_score") : null,
                rs.getObject("winner") != null ? rs.getInt("winner") : null,
                rs.getObject("stage") != null ? rs.getInt("stage") : null
        ), tournament_id);
    }

    @Override
    public void setMatchWinner(Long matchId, Long tournamentId, Integer winner) {
        if (winner == null || (winner != 1 && winner != 2)) {
            throw new IllegalArgumentException("winner must be 1 (local) or 2 (visitor)");
        }

        Map<String, Object> match = jdbcTemplate.queryForMap(
                "SELECT local_id, visitor_id FROM match WHERE id = ? AND tournament_id = ?",
                matchId, tournamentId
        );

        Long localId = (Long) match.get("local_id");
        Long visitorId = (Long) match.get("visitor_id");

        if (localId == null || visitorId == null) {
            throw new IllegalStateException("Cannot set winner for TBD matches");
        }

        jdbcTemplate.update("UPDATE match SET winner = ? WHERE id = ? AND tournament_id = ?",
                winner, matchId, tournamentId);

        Long winnerId = (winner == 1) ? localId : visitorId;

        Integer totalMatches = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM match WHERE tournament_id = ?",
                Integer.class, tournamentId
        );

        Integer finishedMatches = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM match WHERE tournament_id = ? AND winner IS NOT NULL AND winner > 0",
                Integer.class, tournamentId
        );

        boolean isFinished = totalMatches.equals(finishedMatches) && totalMatches > 0;

        Tournament t = findById(tournamentId).orElse(null);
        if (t == null) {
            return;
        }
        Structure structure = t.getStructure();
        boolean isGroupStage = Boolean.TRUE.equals(t.getIs_group_stage());

        if(structure.equals(Structure.ELIMINATION) && !isFinished) {
            setNextMatchInfo(matchId, tournamentId, winnerId);
        }else if(structure.equals(Structure.HYBRID)) {
            if(isGroupStage) {
                jdbcTemplate.update("UPDATE participant_user SET points = points + 3 WHERE user_id = ?", winnerId);
                if(isFinished) {
                    createBracketFromGroups(tournamentId);
                    totalMatches = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM match WHERE tournament_id = ?",
                            Integer.class, tournamentId
                    );
                    isFinished = totalMatches.equals(finishedMatches);
                }
            }else if(!isFinished){
                setNextMatchInfo(matchId, tournamentId, winnerId);
            }
        }else{
            jdbcTemplate.update("UPDATE participant_user SET points = points + 3 WHERE user_id = ?", winnerId);
        }

        if (isFinished) {
            setFinished(tournamentId, matchId);
        }
    }

    private void setNextMatchInfo(Long matchId, Long tournamentId, Long winnerId) {
        Integer currentStage = jdbcTemplate.queryForObject(
                "SELECT stage FROM match WHERE id = ? AND tournament_id = ?",
                Integer.class, matchId, tournamentId
        );

        List<Long> idsThisStage = jdbcTemplate.queryForList(
                "SELECT id FROM match WHERE tournament_id = ? AND stage = ? ORDER BY id",
                Long.class, tournamentId, currentStage
        );

        int indexInStage = idsThisStage.indexOf(matchId);
        if (indexInStage < 0) {
            return;
        }
        int parentOffset = indexInStage / 2;
        List<Long> nextMatches = jdbcTemplate.queryForList(
                "SELECT id FROM match WHERE tournament_id = ? AND stage = ? ORDER BY id LIMIT 1 OFFSET ?",
                Long.class, tournamentId, currentStage + 1, parentOffset
        );

        if (nextMatches.isEmpty()) {
            return;
        }
        Long nextMatchId = nextMatches.get(0);

        boolean isLeftChild = (indexInStage % 2 == 0);

        if (isLeftChild) {
            jdbcTemplate.update(
                    "UPDATE match SET local_id = ? WHERE id = ? AND tournament_id = ?",
                    winnerId, nextMatchId, tournamentId
            );
        } else {
            jdbcTemplate.update(
                    "UPDATE match SET visitor_id = ? WHERE id = ? AND tournament_id = ?",
                    winnerId, nextMatchId, tournamentId
            );
        }
    }

    @Override
    @Transactional
    public void swapMatchesMembers(Long tournament_id, Long match1, Long match2, Long user1, Long user2){
        Match m1 = getMatch(tournament_id, match1);
        Match m2 = getMatch(tournament_id, match2);
        if (m1 == null || m2 == null) {
            throw new IllegalArgumentException("Both matches must exist in the tournament");
        }

        boolean u1IsLocalM1 = m1.getLocalId() != null && m1.getLocalId().equals(user1);
        boolean u1IsVisitM1 = m1.getVisitorId() != null && m1.getVisitorId().equals(user1);
        boolean u2IsLocalM2 = m2.getLocalId() != null && m2.getLocalId().equals(user2);
        boolean u2IsVisitM2 = m2.getVisitorId() != null && m2.getVisitorId().equals(user2);

        if ((!u1IsLocalM1 && !u1IsVisitM1) || (!u2IsLocalM2 && !u2IsVisitM2)) {
            throw new IllegalArgumentException("user1 must be in match1 and user2 must be in match2");
        }

        if (u1IsLocalM1) {
            jdbcTemplate.update(
                    "UPDATE match SET local_id=? WHERE tournament_id=? AND id=?",
                    user2, tournament_id, match1
            );
        } else {
            jdbcTemplate.update(
                    "UPDATE match SET visitor_id=? WHERE tournament_id=? AND id=?",
                    user2, tournament_id, match1
            );
        }
        if (u2IsLocalM2) {
            jdbcTemplate.update(
                    "UPDATE match SET local_id=? WHERE tournament_id=? AND id=?",
                    user1, tournament_id, match2
            );
        } else {
            jdbcTemplate.update(
                    "UPDATE match SET visitor_id=? WHERE tournament_id=? AND id=?",
                    user1, tournament_id, match2
            );
        }
    }

    @Override
    public Match getMatch(long tournamentId, long matchId) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM match WHERE tournament_id = ? AND id = ?",
                ROW_MAPPER_MATCH, tournamentId, matchId
        );
    }
}
