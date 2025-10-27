package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Match;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MatchDao {

    void insertMatch(Long id, Long tournamentId, Long localId, Long visitorId, Integer stage, Integer localScore, Integer visitorScore, Integer winner, Boolean isGroupStage);

    Long getMatchWinner(Long tournamentId, Long matchId);

    void setMatchResults(Long matchId, Long tournamentId, Integer localScore, Integer visitorScore, Integer winner, LocalDate date);

    List<Match> getTournamentMatches(Long tournament_id, Integer teamSize);

    Match getMatch(long tournamentId, long matchId);

    Long getMaxMatchId(Long tournamentId);

    void updateMatchLocal(Long tournamentId, Long matchId, Long userId);

    void updateMatchVisitor(Long tournamentId, Long matchId, Long userId);

    Boolean allMatchesPlayed(Long tournamentId);

    Integer getMatchStage(Long tournamentId, Long matchId);

    List<Long> getStageMatchIds(Integer stage, Long tournamentId);

    Integer getTournamentMaxStage(Long tournamentId);

    Integer getTournamentGroupMaxStage(Long tournamentId, Integer groupNumber);
}
