package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Match.Match;

import java.util.*;

public interface MatchService {

    void setMatchResults(Long matchId, Long tournamentId, Integer localScore, Integer visitorScore);

    void swapMatchesMembers(Long tournament_id, Long match1, Long match2, Long user1, Long user2);

    Map<Integer, List<Match>> getTournamentMatchesByStage(Long tournamentId);
}
