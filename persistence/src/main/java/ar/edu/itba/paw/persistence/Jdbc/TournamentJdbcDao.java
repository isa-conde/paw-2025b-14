package ar.edu.itba.paw.persistence.Jdbc;

import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TournamentJdbcDao implements TournamentDao {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

//    @Autowired
    public TournamentJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tournament")
                .usingGeneratedKeyColumns("id");
    }

    private static final RowMapper<Tournament> ROW_MAPPER = (rs, rowNum) -> new Tournament(
            rs.getLong("id"),
            rs.getLong("creator_id"),
            rs.getString("name"),
            rs.getLong("game_id"),
            Region.valueOf(rs.getString("region")),
            Elo.valueOf(rs.getString("elo")),
            rs.getDate("start_date").toLocalDate(),
            rs.getDate("end_date").toLocalDate(),
            rs.getString("format"),
            Structure.valueOf(rs.getString("structure")),
            rs.getInt("max_participants"),
            rs.getLong("image_id"),
            rs.getBoolean("open_inscriptions"),
            rs.getBoolean("is_finished"),
            rs.getLong("tournament_winner"),
            rs.getBoolean("is_group_stage"),
            rs.getBoolean("tournament_started"),
            rs.getLong("format_id"));

    @Override
    public Optional<Tournament> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM tournament WHERE id = ?", ROW_MAPPER, id).stream().findFirst();
    }

    //@Override
    public List<Tournament> findGameTournaments(Long game_id) {
        return jdbcTemplate.query("SELECT * FROM tournament t WHERE game_id = ? AND open_inscriptions = true", ROW_MAPPER, game_id);
    }

    @Override
    public Tournament create(Long creator_id, String name, Long game_id, Region region, Elo elo, LocalDate start_date, LocalDate end_date, String format, Structure structure, Integer max_participants, Long image_id, Boolean open_inscriptions, Boolean is_finished, Long format_id) {

        SqlParameterSource values = new MapSqlParameterSource()
                .addValue("creator_id", creator_id)
                .addValue("name", name)
                .addValue("game_id", game_id)
                .addValue("region", region.name(), Types.OTHER)
                .addValue("elo", elo.name(), Types.OTHER)
                .addValue("start_date", start_date)
                .addValue("end_date", end_date)
                .addValue("format", format)
                .addValue("structure", structure.name(), Types.OTHER)
                .addValue("max_participants", max_participants)
                .addValue("image_id", image_id)
                .addValue("open_inscriptions", open_inscriptions)
                .addValue("is_finished", is_finished)
                .addValue("tournament_started", false)
                .addValue("format_id", format_id);

        Number key = jdbcInsert.executeAndReturnKey(values);
        return new Tournament(key.longValue(), creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, null, null, false, format_id);
    }

    @Override
    public Boolean isTournamentStarted(Long tournamentId) {
        final String sql =
                "SELECT tournament_started FROM tournament WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Boolean.class, tournamentId);
        }catch (DataAccessException e){
            return false;
        }
    }

    @Override
    public void setIsGroupStage(Long tournamentId, Boolean bool){
        jdbcTemplate.update("UPDATE tournament SET is_group_stage = ? WHERE id = ?", bool, tournamentId);
    }

    @Override
    public Structure getTournamentStructure(Long tournamentId) {
        return jdbcTemplate.query(
                "SELECT structure FROM tournament WHERE id = ? LIMIT 1",
                rs -> rs.next() ? Structure.valueOf(rs.getString(1)) : null,
                tournamentId
        );
    }

    @Override
    public Integer getCreatedAndFinishedTournamentsPages(Long userId) {
        int count = countCreatedTournaments(userId, true);
        return (int) Math.ceil(count / 9.0);
    }

    @Override
    public Integer getCreatedAndOngoingTournamentsPages(Long userId) {
        int count = countCreatedTournaments(userId, false);
        return (int) Math.ceil(count / 9.0);
    }


    private int countCreatedTournaments(Long userId, boolean isFinished) {
        String sql = "SELECT COUNT(*) FROM tournament WHERE creator_id = ? AND is_finished = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId, isFinished);
    }


    @Override
    public List<Tournament> findByCreator(Long creator_id, Long page) {
        return jdbcTemplate.query("SELECT * FROM tournament t WHERE creator_id = ? LIMIT 9 OFFSET ?", ROW_MAPPER, creator_id, 9*page);
    }

    @Override
    public void setFinished(Long tournament_id) {
        jdbcTemplate.update("UPDATE tournament SET is_finished = true, end_date = CURRENT_DATE WHERE id = ?", tournament_id);
    }

    @Override
    public void closeInscriptions(Long tournament_id) {
        jdbcTemplate.update("UPDATE tournament SET open_inscriptions = false WHERE id = ?", tournament_id);
    }

    @Override
    public void setTournamentWinner(Long tournament_id, Long user_id){
        jdbcTemplate.update("UPDATE tournament SET tournament_winner = ? WHERE id = ?", user_id, tournament_id);
    }

    @Override
    public void startTournament(Long tournament_id) {
        jdbcTemplate.update(
                """
                UPDATE tournament
                   SET tournament_started = true,
                       end_date = CASE
                                    WHEN end_date IS NOT NULL AND end_date < CURRENT_DATE
                                      THEN CURRENT_DATE
                                    ELSE end_date
                                  END,
                       start_date = CURRENT_DATE WHERE id = ?
                """,
                tournament_id
        );
    }

    @Override
    public List<Tournament> findUserActiveTournaments(Long userId, Long page) {
        return findUserTournaments(userId, false, page);
    }

    @Override
    public List<Tournament> findUserPastTournaments(Long userId, Long page) {
        return findUserTournaments(userId, true, page);
    }

    private List<Tournament> findUserTournaments(Long userId, Boolean isFinished, Long page) {
        String sql = "SELECT t.* " +
                "FROM tournament t " +
                "INNER JOIN participant p ON p.tournament_id = t.id " +
                "WHERE p.user_id = ? AND t.is_finished = ? " +
                "LIMIT 9 OFFSET ?; ";
        return jdbcTemplate.query(sql, ROW_MAPPER, userId, isFinished, 9*page);
    }

    private int countUserTournaments(Long userId, Boolean isFinished) {
        String sql = "SELECT COUNT(*) " +
                "FROM tournament t " +
                "INNER JOIN participant p ON p.tournament_id = t.id " +
                "WHERE p.user_id = ? AND t.is_finished = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId, isFinished);
    }

    @Override
    public Integer getUserActiveTournamentsPages(Long userId) {
        int count = countUserTournaments(userId, false);
        return (int) Math.ceil(count / 9.0);
    }

    @Override
    public Integer getUserPastTournamentsPages(Long userId) {
        int count = countUserTournaments(userId, true);
        return (int) Math.ceil(count / 9.0);
    }


    @Override
    public List<Tournament> findTournaments(TournamentFilter filter, Long page) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder sql = new StringBuilder("SELECT * FROM tournament t");

        sql.append(buildTournamentFilterSql(filter, params));

        sql.append(" LIMIT :limit OFFSET :offset");
        params.addValue("limit", 9);
        params.addValue("offset", 9*page);

        return namedJdbcTemplate.query(sql.toString(), params, ROW_MAPPER);
    }

    private String buildTournamentFilterSql(TournamentFilter filter, MapSqlParameterSource params) {

        StringBuilder sql = new StringBuilder();

        boolean needsGameJoin = filter.getGenre() != null;
        boolean needsFormatJoin = filter.getPlayersPerTeam() != null;

        if (needsGameJoin) {
            sql.append(" JOIN game g ON t.game_id = g.id");
        }
        if (needsFormatJoin) {
            sql.append(" LEFT JOIN game_format gf ON t.game_id = gf.game_id AND t.format_id = gf.id");
        }

        sql.append(" WHERE open_inscriptions = true");

        if (filter.getName() != null) {
            sql.append(" AND LOWER(t.name) LIKE LOWER(:name)");
            params.addValue("name", filter.getName());
        }
        if (filter.getGame_id() != null) {
            sql.append(" AND t.game_id = :game_id");
            params.addValue("game_id", filter.getGame_id());
        }
        if (filter.getElo() != null) {
            sql.append(" AND t.elo = :elo");
            params.addValue("elo", filter.getElo().name(), Types.OTHER);
        }
        if (filter.getRegion() != null) {
            sql.append(" AND t.region = :region");
            params.addValue("region", filter.getRegion().name(), Types.OTHER);
        }
        if (filter.getFormat() != null) {
            sql.append(" AND t.format = :format");
            params.addValue("format", filter.getFormat());
        }
        if (filter.getStructure() != null) {
            sql.append(" AND t.structure = :structure");
            params.addValue("structure", filter.getStructure().name(), Types.OTHER);
        }
        if (filter.getStart_date() != null) {
            sql.append(" AND t.start_date <= :start_date");
            params.addValue("start_date", filter.getStart_date());
        }
        if (filter.getEnd_date() != null) {
            sql.append(" AND t.end_date <= :end_date");
            params.addValue("end_date", filter.getEnd_date());
        }
        if (filter.getPlayersPerTeam() != null) {
            sql.append(" AND gf.players_per_team = :playersPerTeam");
            params.addValue("playersPerTeam", filter.getPlayersPerTeam());
        }
        if (filter.getGenre() != null) {
            sql.append(" AND g.genre = :genre");
            params.addValue("genre", filter.getGenre().name(), Types.OTHER);
        }
        return sql.toString();
    }
//end ward
    @Override
    public List<Tournament> searchByName(String name){
        String sql = "SELECT * " +
                "FROM tournament " +
                "WHERE LOWER(name) LIKE '%' || LOWER(?) || '%'";

        return jdbcTemplate.query(sql, ROW_MAPPER, name);
    }

    @Override
    public Map<Long,List<Tournament>> getUnfilteredTournamentPages(Long page) {
        String sql = """
            WITH top_games AS (
                SELECT g.id
                FROM game g
                JOIN tournament t2 ON g.id = t2.game_id
                GROUP BY g.id
                ORDER BY COUNT(t2.id) DESC
                LIMIT 3 OFFSET ?
            )
            SELECT t.*
            FROM tournament t
            WHERE t.game_id IN (SELECT id FROM top_games)
              AND t.open_inscriptions = true
              AND (
                  SELECT COUNT(*)
                  FROM tournament t2
                  WHERE t2.game_id = t.game_id
                    AND t2.start_date <= t.start_date
                    AND t2.open_inscriptions = true
              ) <= 9
            ORDER BY t.game_id, t.start_date
        """;
        List<Tournament> tournaments = jdbcTemplate.query(sql, ROW_MAPPER, page * 3);

        return tournaments.stream()
                .collect(Collectors.groupingBy(Tournament::getGame_id,
                        LinkedHashMap::new,
                        Collectors.toList()));
    }

    @Override
    public Integer getPageAmount(Integer pageSize, TournamentFilter tf) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder("SELECT ");

        if (tf.getGame_id() != null) {
            sql.append("COUNT(*) ");
        } else {
            sql.append("COUNT(DISTINCT t.game_id) ");
        }

        sql.append("FROM tournament t");
        sql.append(buildTournamentFilterSql(tf, params));

        Integer count = namedJdbcTemplate.queryForObject(sql.toString(), params, Integer.class);

        return (int) Math.ceil((double) count / pageSize);
    }

    @Override
    public void updateTournamentInfo(Long tournamentId, String name, LocalDate startDate, LocalDate endDate, Integer maxParticipants) {
        List<String> sets = new ArrayList<>();
        MapSqlParameterSource p = new MapSqlParameterSource().addValue("id", tournamentId);

        if (name != null) {
            sets.add("name = :name");
            p.addValue("name", name);
        }
        if (startDate != null) {
            sets.add("start_date = :start_date");
            p.addValue("start_date", startDate);
        }
        if (endDate != null) {
            sets.add("end_date = :end_date");
            p.addValue("end_date", endDate);
        }
        if (maxParticipants != null) {
            sets.add("max_participants = :max_participants");
            p.addValue("max_participants", maxParticipants);
        }

        if (sets.isEmpty()) {
            return;
        }

        String sql = "UPDATE tournament SET " + String.join(", ", sets) + " WHERE id = :id";
        namedJdbcTemplate.update(sql, p);
    }

    @Override
    public int getTournamentParticipantsCount(Long tournamentId){
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM participant WHERE tournament_id = ?",
                Integer.class, tournamentId
        );
    }

    @Override
    public Boolean getIsGroupStage(Long tournamentId){
        return jdbcTemplate.queryForObject(
                "SELECT is_group_stage FROM tournament WHERE id = ?",
                Boolean.class, tournamentId
        );
    }

    @Override
    public void updateAllStartDates() {
        jdbcTemplate.update(
                """
                UPDATE tournament
                   SET start_date = CURRENT_DATE
                 WHERE start_date < CURRENT_DATE
                   AND COALESCE(tournament_started, false) = false
                """
        );
    }

    @Override
    public void updateAllEndDates() {
        jdbcTemplate.update(
                """
                UPDATE tournament
                   SET end_date = CURRENT_DATE
                 WHERE end_date < CURRENT_DATE
                   AND COALESCE(is_finished, false) = false
                """
        );
    }

    @Override
    public boolean isClosed(Long tournamentId) {
        Boolean open = jdbcTemplate.queryForObject(
                "SELECT open_inscriptions FROM tournament WHERE id = ?",
                Boolean.class, tournamentId
        );
        return !open;
    }
}
