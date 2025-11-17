package ar.edu.itba.paw.persistence.hibernate;

import ar.edu.itba.paw.model.Comment;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.Hibernate.CommentHibernateDao;
import ar.edu.itba.paw.persistence.TestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class CommentHibernateDaoTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource ds;

    @Autowired
    private CommentHibernateDao commentHibernateDao;

    private JdbcTemplate jdbcTemplate;
    private static final String COMMENT = "Hola fernan";
    private static final Long ID = 100L;
    private static final int COMMENTS_PAGE_SIZE = 6;
    private static final User user =new User(ID,"janedoe", "another@mail.com","1234567890",false,null,null,null,"es");
    private static final User noFriends = new User(ID+1,"janetdoe", "another2@mail.com","1234567890",false,null,null,null,"es");

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate(){
        commentHibernateDao.create(user,user,COMMENT);
        em.flush();

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"comments",
                "commenter_id = receiver_id and commenter_id = " + ID + " and comment = '" +
                        COMMENT + "'"));
    }

    @Test
    public void testGetNoComments(){
        List<Comment> ans = commentHibernateDao.getCommentsByReceived(noFriends,0L);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testGetCommentsEmptyPage(){
        List<Comment> ans = commentHibernateDao.getCommentsByReceived(user,2L);

        Assert.assertNotNull(ans);
        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testGetCommentsLastPage(){
        List<Comment> ans = commentHibernateDao.getCommentsByReceived(user,1L);

        Assert.assertNotNull(ans);
        Assert.assertEquals(1,ans.size());
        Assert.assertEquals(ID+6,ans.get(0).getId().longValue());
        Assert.assertEquals(ID.longValue(),ans.get(0).getReceiver().getId());
        Assert.assertEquals(ID+1,ans.get(0).getCommenter().getId());
        Assert.assertEquals("First",ans.get(0).getComment());
        Assert.assertEquals(LocalDateTime.of(2025,11,16,22,4,7),ans.get(0).getCreatedAt());
    }

    @Test
    public void testGetCommentsFullPage(){
        List<Comment> ans = commentHibernateDao.getCommentsByReceived(user,0L);

        Assert.assertNotNull(ans);
        Assert.assertEquals(COMMENTS_PAGE_SIZE,ans.size());
        for (int i = 0; i < COMMENTS_PAGE_SIZE; i++) {
            Assert.assertEquals(ID+i,ans.get(i).getId().longValue());
            Assert.assertEquals(ID.longValue(),ans.get(i).getReceiver().getId());
            Assert.assertEquals(ID+1,ans.get(i).getCommenter().getId());
            Assert.assertEquals("First",ans.get(i).getComment());
            Assert.assertEquals(LocalDateTime.of(2025,11,16,22,4,1+i),ans.get(i).getCreatedAt());
        }
    }

    @Test
    public void testGetCommentPages(){
        int ans = commentHibernateDao.getCommentPagesByReceived(user);

        Assert.assertEquals((int) Math.ceil((double) 7 / COMMENTS_PAGE_SIZE), ans);
    }

    @Test
    public void testGetNoPages(){
        int ans = commentHibernateDao.getCommentPagesByReceived(noFriends);

        Assert.assertEquals(0,ans);
    }
}
