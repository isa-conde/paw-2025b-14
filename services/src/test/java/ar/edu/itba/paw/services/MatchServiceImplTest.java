package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.TournamentNotFoundException;
import ar.edu.itba.paw.interfaces.exception.MatchNotFoundException;
import ar.edu.itba.paw.interfaces.exception.DrawInEliminationMatchException;
import ar.edu.itba.paw.interfaces.persistence.MatchDao;
import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.Match.Match;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.enums.Structure;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
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
    private static final Long OTHER_ID = 2L;
    private static final Long MATCH_ID = 5L;
    private static final Long LOCAL_ID = 10L;
    private static final Long VISITOR_ID = 20L;

    @Test(expected = TournamentNotFoundException.class)
    public void testGetMatchesNoTournament(){
        Mockito.when(ts.findById(ID)).thenReturn(Optional.empty());

        matchService.getTournamentMatchesByStage(ID, null);
    }

    @Test
    public void testGetMatch(){
        Tournament tournament = Mockito.mock(Tournament.class);
        Match match = Mockito.mock(Match.class);
        Mockito.when(ts.findById(ID)).thenReturn(Optional.of(tournament));
        Mockito.when(matchDao.getMatch(ID, OTHER_ID)).thenReturn(match);

        Assert.assertSame(match, matchService.getMatch(ID, OTHER_ID));
    }

    @Test(expected = MatchNotFoundException.class)
    public void testGetMatchNoMatch(){
        Tournament tournament = Mockito.mock(Tournament.class);
        Mockito.when(ts.findById(ID)).thenReturn(Optional.of(tournament));
        Mockito.when(matchDao.getMatch(ID, OTHER_ID)).thenReturn(null);

        matchService.getMatch(ID, OTHER_ID);
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

    @Test
    public void testSetMatchResultsLeagueSumsPointsAndScoreDifference() {
        Tournament tournament = tournament(Structure.LEAGUE, null);
        Match match = match(null);

        Mockito.when(ts.findById(ID)).thenReturn(Optional.of(tournament));
        Mockito.when(matchDao.getMatch(ID, MATCH_ID)).thenReturn(match);
        Mockito.when(matchDao.allMatchesPlayed(ID)).thenReturn(false);

        matchService.setMatchResults(MATCH_ID, ID, 5, 3);

        Mockito.verify(matchDao).setMatchResults(Mockito.eq(MATCH_ID), Mockito.eq(ID), Mockito.eq(5), Mockito.eq(3), Mockito.eq(1), Mockito.any(LocalDate.class));
        Mockito.verify(participantDao).sumPoints(ID, LOCAL_ID, 3, 2, 1);
        Mockito.verify(participantDao).sumPoints(ID, VISITOR_ID, 0, -2, 1);
    }

    @Test(expected = DrawInEliminationMatchException.class)
    public void testSetMatchResultsEliminationRejectsDraw() {
        Tournament tournament = tournament(Structure.ELIMINATION, null);
        Match match = match(null);

        Mockito.when(ts.findById(ID)).thenReturn(Optional.of(tournament));
        Mockito.when(matchDao.getMatch(ID, MATCH_ID)).thenReturn(match);

        matchService.setMatchResults(MATCH_ID, ID, 1, 1);
    }

    @Test
    public void testSetMatchResultsEliminationAdvancesWinner() {
        Tournament tournament = tournament(Structure.ELIMINATION, null);
        Match match = match(null);

        Mockito.when(ts.findById(ID)).thenReturn(Optional.of(tournament));
        Mockito.when(matchDao.getMatch(ID, MATCH_ID)).thenReturn(match);
        Mockito.when(matchDao.allMatchesPlayed(ID)).thenReturn(false);
        Mockito.when(matchDao.getMatchStage(ID, MATCH_ID)).thenReturn(1);
        Mockito.when(matchDao.getStageMatchIds(1, ID, null)).thenReturn(List.of(MATCH_ID, 6L));
        Mockito.when(matchDao.getStageMatchIds(2, ID, null)).thenReturn(List.of(7L));

        matchService.setMatchResults(MATCH_ID, ID, 2, 0);

        Mockito.verify(matchDao).updateMatchLocal(ID, 7L, LOCAL_ID);
    }

    @Test
    public void testSetMatchResultsHybridGroupStageCreatesBracketWhenGroupMatchesFinish() {
        Tournament tournament = tournament(Structure.HYBRID, true);
        Match match = match(true);

        Mockito.when(ts.findById(ID)).thenReturn(Optional.of(tournament));
        Mockito.when(matchDao.getMatch(ID, MATCH_ID)).thenReturn(match);
        Mockito.when(matchDao.allMatchesPlayed(ID, true)).thenReturn(true);
        Mockito.when(matchDao.allMatchesPlayed(ID, false)).thenReturn(false);

        matchService.setMatchResults(MATCH_ID, ID, 3, 1);

        Mockito.verify(participantDao).sumPoints(ID, LOCAL_ID, 3, 2, 1);
        Mockito.verify(participantDao).sumPoints(ID, VISITOR_ID, 0, -2, 1);
        Mockito.verify(ts).createBracketFromGroups(ID);
        Mockito.verify(ts, Mockito.never()).setFinished(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void testSetMatchResultsEliminationFinalFinishesTournament() {
        Tournament tournament = tournament(Structure.ELIMINATION, null);
        Match match = match(null);

        Mockito.when(ts.findById(ID)).thenReturn(Optional.of(tournament));
        Mockito.when(matchDao.getMatch(ID, MATCH_ID)).thenReturn(match);
        Mockito.when(matchDao.allMatchesPlayed(ID)).thenReturn(true);

        matchService.setMatchResults(MATCH_ID, ID, 2, 0);

        Mockito.verify(ts).setFinished(ID, MATCH_ID);
        Mockito.verify(matchDao, Mockito.never()).updateMatchLocal(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(matchDao, Mockito.never()).updateMatchVisitor(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong());
    }

    private Tournament tournament(Structure structure, Boolean groupStage) {
        Tournament tournament = Mockito.mock(Tournament.class);
        Mockito.when(tournament.getFormatEntity()).thenReturn(null);
        Mockito.when(tournament.getStructure()).thenReturn(structure);
        Mockito.when(tournament.getIsGroupStage()).thenReturn(groupStage);
        return tournament;
    }

    private Match match(Boolean groupStage) {
        Match match = Mockito.mock(Match.class);
        Mockito.when(match.getWinner()).thenReturn(null);
        Mockito.when(match.getLocalId()).thenReturn(LOCAL_ID);
        Mockito.when(match.getVisitorId()).thenReturn(VISITOR_ID);
        Mockito.when(match.getIsGroupStage()).thenReturn(groupStage);
        return match;
    }
}
