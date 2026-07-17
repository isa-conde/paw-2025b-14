package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.interfaces.persistence.TeamDao;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ParticipantServiceImplTest {
    @Mock
    private ParticipantDao participantDao;
    @Mock
    private TournamentDao tournamentDao;
    @Mock
    private TournamentService ts;
    @Mock
    private UserDao userDao;
    @Mock
    private TeamDao teamDao;
    @Mock
    private MailService ms;
    @InjectMocks
    private ParticipantServiceImpl participantService;

    private static final Long ID = 1L;
    private static final Long OTHER_ID = 2L;

    @Test
    public void testGetTournamentParticipantsTeamsCase() {
        // Setup: teamSize > 1
        int teamSize = 2;

        // Crear participants desordenados
        List<Participant> unsortedParticipants = new ArrayList<>();

        Participant p1 = mockParticipant("Team A", 10, 5); // Alto points, alto diff
        Participant p2 = mockParticipant("Team B", 10, 3); // Mismo points, menor diff
        Participant p3 = mockParticipant("Team C", 5, 10); // Menor points
        Participant p4 = mockParticipant("Team D", 15, 0); // Alto points

        // Agregar desordenado
        unsortedParticipants.add(p3);
        unsortedParticipants.add(p1);
        unsortedParticipants.add(p4);
        unsortedParticipants.add(p2);

        // Mock del DAO para teams
        Mockito.when(participantDao.getTournamentParticipantTeams(ID)).thenReturn(unsortedParticipants);

        // Ejecutar
        List<Participant> result = participantService.getTournamentParticipants(ID, teamSize);

        // Verificar: Orden descendente por points, luego por scoreDifference
        // Esperado: p4 (15,0), p1 (10,5), p2 (10,3), p3 (5,10)
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(p4, result.get(0));
        Assert.assertEquals(p1, result.get(1));
        Assert.assertEquals(p2, result.get(2));
        Assert.assertEquals(p3, result.get(3));
    }

    @Test
    public void testGetTournamentParticipantsUsersCase() {
        // Setup: teamSize <= 1
        int teamSize = 1;

        // Crear participants desordenados
        List<Participant> unsortedParticipants = new ArrayList<>();

        Participant p1 = mockParticipant("User A", 20, 8); // Alto points, alto diff
        Participant p2 = mockParticipant("User B", 20, 6); // Mismo points, menor diff
        Participant p3 = mockParticipant("User C", 10, 15); // Menor points
        Participant p4 = mockParticipant("User D", 25, -2); // Alto points, negativo diff

        // Agregar desordenado
        unsortedParticipants.add(p2);
        unsortedParticipants.add(p4);
        unsortedParticipants.add(p1);
        unsortedParticipants.add(p3);

        // Mock del DAO para users
        Mockito.when(participantDao.getTournamentParticipantUsers(ID)).thenReturn(unsortedParticipants);

        // Ejecutar
        List<Participant> result = participantService.getTournamentParticipants(ID, teamSize);

        // Verificar: Orden descendente por points, luego por scoreDifference
        // Esperado: p4 (25,-2), p1 (20,8), p2 (20,6), p3 (10,15)
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(p4, result.get(0));
        Assert.assertEquals(p1, result.get(1));
        Assert.assertEquals(p2, result.get(2));
        Assert.assertEquals(p3, result.get(3));
    }

    @Test
    public void testJoinTournamentUserClosesInscriptionsWithCountQuery() {
        Tournament tournament = Mockito.mock(Tournament.class);
        User user = Mockito.mock(User.class);
        User creator = Mockito.mock(User.class);

        Mockito.when(participantDao.hasJoined(ID, OTHER_ID)).thenReturn(false);
        Mockito.when(tournamentDao.findById(OTHER_ID)).thenReturn(Optional.of(tournament));
        Mockito.when(tournament.getMaxParticipants()).thenReturn(4);
        Mockito.when(tournament.getCreatorId()).thenReturn(OTHER_ID);
        Mockito.when(tournament.getName()).thenReturn("Tournament");
        Mockito.when(participantDao.countTournamentParticipantUsers(OTHER_ID)).thenReturn(4);
        Mockito.when(userDao.findById(ID)).thenReturn(Optional.of(user));
        Mockito.when(userDao.findById(OTHER_ID)).thenReturn(Optional.of(creator));
        Mockito.when(user.getUsername()).thenReturn("player");
        Mockito.when(user.getEmail()).thenReturn("player@mail.com");
        Mockito.when(creator.getUsername()).thenReturn("creator");
        Mockito.when(creator.getEmail()).thenReturn("creator@mail.com");

        participantService.joinTournamentUser(ID, OTHER_ID);

        Mockito.verify(ts).closeInscriptions(OTHER_ID);
        Mockito.verify(participantDao, Mockito.never()).getTournamentParticipantUsers(OTHER_ID);
    }

    @Test
    public void testJoinTournamentTeamClosesInscriptionsWithCountQuery() {
        Tournament tournament = Mockito.mock(Tournament.class);
        Team team = Mockito.mock(Team.class);
        User creator = Mockito.mock(User.class);
        User user = Mockito.mock(User.class);

        Mockito.when(tournamentDao.findById(ID)).thenReturn(Optional.of(tournament));
        Mockito.when(teamDao.findById(OTHER_ID)).thenReturn(Optional.of(team));
        Mockito.when(tournament.getCreatorId()).thenReturn(ID);
        Mockito.when(tournament.getName()).thenReturn("Tournament");
        Mockito.when(tournament.getMaxParticipants()).thenReturn(2);
        Mockito.when(tournament.getFormatEntity()).thenReturn(null);
        Mockito.when(team.getName()).thenReturn("Team");
        Mockito.when(userDao.findById(ID)).thenReturn(Optional.of(creator));
        Mockito.when(userDao.findById(OTHER_ID)).thenReturn(Optional.of(user));
        Mockito.when(creator.getUsername()).thenReturn("creator");
        Mockito.when(creator.getEmail()).thenReturn("creator@mail.com");
        Mockito.when(user.getUsername()).thenReturn("player");
        Mockito.when(user.getEmail()).thenReturn("player@mail.com");
        Mockito.when(participantDao.countTournamentParticipantTeams(ID)).thenReturn(2);

        participantService.joinTournamentTeam(ID, OTHER_ID, List.of(OTHER_ID));

        Mockito.verify(ts).closeInscriptions(ID);
        Mockito.verify(participantDao, Mockito.never()).getTournamentParticipantTeams(ID);
    }

    @Test
    public void testUpdateCreatorRatingUsesHistoricalCount() {
        User creator = new User();
        creator.setRating(4f);
        creator.setRatingCount(2);

        Mockito.when(participantDao.hasRated(OTHER_ID, ID)).thenReturn(false);
        Mockito.when(userDao.findById(ID)).thenReturn(Optional.of(creator));

        participantService.updateCreatorRating(ID, ID, OTHER_ID, 1f);

        Mockito.verify(participantDao).updateHasRated(OTHER_ID, ID);
        Mockito.verify(userDao).updateUserRating(ID, 3f, 3);
    }

    // Helper para crear mocks de Participant
    private Participant mockParticipant(String name, int points, int scoreDifference) {
        Participant p = new Participant(); // Asumiendo constructor vacío; ajusta si necesita params
        // Si Participant no es mockeable fácilmente, usa un builder o setters
        // Para simplicidad, asumimos setters
        p.setName(name); // Asumiendo que tiene name para identificación, pero no es necesario para asserts
        p.setPoints(points);
        p.setScoreDifference(scoreDifference);
        return p;
    }
}
