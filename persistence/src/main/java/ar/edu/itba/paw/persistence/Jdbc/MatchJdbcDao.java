//package ar.edu.itba.paw.persistence.Jdbc;
//
//import ar.edu.itba.paw.interfaces.persistence.MatchDao;
//import ar.edu.itba.paw.model.Match;
//import ar.edu.itba.paw.model.MatchInfo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
//import org.springframework.stereotype.Repository;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class MatchJdbcDao implements MatchDao {
//
//    private final JdbcTemplate jdbcTemplate;
//    private final SimpleJdbcInsert jdbcInsert;
//
//    //@Autowired
//    public MatchJdbcDao(final DataSource ds) {
//        this.jdbcTemplate = new JdbcTemplate(ds);
//        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("match");
//    }
//
////    public static final RowMapper<Match> ROW_MAPPER_MATCH = (rs, rowNum) -> new Match(
////            rs.getLong("id"),
////            rs.getLong("tournament_id"),
////            rs.getObject("local_id") != null ? rs.getLong("local_id") : null,
////            rs.getObject("visitor_id") != null ? rs.getLong("visitor_id") : null,
////            rs.getObject("local_score") != null ? rs.getInt("local_score") : null,
////            rs.getObject("visitor_score") != null ? rs.getInt("visitor_score") : null,
////            rs.getObject("winner") != null ? rs.getInt("winner") : null,
////            rs.getInt("stage"),
////            rs.getObject("is_group_stage") != null ? rs.getBoolean("is_group_stage") : null
////    );
//
//    private static final RowMapper<MatchInfo> ROW_MAPPER_MATCH_INFO = (rs, rowNum) ->
//        new MatchInfo(
//            rs.getLong("id"),
//            rs.getLong("tournament_id"),
//            rs.getObject("local_id") != null ? rs.getLong("local_id") : null,
//            rs.getObject("visitor_id") != null ? rs.getLong("visitor_id") : null,
//            rs.getObject("local_score") != null ? rs.getInt("local_score") : null,
//            rs.getObject("visitor_score") != null? rs.getInt("visitor_score") : null,
//            rs.getObject("winner") != null ? rs.getInt("winner") : null,
//            rs.getObject("stage") != null ? rs.getInt("stage") : null,
//            rs.getObject("group_number") != null ? rs.getInt("group_number") : null,
//            rs.getObject("is_group_stage") != null ? rs.getBoolean("is_group_stage") : null
//        );
//
//
//    @Override
//    public void insertMatch(Long id, Long tournamentId, Long localId, Long visitorId, Integer stage, Integer localScore, Integer visitorScore, Integer winner, Boolean isGroupStage) {
//        Map<String, Object> values = new HashMap<>();
//        values.put("id", id);
//        values.put("tournament_id", tournamentId);
//        values.put("local_id", localId);
//        values.put("visitor_id", visitorId);
//        values.put("local_score", localScore);
//        values.put("visitor_score", visitorScore);
//        values.put("winner", winner);
//        values.put("stage", stage);
//        values.put("is_group_stage", isGroupStage);
//        jdbcInsert.execute(values);
//    }
//
//    @Override
//    public Long getMatchWinner(Long tournamentId, Long matchId) {
//        return jdbcTemplate.queryForObject(
//                "SELECT ( " +
//                        "CASE WHEN winner = 1 THEN local_id " +
//                        "WHEN winner = 2 THEN visitor_id " +
//                        "ELSE NULL END ) AS winner_id" +
//                        "  FROM match WHERE tournament_id = ? AND id = ? ",
//                Long.class, tournamentId, matchId
//        );
//    }
//
//    @Override
//    public List<MatchInfo> getTournamentMatches(Long tournamentId, Integer teamSize) {
//        final String joinCondition = (teamSize != null && teamSize > 1)
//                ? "AND p.team_id = m.local_id AND p.user_id IS NULL"
//                : "AND p.user_id = m.local_id";
//
//        final String sql = """
//           SELECT m.id, m.tournament_id, m.local_id, m.visitor_id,
//                  m.local_score, m.visitor_score, m.winner, m.stage, m.is_group_stage,
//                  COALESCE(p.group_number, 0) AS group_number
//             FROM match m
//        LEFT JOIN participant p
//               ON p.tournament_id = m.tournament_id
//                  %s
//            WHERE m.tournament_id = ?
//         ORDER BY m.stage, m.id
//        """.formatted(joinCondition);
//
//        return jdbcTemplate.query(sql, ROW_MAPPER_MATCH_INFO, tournamentId);
//    }
//
//    @Override
//    public Long getMaxMatchId(Long tournamentId){
//        return jdbcTemplate.queryForObject(
//                "SELECT MAX(id) FROM match WHERE tournament_id = ?",
//                Long.class, tournamentId
//        );
//    }
//
//    @Override
//    public void setMatchWinner(Long matchId, Long tournamentId, Integer winner) {
//        jdbcTemplate.update("UPDATE match SET winner = ? WHERE id = ? AND tournament_id = ?",
//                winner, matchId, tournamentId);
//    }
//
//    @Override
//    public void updateMatchLocal(Long tournament_id, Long match, Long user){
//        jdbcTemplate.update(
//                "UPDATE match SET local_id=? WHERE tournament_id=? AND id=?",
//                user, tournament_id, match
//        );
//    }
//
//    @Override
//    public void updateMatchVisitor(Long tournament_id, Long match, Long user){
//        jdbcTemplate.update(
//                "UPDATE match SET visitor_id=? WHERE tournament_id=? AND id=?",
//                user, tournament_id, match
//        );
//    }
//
//    @Override
//    public Match getMatch(long tournamentId, long matchId) {
////        return jdbcTemplate.queryForObject(
////                "SELECT * FROM match WHERE tournament_id = ? AND id = ?",
////                ROW_MAPPER_MATCH, tournamentId, matchId
////        );
//        return null;
//    }
//
//    @Override
//    public Boolean allMatchesPlayed(Long tournamentId){
//        final String sql = """
//            SELECT CASE
//                      WHEN COUNT(*) > 0
//                           AND COUNT(*) = COUNT(CASE WHEN winner IS NOT NULL AND winner > 0 THEN 1 END)
//                      THEN TRUE
//                      ELSE FALSE
//                  END AS all_played
//           FROM match
//           WHERE tournament_id = ?
//        """;
//        return jdbcTemplate.queryForObject(sql, Boolean.class, tournamentId);
//    }
//
//    @Override
//    public Integer getMatchStage(Long tournamentId, Long matchId) {
//        return jdbcTemplate.queryForObject(
//                "SELECT stage FROM match WHERE tournament_id = ? AND id = ?",
//                Integer.class, tournamentId, matchId
//        );
//    }
//
//    @Override
//    public List<Long> getStageMatchIds(Integer stage, Long tournamentId) {
//        return jdbcTemplate.queryForList(
//                "SELECT id FROM match WHERE tournament_id = ? AND stage = ? ORDER BY id",
//                Long.class, tournamentId, stage
//        );
//    }
//
//    @Override
//    public Integer getTournamentMaxStage(Long tournamentId) {
//        return jdbcTemplate.queryForObject(
//                "SELECT COALESCE(MAX(stage), 0) FROM match WHERE tournament_id = ?",
//                Integer.class, tournamentId
//        );
//    }
//
//    @Override
//    public Integer getTournamentGroupMaxStage(Long tournamentId, Integer groupNumber) {
//        return jdbcTemplate.queryForObject(
//                "SELECT MAX(m.stage) " +
//                        "FROM match m " +
//                        "JOIN participant pu " +
//                        "  ON pu.tournament_id = m.tournament_id " +
//                        " AND pu.user_id = m.visitor_id " +
//                        "WHERE m.tournament_id = ? " +
//                        "  AND pu.group_number = ?",
//                Integer.class, tournamentId, groupNumber
//        );
//    }
//}
