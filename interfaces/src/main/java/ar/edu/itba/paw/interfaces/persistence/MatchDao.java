package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Match;
import ar.edu.itba.paw.model.MatchWithPlayers;

import java.util.List;

public interface MatchDao {

    void insertMatch(Long id, Long tournamentId, Long localId, Long visitorId, Integer stage, Integer localScore, Integer visitorScore, Integer winner);

    void swapMatchesMembers(Long tournament_id, Long match1, Long match2, Long user1, Long user2);

    Long getMatchWinner(Long tournamentId, Long matchId);

    void setMatchWinner(Long matchId, Long tournamentId, Integer winner);

    List<MatchWithPlayers> getTournamentMatches(Long tournament_id);

    Match getMatch(long tournamentId, long matchId);
}
