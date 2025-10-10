package ar.edu.itba.paw.persistence.Jdbc;

import ar.edu.itba.paw.interfaces.persistence.TokenDao;
import ar.edu.itba.paw.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Repository
public class TokenJdbcDao implements TokenDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsertToken;

    private static final RowMapper<Token> ROW_MAPPER_TOKEN = (rs, rowNum) -> new Token(rs.getLong("id"), rs.getLong("user_id"), rs.getLong("token"), rs.getDate("expiry_date").toLocalDate());

    @Autowired
    public TokenJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsertToken = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tokens")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Token create(Long user_id, Long token, LocalDate expiry_date) {
        final Map<String, Object> values = Map.of("user_id", user_id, "token", token, "expiry_date", expiry_date, "used", false);
        final Number key = jdbcInsertToken.executeAndReturnKey(values);

        return new Token(key.longValue(), user_id, token, expiry_date);
    }

    @Override
    public Optional<Token> findByToken(Long token) {
        return jdbcTemplate.query(
                "SELECT * FROM tokens WHERE token = ?",
                ROW_MAPPER_TOKEN,
                token
        ).stream().findFirst();
    }

    @Override
    public void markAsUsed(Long tokenId) {
        jdbcTemplate.update("UPDATE tokens SET used = ? WHERE id = ?", true, tokenId);
    }

    @Override
    public void deleteExpiredTokens() {
        jdbcTemplate.update("DELETE FROM tokens WHERE expiry_date < ? OR used = ?", LocalDate.now(), true);
    }
}
