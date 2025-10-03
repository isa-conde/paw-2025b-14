package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.MatchInfo;
import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.Tournament.Tournament;

import java.util.*;

public interface MatchService {

    void setMatchWinner(Long matchId, Long tournamentId, Integer winner);

    void swapMatchesMembers(Long tournament_id, Long match1, Long match2, Long user1, Long user2);

    Map<Integer, Map<Integer, List<MatchInfo>>> getTournamentMatchesByGroup(Long tournamentId);

    Map<Integer, List<MatchInfo>> getTournamentMatchesByStage(Long tournamentId);
}
