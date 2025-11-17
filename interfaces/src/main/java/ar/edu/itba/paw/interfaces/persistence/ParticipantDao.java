package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Match.PointsPair;
import ar.edu.itba.paw.model.Participant;

import java.util.List;

public interface ParticipantDao {

    void joinTournamentUser(long userId, long tournamentId);

    void joinTournamentUserWithTeam(long userId, long tournamentId, long teamId);

    void joinTournamentTeam(long tournamentId, long teamId);

    Participant getTournamentParticipantById(long tournamentId, Long participantId, int teamSize);

    boolean hasJoined(long userId, long tournamentId);

    boolean hasRated(long userId, long tournamentId);

    void leaveTournamentUser(long userId, long tournamentId);

    void leaveTournamentTeam(long teamId, long tournamentId);

    void updateGroupNumberForUsers(long tournamentId, int groupNumber, List<Long> userIds);

    void swapGroups(long tournamentId, long user1, long user2, int group1, int group2);

    List<Participant> getTournamentParticipantsByPointsPair(long tournamentId, Integer groupNumber, PointsPair pointsPair, int teamSize);

    PointsPair getTournamentMaxPointsPairGroup(long tournamentId, Integer group);

    PointsPair getTournamentSecondMaxPointsPairGroup(long tournamentId, Integer group);

    int getTournamentGroups(long tournamentId);

    List<Participant> getTournamentParticipantUsers(long tournamentId);

    Integer getGroupNumber(long tournamentId, long userId);

    void sumPoints(long tournamentId, long userId, int points, int scoreDifference, int teamSize);

    List<Participant> getTournamentParticipantTeams(long tournamentId);

    void updateHasRated(long userId, long tournamentId);
}
