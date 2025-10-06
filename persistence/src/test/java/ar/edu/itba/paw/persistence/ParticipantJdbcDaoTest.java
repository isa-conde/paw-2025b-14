package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Participant;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:db/init.sql")
@Transactional
@Rollback
public class ParticipantJdbcDaoTest {
    private static final Long ID = 1L;
    private static final String USERNAME = "johndoe";
    private static final String OTHER_USERNAME = "janedoe";
    private static final String OTHER_EMAIL = "another@mail.com";
    private static final String EMAIL = "some@mail.com";
    private static final String PASSWORD = "1234567890";
    private static final RowMapper<Participant> ROW_MAPPER_USER = (rs, rowNum) -> new Participant(
            rs.getLong("user_id"),
            rs.getString("username"),
            rs.getInt("points"),
            rs.getInt("group_number"),
            rs.getLong("profile_picture_id")
    );

    private static final RowMapper<Participant> ROW_MAPPER_TEAM = (rs, rowNum) -> new Participant(
            rs.getLong("team_id"),
            rs.getString("name"),
            rs.getInt("points"),
            rs.getInt("group_number"),
            rs.getLong("profile_picture_id")
    );

    @Autowired
    private DataSource ds;

    @Autowired
    private ParticipantJdbcDao participantJdbcDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("participant")
                .usingGeneratedKeyColumns("id");
        jdbcInsert.execute(Map.of("user_id",ID,"tournament_id",ID,"points",21,"group_number",1));
        jdbcInsert.execute(Map.of("user_id",ID+1,"tournament_id",ID,"points",2,"group_number",2));
        jdbcInsert.execute(Map.of("team_id",ID,"tournament_id",ID+1,"points",21,"group_number",1));
        jdbcInsert.execute(Map.of("team_id",ID+1,"tournament_id",ID+1,"points",2,"group_number",2));
        SimpleJdbcInsert userJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users");
        userJdbcInsert.execute(Map.of("id",ID,"email",EMAIL,"username",USERNAME,
                "password",PASSWORD,"verified",true));
        userJdbcInsert.execute(Map.of("id",ID+1,"email",OTHER_EMAIL,"username",OTHER_USERNAME,
                "password",PASSWORD,"verified",true));
        SimpleJdbcInsert teamJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("team");
        teamJdbcInsert.execute(Map.of("id",ID,"name",USERNAME, "owner_id",ID));
        teamJdbcInsert.execute(Map.of("id",ID+1,"name",OTHER_USERNAME, "owner_id",ID+1));
    }

    @Test
    public void testJoinUser(){
        participantJdbcDao.joinTournamentUser(ID+2,ID);

        Assert.assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate,"participant"));
        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "tournament_id = " + ID +
                        " and user_id = " + (ID+2)));
    }

    @Test
    public void testJoinTeam(){
        participantJdbcDao.joinTournamentTeam(ID+1,ID+2);

        Assert.assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate,"participant"));
        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "tournament_id = " + 2 +
                        " and team_id = " + (ID+2)));
    }

    @Test
    public void testJoinUserWithTeam(){
        participantJdbcDao.joinTournamentUserWithTeam(ID,ID+1,ID+2);

        Assert.assertEquals(5, JdbcTestUtils.countRowsInTable(jdbcTemplate,"participant"));
        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "tournament_id = " + 2 +
                " and team_id = " + (ID+2) +
                " and user_id = " + ID));
    }

//    @Test
//    public void testGetUsersInfo(){
//        SimpleJdbcInsert userJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("users");
//        userJdbcInsert.execute(Map.of("id",ID,"email",EMAIL,"username",USERNAME,
//                "password",PASSWORD,"verified",true));
//        userJdbcInsert.execute(Map.of("id",ID+1,"email",EMAIL+"a","username",USERNAME+"a",
//                "password",PASSWORD,"verified",true));
//        List<ParticipantInfo> ans = participantJdbcDao.getTournamentsParticipantUsersInfo(ID);
//
//        Assert.assertNotNull(ans);
//        Assert.assertFalse(ans.isEmpty());
//        Assert.assertEquals(2,ans.size());
//        for (int i = 0; i < 2; i++) {
//            Assert.assertEquals(ID+i,ans.get(i).getId().longValue());
//            Assert.assertEquals(ID+i,ans.get(i).getGroupNumber().longValue());
//            Assert.assertEquals((i==0)?21:2,ans.get(i).getPoints().intValue());
//            Assert.assertEquals(USERNAME + ((i==0)?"":"a"),ans.get(i).getName());
//        }
//    }

//    @Test
//    public void testGetNoOnesInfo(){
//        List<ParticipantInfo> ans = participantJdbcDao.getTournamentsParticipantUsersInfo((long) -1);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans.isEmpty());
//    }

//    @Test
//    public void testGetTeamsInfo(){
//        SimpleJdbcInsert teamJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("team");
//        teamJdbcInsert.execute(Map.of("id",ID,"name",USERNAME, "owner_id",ID));
//        teamJdbcInsert.execute(Map.of("id",ID+1,"name",USERNAME+"a", "owner_id",ID+1));
//        List<ParticipantInfo> ans = participantJdbcDao.getTournamentsParticipantTeamsInfo(ID+1);
//
//        Assert.assertNotNull(ans);
//        Assert.assertFalse(ans.isEmpty());
//        Assert.assertEquals(2,ans.size());
//        for (int i = 0; i < 2; i++) {
//            Assert.assertEquals(ID+i,ans.get(i).getId().longValue());
//            Assert.assertEquals(ID+i,ans.get(i).getGroupNumber().longValue());
//            Assert.assertEquals((i==0)?21:2,ans.get(i).getPoints().intValue());
//            Assert.assertEquals(USERNAME + ((i==0)?"":"a"),ans.get(i).getName());
//        }
//    }

//    @Test
//    public void testGetNoTeamsInfo(){
//        List<ParticipantInfo> ans = participantJdbcDao.getTournamentsParticipantTeamsInfo((long) -1);
//
//        Assert.assertNotNull(ans);
//        Assert.assertTrue(ans.isEmpty());
//    }

    @Test
    public void testGetUsers(){
        List<Participant> ans = participantJdbcDao.getTournamentParticipantUsers(ID);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(2,ans.size());
        for (int i = 0; i < 2; i++) {
            Assert.assertEquals(ID+i,ans.get(i).getId().longValue());
            Assert.assertEquals(ID+i,ans.get(i).getGroupNumber().longValue());
            Assert.assertEquals((i==0)?21:2,ans.get(i).getPoints().intValue());
            Assert.assertEquals((i==0)?USERNAME:OTHER_USERNAME,ans.get(i).getName());
        }
    }

    @Test
    public void testGetNoOne(){
        List<Participant> empty = participantJdbcDao.getTournamentParticipantUsers((long) -1);

        Assert.assertNotNull(empty);
        Assert.assertTrue(empty.isEmpty());
    }

    @Test
    public void testGetTeams(){
        List<Participant> ans = participantJdbcDao.getTournamentParticipantTeams(ID+1);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(2,ans.size());
        for (int i = 0; i < 2; i++) {
            Assert.assertEquals(ID+i,ans.get(i).getId().longValue());
            Assert.assertEquals(ID+i,ans.get(i).getGroupNumber().longValue());
            Assert.assertEquals((i==0)?21:2,ans.get(i).getPoints().intValue());
            Assert.assertEquals((i==0)?USERNAME:OTHER_USERNAME,ans.get(i).getName());
        }
    }

    @Test
    public void testGetNoTeams(){
        List<Participant> empty = participantJdbcDao.getTournamentParticipantTeams((long) -1);

        Assert.assertNotNull(empty);
        Assert.assertTrue(empty.isEmpty());
    }

    @Test
    public void testGetUserById(){
        Participant ans = participantJdbcDao.getTournamentParticipantByUserId(ID,ID);

        Assert.assertNotNull(ans);
        Assert.assertEquals(USERNAME,ans.getName());
        Assert.assertEquals(ID,ans.getId());
        Assert.assertEquals(Integer.valueOf(1),ans.getGroupNumber());
        Assert.assertEquals(Integer.valueOf(21),ans.getPoints());
    }

    @Test
    public void testGetByIdNoOne(){
        Participant ans = participantJdbcDao.getTournamentParticipantByUserId((long)0, (long)-1);

        Assert.assertNull(ans);
    }

    @Test
    public void testHasJoined(){
        boolean ans = participantJdbcDao.hasJoined(ID,ID);

        Assert.assertTrue(ans);
    }

    @Test
    public void testHasNotJoined(){
        boolean ans = participantJdbcDao.hasJoined(ID+2,ID);

        Assert.assertFalse(ans);
    }

    @Test
    public void testLeaveUser(){
        participantJdbcDao.leaveTournamentUser(ID,ID);

        Assert.assertEquals(3,JdbcTestUtils.countRowsInTable(jdbcTemplate,"participant"));
        Assert.assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "tournament_id = user_id and user_id = ID"));
    }

    @Test
    public void testLeaveNoOne(){
        participantJdbcDao.leaveTournamentUser(ID+2,ID);

        Assert.assertEquals(4,JdbcTestUtils.countRowsInTable(jdbcTemplate,"participant"));
    }

    @Test
    public void testUpdateGroupNumberUsers(){
        participantJdbcDao.updateGroupNumberForUsers(ID,3,List.of(ID,ID+1),1);

        Assert.assertEquals(2,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "group_number = 3 and user_id is not null"));
        Assert.assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "group_number <> 3 and user_id is not null"));
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "participant",
                "group_number = 3 and team_id is not null"));
    }

    @Test
    public void testUpdateGroupNumberTeams(){
        participantJdbcDao.updateGroupNumberForUsers(ID+1, 3, List.of(ID, ID + 1), 2);

        Assert.assertEquals(2, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "participant",
                "group_number = 3 and team_id is not null"));
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "participant",
                "group_number <> 3 and team_id is not null"));
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "participant",
                "group_number = 3 and user_id is not null"));
    }

    @Test
    public void testSwapGroups(){
        participantJdbcDao.swapGroups(ID,ID,ID+1,1,2,1);

        Assert.assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "group_number = user_id"));
        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "group_number = 1 and user_id = 2"));
        Assert.assertEquals(1,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"participant",
                "group_number = 2 and user_id = 1"));
    }

    @Test
    public void testGetMaxPoints(){
        int ans = participantJdbcDao.getTournamentMaxPoints(ID);

        Assert.assertEquals(21,ans);
    }

    @Test
    public void testGetSecondMaxPoints(){
        int ans = participantJdbcDao.getTournamentSecondMaxPoints(ID);

        Assert.assertEquals(2,ans);
    }

    @Test
    public void testGetGroupNumberUser(){
        int ans = participantJdbcDao.getGroupNumber(ID,ID,1);

        Assert.assertEquals(1,ans);
    }

    @Test
    public void testGetNoGroupNumberUser(){
        jdbcTemplate.update("update participant set group_number = null");
        int ans = participantJdbcDao.getGroupNumber(ID,ID,1);

        Assert.assertEquals(0,ans);
    }

    @Test
    public void testGetGroupNumberTeam(){
        int ans = participantJdbcDao.getGroupNumber(ID+1,ID,2);

        Assert.assertEquals(1,ans);
    }

    @Test
    public void testGetNoGroupNumberTeam(){
        jdbcTemplate.update("update participant set group_number = null");
        int ans = participantJdbcDao.getGroupNumber(ID+1,ID,2);

        Assert.assertEquals(0,ans);
    }

    @Test
    public void testGetUserByPoints(){
        List<Participant> ans = participantJdbcDao.getTournamentParticipantsByPoints(ID,null,21,1);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(1,ans.size());
        Assert.assertEquals(ID,ans.get(0).getId());
        Assert.assertEquals(21,ans.get(0).getPoints().intValue());
        Assert.assertEquals(1,ans.get(0).getGroupNumber().intValue());
        Assert.assertEquals(USERNAME,ans.get(0).getName());
    }

    @Test
    public void testGetUserByPointsInGroup(){
        jdbcTemplate.update("update participant set points = 21 where group_number = 2");
        List<Participant> ans = participantJdbcDao.getTournamentParticipantsByPoints(ID,1,21,1);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(1,ans.size());
        Assert.assertEquals(ID,ans.get(0).getId());
        Assert.assertEquals(21,ans.get(0).getPoints().intValue());
        Assert.assertEquals(1,ans.get(0).getGroupNumber().intValue());
        Assert.assertEquals(USERNAME,ans.get(0).getName());
    }

    @Test
    public void testGetTeamByPoints(){
        List<Participant> ans = participantJdbcDao.getTournamentParticipantsByPoints(ID+1,null,21,2);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(1,ans.size());
        Assert.assertEquals(ID,ans.get(0).getId());
        Assert.assertEquals(21,ans.get(0).getPoints().intValue());
        Assert.assertEquals(1,ans.get(0).getGroupNumber().intValue());
        Assert.assertEquals(USERNAME,ans.get(0).getName());
    }

    @Test
    public void testGetTeamByPointsInGroup(){
        jdbcTemplate.update("update participant set points = 21 where group_number = 2");
        List<Participant> ans = participantJdbcDao.getTournamentParticipantsByPoints(ID+1,1,21,2);

        Assert.assertNotNull(ans);
        Assert.assertFalse(ans.isEmpty());
        Assert.assertEquals(1,ans.size());
        Assert.assertEquals(ID,ans.get(0).getId());
        Assert.assertEquals(21,ans.get(0).getPoints().intValue());
        Assert.assertEquals(1,ans.get(0).getGroupNumber().intValue());
        Assert.assertEquals(USERNAME,ans.get(0).getName());
    }

    @Test
    public void testGetGroups(){
        int ans = participantJdbcDao.getTournamentGroups(ID);

        Assert.assertEquals(2,ans);
    }

    @Test
    public void testGetNoGroups(){
        int ans = participantJdbcDao.getTournamentGroups(ID+2);

        Assert.assertEquals(0,ans);
    }

    @Test
    public void testAddPoints(){
        participantJdbcDao.sumPoints(ID,ID+1,3);
        int ans = jdbcTemplate.queryForObject("select points from participant where tournament_id = ? and user_id = ?", Integer.class,ID,ID+1);

        Assert.assertEquals(5,ans);
    }
}
