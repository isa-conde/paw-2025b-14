package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Match.Match;

import java.time.LocalDate;
import java.util.List;

public interface MatchDao {

    void insertMatch(long id, long tournamentId, Long localId, Long visitorId, int stage, Integer localScore, Integer visitorScore, Integer winner, Boolean isGroupStage);

    Long getMatchWinner(long tournamentId, long matchId);

    void setMatchResults(long matchId, long tournamentId, int localScore, int visitorScore, int winner, LocalDate date);

    List<Match> getTournamentMatches(long tournamentId, Integer group);

    Match getMatch(long tournamentId, long matchId);

    Long getMaxMatchId(long tournamentId);

    void updateMatchLocal(long tournamentId, long matchId, Long userId);

    void updateMatchVisitor(long tournamentId, long matchId, Long userId);

    boolean allMatchesPlayed(long tournamentId);

    boolean allMatchesPlayed(long tournamentId, Boolean isGroupStage);

    Integer getMatchStage(long tournamentId, long matchId);

    List<Long> getStageMatchIds(int stage, long tournamentId);

    List<Long> getStageMatchIds(int stage, long tournamentId, Boolean isGroupStage);

    int getTournamentMaxStage(long tournamentId);

    int getTournamentGroupMaxStage(long tournamentId, int groupNumber);
}
