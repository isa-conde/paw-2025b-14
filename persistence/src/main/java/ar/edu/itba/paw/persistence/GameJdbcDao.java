package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.GameDao;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.Game.GameImg;
import ar.edu.itba.paw.model.enums.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.*;

@Repository
public class GameJdbcDao implements GameDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final SimpleJdbcInsert jdbcInsertFormat;
    private final SimpleJdbcInsert jdbcInsertImage;

    private static final RowMapper<Game> ROW_MAPPER = (rs, rowNum) -> new Game(rs.getLong("id"), rs.getString("name"), Genre.valueOf(rs.getString("genre")), rs.getInt("image_id"));

    private static final RowMapper<GameImg> ROW_MAPPER_IMG = (rs, rowNum) -> new GameImg(new Game(rs.getLong("id"), rs.getString("name"), Genre.valueOf(rs.getString("genre")), rs.getInt("image_id")), Base64.getEncoder().encodeToString(rs.getBytes("image")));

    private static final RowMapper<GameFormat> ROW_MAPPER_FORMAT = (rs, rowNum) -> new GameFormat(rs.getLong("id"), rs.getString("name"), rs.getInt("players_per_team"), rs.getLong("game_id"));

    @Autowired
    public GameJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .usingGeneratedKeyColumns("id")
                .withTableName("game");
        jdbcInsertFormat = new SimpleJdbcInsert(jdbcTemplate)
                .usingGeneratedKeyColumns("id")
                .withTableName("game_format");
        jdbcInsertImage = new SimpleJdbcInsert(jdbcTemplate)
                .usingGeneratedKeyColumns("id")
                .withTableName("image");
    }

    @Override
    public Optional<Game> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM game WHERE id = ?", ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public Optional<Game> findByName(String name) {
        return jdbcTemplate.query("SELECT * FROM game WHERE name = ?", ROW_MAPPER, name).stream().findFirst();
    }

    @Override
    public List<GameImg> searchByName(String name) {
        String sql = "SELECT g.*, i.image FROM game g " +
                     "LEFT JOIN image i ON g.image_id = i.id " +
                     "WHERE LOWER(g.name) LIKE '%' || LOWER(?) || '%'";
        return jdbcTemplate.query(sql, ROW_MAPPER_IMG, name);
    }

    @Override
    public List<Game> searchByGenre(Genre genre) {
        return jdbcTemplate.query("SELECT * FROM game WHERE genre = ?", ROW_MAPPER, genre.name());
    }

    @Override
    public List<Game> findAll() {
        return jdbcTemplate.query("SELECT * FROM game", ROW_MAPPER);
    }

    @Override
    public Game create(String name, Genre genre, Integer image_id) {
        SqlParameterSource values = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("genre", genre.name(), Types.OTHER)
                .addValue("image_id", image_id);

        Number key = jdbcInsert.executeAndReturnKey(values);
        return new Game(key.longValue(), name, genre, image_id);
    }

    @Override
    public Game createWithFormats(String name, Genre genre, List<GameFormat> formats, byte[] image) {
        SqlParameterSource img = new MapSqlParameterSource().addValue("image", image);
        Integer image_id = jdbcInsertImage.executeAndReturnKey(img).intValue();

        Game game = this.create(name, genre, image_id);

        for (GameFormat f : formats) {
            SqlParameterSource values = new MapSqlParameterSource()
                    .addValue("name", f.getName())
                    .addValue("players_per_team", f.getPlayers_per_team())
                    .addValue("game_id", game.getId());
            jdbcInsertFormat.execute(values);
        }
        return game;
    }

    @Override
    public List<GameImg> findAllWithImg() {
        return jdbcTemplate.query("SELECT g.*, i.image FROM game g LEFT JOIN image i ON g.image_id = i.id;", ROW_MAPPER_IMG);
    }

    @Override
    public Optional<GameImg> findByIdWithImage(long id) {
        return jdbcTemplate.query("SELECT g.*, i.image FROM game g LEFT JOIN image i ON g.image_id = i.id WHERE g.id = ?", ROW_MAPPER_IMG, id
        ).stream().findFirst();
    }

    @Override
    public List<GameFormat> getFormats(Long gameId) {
        return jdbcTemplate.query("SELECT * FROM game_format WHERE game_id = ?", ROW_MAPPER_FORMAT, gameId);
    }

}