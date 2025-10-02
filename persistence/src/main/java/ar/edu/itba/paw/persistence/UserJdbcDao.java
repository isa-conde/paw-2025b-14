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


    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("username"), rs.getString("email"), rs.getString("password"), rs.getBoolean("verified"), rs.getString("bio"), rs.getLong("profile_picture_id"), rs.getLong("banner_id"));

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
    public User create(String username, String email, String password) {
        final Map<String, Object> values = Map.of("username", username, "email", email, "password", password, "verified", false);
        final Number key = jdbcInsert.executeAndReturnKey(values);

        return new User(key.longValue(), username, email, password, false, null, null, null);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jdbcTemplate.query(
                "SELECT * FROM users WHERE username = ?",
                ROW_MAPPER,
                username
        ).stream().findFirst();
    }

    @Override
    public void changePassword(long user_id, String newPassword) {
        jdbcTemplate.update("UPDATE users SET password = ? WHERE id = ?", newPassword, user_id);
    }

    @Override
    public void verifyUser(long user_id) {
        jdbcTemplate.update("UPDATE users SET verified = true WHERE id = ?", user_id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jdbcTemplate.query("SELECT * FROM users WHERE email = ?", ROW_MAPPER, email).stream().findFirst();
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

    @Override
    public void updateProfileInfo(Long userId, String username, String bio, Long pfp, Long banner){
        jdbcTemplate.update("UPDATE users SET username = ?, bio = ?, profile_picture_id = ?, banner_id = ? WHERE id = ?", username, bio, pfp, banner, userId);
    }

}
