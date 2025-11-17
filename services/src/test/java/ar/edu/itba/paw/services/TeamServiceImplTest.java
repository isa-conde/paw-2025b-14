package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceImplTest {
    @Mock
    private ImageDao imageDao;
    @Mock
    private TeamDao teamDao;
    @Mock
    private TeamMemberDao teamMemberDao;
    @Mock
    private UserDao userDao;
    @Mock
    private TournamentDao tournamentDao;
    @Mock
    private TournamentService ts;
    @InjectMocks
    private TeamServiceImpl teamService;

    private static final Long ID = 1L;
    private static final String NAME = "name";

    @Test(expected = UserNotFoundException.class)
    public void testCreatePhantomCreator(){
        Mockito.when(userDao.findById(ID)).thenReturn(Optional.empty());

        teamService.create(NAME,null,null,ID, List.of());
    }

    @Test
    public void testCreate(){
        User owner = createMockUser(ID, NAME);
        Mockito.when(userDao.findById(ID)).thenReturn(Optional.of(owner));

        User member1 = createMockUser(2L, "member1");
        User member2 = createMockUser(3L, "member2");
        Mockito.when(userDao.findByUsername("member1")).thenReturn(Optional.of(member1));
        Mockito.when(userDao.findByUsername("member2")).thenReturn(Optional.of(member2));
        Mockito.when(userDao.findByUsername(NAME)).thenReturn(Optional.of(owner));

        Team createdTeam = createMockTeam(ID, NAME, ID);
        Mockito.when(teamDao.create(NAME, null, null, ID)).thenReturn(createdTeam);

        List<String> members = Arrays.asList("member1", "member2", NAME);

        // Ejecutar
        Team result = teamService.create(NAME, null, null,ID, members);

        // Verificaciones
        Assert.assertNotNull(result);
        Assert.assertEquals(createdTeam, result);
    }

    @Test(expected = UserNotFoundException.class)
    public void testCreateMemberNotFound() {
        // Setup: Owner existe, pero un member no
        User owner = createMockUser(ID, NAME);
        Mockito.when(userDao.findById(ID)).thenReturn(Optional.of(owner));
        User someone = createMockUser(2L, "validMember");
        Team team = createMockTeam(ID,NAME,ID);
        Mockito.when(teamDao.create(NAME,null,null,ID)).thenReturn(team);
        Mockito.when(userDao.findByUsername("validMember")).thenReturn(Optional.of(someone));
        Mockito.when(userDao.findByUsername("invalidMember")).thenReturn(Optional.empty()); // Lanza exception

        List<String> members = Arrays.asList("validMember", "invalidMember");

        // Ejecutar: Debe lanzar en el loop al encontrar invalid
        teamService.create(NAME, null, null, ID, members);
    }

    private User createMockUser(Long id, String username) {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(id);
        Mockito.when(user.getUsername()).thenReturn(username);
        return user;
    }

    private Team createMockTeam(Long id, String name, Long ownerId) {
        Team team = Mockito.mock(Team.class);
        Mockito.when(team.getId()).thenReturn(id);
        Mockito.when(team.getName()).thenReturn(name);
        // Agrega más getters si necesitas verificarlos
        return team;
    }
}
