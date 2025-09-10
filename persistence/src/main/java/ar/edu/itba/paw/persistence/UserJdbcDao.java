package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserJdbcDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("username"), rs.getString("email"));

    public UserJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .usingGeneratedKeyColumns("id")
                .withTableName("users");
    }

    @Override
    public Optional<User> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?", ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public User create(String username, String email) {
        final Map<String, Object> values = Map.of("username", username, "email", email);
        final Number key = jdbcInsert.executeAndReturnKey(values);

        return new User(key.longValue(), username, email);
    }

    @Override
    public Optional<User> authenticate(String username, String email) {
        return jdbcTemplate.query(
                "SELECT * FROM users WHERE username = ? AND email = ?",
                ROW_MAPPER,
                username,
                email
        ).stream().findFirst();
    }

    @Override
    public Boolean checkUsernameExists (String username) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE username = ?", Integer.class , username);
        return count != null && count > 0;
    }

    @Override
    public Boolean checkEmailExists(String email) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Integer.class , email);
        return count != null && count > 0;
    }
}
