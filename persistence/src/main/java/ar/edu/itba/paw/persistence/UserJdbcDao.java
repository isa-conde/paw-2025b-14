package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserJdbcDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("username"), rs.getString("email"), rs.getString("password"), rs.getBoolean("verified"), rs.getString("bio"), rs.getLong("profile_picture_id"), rs.getLong("banner_id"), rs.getString("locale"));

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

        return new User(key.longValue(), username, email, password, false, null, null, null, "es");
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
        return count > 0;
    }

    @Override
    public Boolean checkEmailExists(String email) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Integer.class , email);
        return count > 0;
    }

    @Override
    public void updateProfileInfo(Long userId, String username, String bio, Long pfp, Long banner){
        StringBuilder sql = new StringBuilder("UPDATE users SET ");
        List<Object> params = new ArrayList<>();

        sql.append("username = ?");
        params.add(username);

        sql.append(", bio = ?");
        params.add(bio);

        if (pfp != null) {
            sql.append(", profile_picture_id = ?");
            params.add(pfp);
        }

        if (banner != null) {
            sql.append(", banner_id = ?");
            params.add(banner);
        }

        sql.append(" WHERE id = ?");
        params.add(userId);

        jdbcTemplate.update(sql.toString(), params.toArray());    }

    @Override
    public List<User> searchByName(String name) {
        return jdbcTemplate.query("SELECT * FROM users WHERE LOWER(username) LIKE '%' || LOWER(?) || '%'", ROW_MAPPER, name);
    }

    @Override
    public void updateUserLocale(String locale, Long userId) {
        jdbcTemplate.update("UPDATE users SET locale = ? WHERE id = ?", locale, userId);
    }

}
