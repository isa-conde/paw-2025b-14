//package ar.edu.itba.paw.persistence.hibernate;
//
//import ar.edu.itba.paw.model.Game.GameFormat;
//import ar.edu.itba.paw.persistence.Hibernate.GameFormatHibernateDao;
//import ar.edu.itba.paw.persistence.TestConfig;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TestConfig.class)
//@Sql("classpath:db/init.sql")
//@Transactional
//@Rollback
//public class GameFormatHibernateDaoTest {
//    @Autowired
//    private GameFormatHibernateDao gameFormatHibernateDao;
//
//    private static final Long ID = 100L;
//
//    @Test
//    public void testGetNoFormats(){
//        List<GameFormat> ans = gameFormatHibernateDao.getFormats(ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans.isEmpty());
//    }
//
//    @Test
//    public void testGetFormats(){
//        List<GameFormat> ans = gameFormatHibernateDao.getFormats(ID+3);
//
//        Assert.assertNotNull(ans);
//        Assert.assertEquals(1,ans.size());
//        Assert.assertEquals("formi",ans.get(0).getName());
//        Assert.assertEquals(6,ans.get(0).getPlayersPerTeam());
//        Assert.assertEquals(ID, ans.get(0).getId());
//        Assert.assertEquals(ID+3,ans.get(0).getGame().getId().longValue());
//    }
//
//    @Test
//    public void testFindById(){
//        Optional<GameFormat> ans = gameFormatHibernateDao.findById(ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans.isPresent());
//        Assert.assertEquals("formi",ans.get().getName());
//        Assert.assertEquals(6,ans.get().getPlayersPerTeam());
//        Assert.assertEquals(ID, ans.get().getId());
//        Assert.assertEquals(ID+3,ans.get().getGame().getId().longValue());
//    }
//
//    @Test
//    public void testFindNothing(){
//        Optional<GameFormat> ans = gameFormatHibernateDao.findById(0L);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans.isEmpty());
//    }
//}
