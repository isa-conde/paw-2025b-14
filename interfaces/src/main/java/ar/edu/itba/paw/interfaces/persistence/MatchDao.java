package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Match;
import ar.edu.itba.paw.model.MatchInfo;

import java.util.List;

public interface MatchDao {

    void insertMatch(Long id, Long tournamentId, Long localId, Long visitorId, Integer stage, Integer localScore, Integer visitorScore, Integer winner, Boolean isGroupStage);

    Long getMatchWinner(Long tournamentId, Long matchId);

    void setMatchWinner(Long matchId, Long tournamentId, Integer winner);

    List<MatchInfo> getTournamentMatches(Long tournament_id);

    Match getMatch(long tournamentId, long matchId);

    Long getMaxMatchId(Long tournamentId);

    void updateMatchLocal(Long tournament_id, Long match, Long user);

    void updateMatchVisitor(Long tournament_id, Long match, Long user);

    Boolean allMatchesPlayed(Long tournamentId);

    Integer getMatchStage(Long tournamentId, Long matchId);

    List<Long> getStageMatchIds(Integer stage, Long tournamentId);

    Integer getTournamentMaxStage(Long tournamentId);
}
