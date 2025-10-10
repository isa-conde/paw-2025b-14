package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.persistence.Jdbc.ImageJdbcDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class ImageJdbcDaoTest {
    private static final byte[] IMAGE = "0".repeat(64).getBytes(StandardCharsets.UTF_8);
    private List<Long> USED_IDS;
    private int rows;

    @Autowired
    private DataSource ds;

    @Autowired
    private ImageJdbcDao imageJdbcDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        USED_IDS = new ArrayList<>();
        jdbcTemplate = new JdbcTemplate(ds);
        SimpleJdbcInsert imageInsert = new SimpleJdbcInsert(jdbcTemplate)
                .usingGeneratedKeyColumns("id")
                .withTableName("image");
        for(int i=1; i<10; i++){
            USED_IDS.add(imageInsert.executeAndReturnKey(Map.of("image", String.valueOf(i).repeat(64).getBytes(StandardCharsets.UTF_8))).longValue());
        }
        rows = JdbcTestUtils.countRowsInTable(jdbcTemplate,"image");
    }

    @Test
    public void testInsert(){
        final Long ans = imageJdbcDao.insertImage(IMAGE);
        byte[] retrieved = jdbcTemplate.queryForObject("select image from image where id = ?", byte[].class, ans);

        Assert.assertNotNull(retrieved);
        Assert.assertArrayEquals(IMAGE, retrieved);
        Assert.assertEquals(rows+1, JdbcTestUtils.countRowsInTable(jdbcTemplate,"image"));
    }

    @Test
    public void testFindById(){
        final Optional<byte[]> ans = imageJdbcDao.findById(USED_IDS.get(0));

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isPresent());
        Assert.assertArrayEquals("1".repeat(64).getBytes(StandardCharsets.UTF_8), ans.get());
    }

    @Test
    public void testFindNothing(){
        final Optional<byte[]> ans = imageJdbcDao.findById((long) -1);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testUpdateImage(){
        imageJdbcDao.updateImage(USED_IDS.get(0),IMAGE);
        byte[] retrieved = jdbcTemplate.queryForObject("select image from image where id = ?", byte[].class, USED_IDS.get(0));

        Assert.assertNotNull(retrieved);
        Assert.assertNotEquals("1".repeat(64).getBytes(StandardCharsets.UTF_8), retrieved);
        Assert.assertArrayEquals(IMAGE, retrieved);
    }
}
