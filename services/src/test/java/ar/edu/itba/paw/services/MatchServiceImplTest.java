package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.TournamentNotFoundException;
import ar.edu.itba.paw.interfaces.persistence.MatchDao;
import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Match.Match;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Tournament;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class MatchServiceImplTest {
    @Mock
    private MatchDao matchDao;
    @Mock
    private ParticipantDao participantDao;
    @Mock
    private TournamentDao tournamentDao;
    @Mock
    private TournamentService ts;
    @InjectMocks
    private MatchServiceImpl matchService;

    private static final Long ID = 1L;

    @Test(expected = TournamentNotFoundException.class)
    public void testGetMatchesNoTournament(){
        Mockito.when(ts.findById(ID)).thenReturn(Optional.empty());

        matchService.getTournamentMatchesByStage(ID, null);
    }

    @Test
    public void testGetMatchesSimple() {
        Tournament tournament = Mockito.mock(Tournament.class);
        Mockito.when(ts.findById(ID)).thenReturn(Optional.of(tournament));
        Mockito.when(tournament.getFormatEntity()).thenReturn(null);

        List<Match> matches = new ArrayList<>();
        Match m1 = Mockito.mock(Match.class);
        Mockito.when(m1.getId()).thenReturn(1L);
        Mockito.when(m1.getLocalId()).thenReturn(10L);
        Mockito.when(m1.getVisitorId()).thenReturn(20L);
        Mockito.when(m1.getStage()).thenReturn(1);
        Mockito.when(m1.getIsGroupStage()).thenReturn(false);

        Match m2 = Mockito.mock(Match.class);
        Mockito.when(m2.getId()).thenReturn(2L);
        Mockito.when(m2.getLocalId()).thenReturn(30L);
        Mockito.when(m2.getVisitorId()).thenReturn(40L);
        Mockito.when(m2.getStage()).thenReturn(2);
        Mockito.when(m2.getIsGroupStage()).thenReturn(false);

        matches.add(m2);
        matches.add(m1);

        Mockito.when(matchDao.getTournamentMatches(ID, 1)).thenReturn(matches);

        Participant p10 = Mockito.mock(Participant.class);
        Participant p20 = Mockito.mock(Participant.class);
        Participant p30 = Mockito.mock(Participant.class);
        Participant p40 = Mockito.mock(Participant.class);

        Mockito.when(participantDao.getTournamentParticipantById(ID, 10L, 1)).thenReturn(p10);
        Mockito.when(participantDao.getTournamentParticipantById(ID, 20L, 1)).thenReturn(p20);
        Mockito.when(participantDao.getTournamentParticipantById(ID, 30L, 1)).thenReturn(p30);
        Mockito.when(participantDao.getTournamentParticipantById(ID, 40L, 1)).thenReturn(p40);

        Map<Integer, List<Match>> result = matchService.getTournamentMatchesByStage(ID, 1);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());

        List<Match> stage1Matches = result.get(1);
        Assert.assertNotNull(stage1Matches);
        Assert.assertEquals(1, stage1Matches.size());
        Assert.assertSame(m1, stage1Matches.getFirst());

        List<Match> stage2Matches = result.get(2);
        Assert.assertNotNull(stage2Matches);
        Assert.assertEquals(1, stage2Matches.size());
        Assert.assertSame(m2, stage2Matches.getFirst());
    }
}
