package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.GameDao;
import ar.edu.itba.paw.model.Game;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Genre;
import org.springframework.beans.factory.annotation.Autowired;
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
public class GameJdbcDao implements GameDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final RowMapper<Game> ROW_MAPPER = (rs, rowNum) -> new Game(rs.getLong("id"), rs.getString("name"), rs.getObject("genre", Genre.class));

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
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        params.put("genre", genre);

        Number id = jdbcInsert.executeAndReturnKey(params);
        return new Game(id.longValue(), name, genre);
    }
}
