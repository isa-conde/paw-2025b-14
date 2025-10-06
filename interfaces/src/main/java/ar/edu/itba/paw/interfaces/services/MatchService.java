package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.MatchInfo;

import java.util.*;

public interface MatchService {

    void setMatchWinner(Long matchId, Long tournamentId, Integer winner);

    void swapMatchesMembers(Long tournament_id, Long match1, Long match2, Long user1, Long user2);

    Map<Integer, List<MatchInfo>> getTournamentMatchesByStage(Long tournamentId);
}
