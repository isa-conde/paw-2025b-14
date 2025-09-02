package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.swing.*;
import java.lang.reflect.Type;
import java.sql.Types;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TournamentJdbcDao implements TournamentDao {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final SimpleJdbcInsert jdbcInsertUser;
    private final SimpleJdbcInsert jdbcInsertTeam;
    private final SimpleJdbcInsert jdbcInsertImage;

    @Autowired
    public TournamentJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tournament")
                .usingGeneratedKeyColumns("id");
        this.jdbcInsertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("participant_user");
        this.jdbcInsertTeam = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("participant_team");
        this.jdbcInsertImage = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("image")
                .usingGeneratedKeyColumns("id");
    }

    private static final RowMapper<Tournament> ROW_MAPPER = (rs, rowNum) -> new Tournament(rs.getLong("id"),
            rs.getLong("creator_id"), rs.getString("name"), rs.getLong("game_id"), Region.valueOf(rs.getString("region")),
            Elo.valueOf(rs.getString("elo")), rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(),
            rs.getString("format"), Structure.valueOf(rs.getString("structure")), rs.getInt("max_participants"), rs.getInt("image_id"));

    private static final RowMapper<User> ROW_MAPPER_USER = (rs, rowNum) -> new User(rs.getLong("userId"), rs.getString("username"), rs.getString("email"));

    private static final RowMapper<TournamentImg> ROW_MAPPER_IMG = (rs, rowNum) -> new TournamentImg(new Tournament(rs.getLong("id"),
            rs.getLong("creator_id"), rs.getString("name"), rs.getLong("game_id"), Region.valueOf(rs.getString("region")),
            Elo.valueOf(rs.getString("elo")), rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(),
            rs.getString("format"), Structure.valueOf(rs.getString("structure")), rs.getInt("max_participants"), rs.getInt("image_id")), Base64.getEncoder().encodeToString(rs.getBytes("image")));


    @Override
    public Optional<Tournament> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM tournament WHERE id = ?", ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Tournament> findTournaments(TournamentFilter filter) {
        StringBuilder sql = new StringBuilder("SELECT * FROM tournament t WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (filter.getName() != null){
            sql.append(" AND t.name LIKE :name");
            params.addValue("name", filter.getName());
        }if (filter.getGame_id() != null){
            sql.append(" AND t.game_id = :game_id");
            params.addValue("game_id", filter.getGame_id());
        }if (filter.getElo() != null){
            sql.append(" AND t.elo = :elo");
            params.addValue("elo", filter.getElo(), Types.OTHER);
        }if (filter.getRegion() != null){
            sql.append(" AND t.region = :region");
            params.addValue("region", filter.getRegion(), Types.OTHER);
        }if (filter.getFormat() != null){
            sql.append(" AND t.format = :format");
            params.addValue("format", filter.getFormat());
        }if (filter.getStructure() != null){
            sql.append(" AND t.structure = :structure");
            params.addValue("structure", filter.getStructure(), Types.OTHER);
        }if (filter.getStart_date() != null){
            sql.append(" AND t.start_date < :start_date");
            params.addValue("start_date", filter.getStart_date());
        }if (filter.getEnd_date() != null){
            sql.append(" AND t.end_date < :end_date");
            params.addValue("end_date", filter.getEnd_date());
        }

        return namedJdbcTemplate.query(sql.toString(), params, ROW_MAPPER);
    }

    @Override
    public List<Tournament> findGameTournaments(Long game_id){
        return jdbcTemplate.query("SELECT * FROM tournament t WHERE game_id = ?", ROW_MAPPER, game_id);
    }

    @Override
    public Tournament create(Long creator_id, String name, Long game_id, Region region, Elo elo, LocalDate start_date, LocalDate end_date, String format, Structure structure, Integer max_participants, byte[] image) {

        SqlParameterSource img = new MapSqlParameterSource().addValue("image", image);
        Integer image_id = jdbcInsertImage.executeAndReturnKey(img).intValue();

        SqlParameterSource values = new MapSqlParameterSource()
                .addValue("creator_id", creator_id)
                .addValue("name", name)
                .addValue("game_id", game_id)
                .addValue("region", region, Types.OTHER)
                .addValue("elo", elo, Types.OTHER)
                .addValue("start_date", start_date)
                .addValue("end_date", end_date)
                .addValue("format", format)
                .addValue("structure", structure, Types.OTHER)
                .addValue("max_participants", max_participants)
                .addValue("image_id", image_id);

        Number key = jdbcInsert.executeAndReturnKey(values);
        return new Tournament(key.longValue(), creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id);
    }

    @Override
    public void joinTournamentUser(Long user_id, Long tournament_id) {
        Map<String, Object> values = new HashMap<>();

        values.put("user_id", user_id);
        values.put("tournament_id", tournament_id);

        jdbcInsertUser.execute(values);
    }

    @Override
    public void joinTournamentTeam(Long team_id, Long tournament_id) {
        Map<String, Object> values = new HashMap<>();

        values.put("team_id", team_id);
        values.put("tournament_id", tournament_id);

        jdbcInsertUser.execute(values);
    }

    @Override
    public List<User> getTournamentParticipants(Long tournament_id){
        return jdbcTemplate.query("SELECT u.id, u.username FROM users u" +
                                "JOIN participant_user p ON u.id = p.user_id" +
                                "WHERE p.tournament_id = ?", ROW_MAPPER_USER ,tournament_id);
    }

    @Override
    public List<TournamentImg> findWithImg(TournamentFilter filter) {
        StringBuilder sql = new StringBuilder(
                "SELECT t.*, i.image " +
                        "FROM tournament t " +
                        "LEFT JOIN image i ON t.image_id = i.id " +
                        "WHERE 1=1"
        );
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (filter.getName() != null){
            sql.append(" AND t.name LIKE = :name");
            params.addValue("name", filter.getName());
        }
        if (filter.getGame_id() != null){
            sql.append(" AND t.game_id = :game_id");
            params.addValue("game_id", filter.getGame_id());
        }
        if (filter.getElo() != null){
            sql.append(" AND t.elo = :elo");
            params.addValue("elo", filter.getElo(), Types.OTHER);
        }if (filter.getRegion() != null){
            sql.append(" AND t.region = :region");
            params.addValue("region", filter.getRegion(), Types.OTHER);
        }
        if (filter.getFormat() != null){
            sql.append(" AND t.format = :format");
            params.addValue("format", filter.getFormat());
        }
        if (filter.getStructure() != null){
            sql.append(" AND t.structure = :structure");
            params.addValue("structure", filter.getStructure(), Types.OTHER);
        }
        if (filter.getStart_date() != null){
            sql.append(" AND t.start_date < :start_date");
            params.addValue("start_date", filter.getStart_date());
        }
        if (filter.getEnd_date() != null){
            sql.append(" AND t.end_date < :end_date");
            params.addValue("end_date", filter.getEnd_date());
        }

        return namedJdbcTemplate.query(sql.toString(), params, ROW_MAPPER_IMG);
    }

    public Optional<TournamentImg> findByIdWithImg(Long id){
        return jdbcTemplate.query("SELECT t.*, i.image FROM tournament t LEFT JOIN image i ON t.image_id = i.id WHERE t.id = ?", ROW_MAPPER_IMG, id
        ).stream().findFirst();
    }
}
