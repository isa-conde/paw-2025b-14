package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Optional;

@Repository
public class ImageJdbcDao implements ImageDao {

    private final JdbcTemplate template;
    private final SimpleJdbcInsert jdbcInsert;

    private static final RowMapper<byte[]> ROW_MAPPER = ((rs, rowNum) -> rs.getBytes("image"));

    public ImageJdbcDao (final DataSource ds){
        this.template = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(template)
                .withTableName("image")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<byte[]> findById(Long id) {
        return template.query("SELECT * FROM image WHERE id = ?", ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public Long insertImage(byte[] image) {
        SqlParameterSource img = new MapSqlParameterSource().addValue("image", image);
        return jdbcInsert.executeAndReturnKey(img).longValue();
    }

    @Override
    public void updateImage(Long id, byte[] image){
        template.update("UPDATE image SET image = ? WHERE id = ?", image, id);
    }
}
