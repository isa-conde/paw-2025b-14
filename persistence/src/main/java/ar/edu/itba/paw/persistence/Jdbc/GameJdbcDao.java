package ar.edu.itba.paw.persistence.Jdbc;

import ar.edu.itba.paw.interfaces.persistence.GameDao;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
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


public class GameJdbcDao implements GameDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final SimpleJdbcInsert jdbcInsertFormat;
    private final SimpleJdbcInsert jdbcInsertImage;
    private final SimpleJdbcInsert jdbcInsertFavourites;

    private static final RowMapper<Game> ROW_MAPPER = (rs, rowNum) -> new Game(rs.getLong("id"), rs.getString("name"), Genre.valueOf(rs.getString("genre")), rs.getInt("image_id"));

    private static final RowMapper<GameFormat> ROW_MAPPER_FORMAT = (rs, rowNum) -> new GameFormat(rs.getLong("id"), rs.getString("name"), rs.getInt("players_per_team"));

    //@Autowired
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
        jdbcInsertFavourites = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user_favourites");
    }

    @Override
    public Optional<Game> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM game WHERE id = ?", ROW_MAPPER, id).stream().findFirst();
    }

    //@Override
    public Optional<Game> findByName(String name) {
        return jdbcTemplate.query("SELECT * FROM game WHERE name = ?", ROW_MAPPER, name).stream().findFirst();
    }

    @Override
    public List<Game> searchByName(String name) {
        String sql = "SELECT * FROM game " +
                     "WHERE LOWER(name) LIKE '%' || LOWER(?) || '%'";
        return jdbcTemplate.query(sql, ROW_MAPPER, name);
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

    //@Override
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
    public boolean checkNameExists(String name){
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM game WHERE name = ?", Integer.class, name);
        return count > 0;
    }
    //@Override
    public List<GameFormat> getFormats(Long gameId) {
        return jdbcTemplate.query("SELECT * FROM game_format WHERE game_id = ?", ROW_MAPPER_FORMAT, gameId);
    }

    @Override
    public List<Game> getFavourites(Long userId) {
        final String sql = """
            SELECT g.*
            FROM (
              SELECT t.game_id, COUNT(DISTINCT t.id) AS tournament_count
              FROM tournament t
              WHERE EXISTS (
                SELECT 1 FROM participant p
                WHERE p.tournament_id = t.id
                  AND p.user_id       = ?
              )
              GROUP BY t.game_id
            ) fav
            JOIN game g ON g.id = fav.game_id
            ORDER BY fav.tournament_count DESC, g.name ASC
            LIMIT 6
        """;
        return jdbcTemplate.query(sql, ROW_MAPPER, userId);
    }

    @Override
    public List<Game> findAllPaged(Long page){
        String sql = "SELECT * FROM game " +
                     "LIMIT 9 " +
                     "OFFSET ? ";
        return jdbcTemplate.query(sql, ROW_MAPPER, page * 9);
    }

    @Override
    public Long getPageAmount() {
        Double gameAmount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM game", Double.class);
        return (long) Math.ceil( gameAmount / 9L);
    }

    //@Override
    public Optional<GameFormat> getFormatById(Long id) {
        return jdbcTemplate.query("SELECT * FROM game_format WHERE id = ?", ROW_MAPPER_FORMAT, id).stream().findFirst();
    }

    //@Override
    public Integer getPlayersPerTeam(Long id){
        List<Integer> results = jdbcTemplate.query(
                "SELECT players_per_team FROM game_format WHERE id = ?",
                (rs, rowNum) -> rs.getInt("players_per_team"),
                id
        );
        return results.isEmpty() ? null : results.get(0);
    }
}