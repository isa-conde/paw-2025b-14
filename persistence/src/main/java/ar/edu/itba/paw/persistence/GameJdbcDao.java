package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.GameDao;
import ar.edu.itba.paw.model.Game;
import ar.edu.itba.paw.model.User;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class GameJdbcDao implements GameDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final RowMapper<Game> ROW_MAPPER = (rs, rowNum) -> new Game(rs.getLong("id"), rs.getString("name"), Genre.valueOf(rs.getString("genre")));

    @Autowired
    public GameJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .usingGeneratedKeyColumns("id")
                .withTableName("game");
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
    public List<Game> searchByName(String name) {
        String sql = "SELECT * FROM game WHERE name ILIKE '%' || ? || '%'";
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
    public Game create(String name, Genre genre) {
        SqlParameterSource values = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("genre", genre.name(), Types.OTHER);

        Number key = jdbcInsert.executeAndReturnKey(values);
        return new Game(key.longValue(), name, genre);
    }

}
