package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Match.Match;

import java.util.*;

public interface MatchService {

    void setMatchResults(long matchId, long tournamentId, Integer localScore, Integer visitorScore);

    void swapMatchesMembers(long tournamentId, long match1, long match2, long user1, long user2);

    Map<Integer, List<Match>> getTournamentMatchesByStage(long tournamentId, Integer group);
}
