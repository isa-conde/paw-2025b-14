package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Match.PointsPair;
import ar.edu.itba.paw.model.Participant;

import java.util.List;

public interface ParticipantDao {

    void joinTournamentUser(Long userId, Long tournamentId);

    void joinTournamentUserWithTeam(Long userId, Long tournamentId, Long teamId);

    void joinTournamentTeam(Long tournamentId, Long teamId);

    Participant getTournamentParticipantById(Long tournamentId, Long participantId, Integer teamSize);

    Boolean hasJoined(Long userId, Long tournamentId);

    Boolean hasRated(Long userId, Long tournamentId);

    void leaveTournamentUser(Long userId, Long tournamentId);

    void leaveTournamentTeam(Long teamId, Long tournamentId);

    void updateGroupNumberForUsers(long tournamentId, int groupNumber, List<Long> userIds, Integer teamSize);

    void swapGroups(Long tournamentId, Long user1, Long user2, Integer group1, Integer group2, Integer teamSize);

    List<Participant> getTournamentParticipantsByPointsPair(Long tournamentId, Integer groupNumber, PointsPair pointsPair, Integer teamSize);

    PointsPair getTournamentMaxPointsPairGroup(Long tournamentId, Integer group);

    PointsPair getTournamentSecondMaxPointsPairGroup(Long tournamentId, Integer group);

    Integer getTournamentGroups(Long tournamentId);

    List<Participant> getTournamentParticipantUsers(Long tournamentId);

    Integer getGroupNumber(Long tournamentId, Long userId, Integer teamSize);

    void sumPoints(Long tournamentId, Long userId, Integer points, Integer scoreDifference, Integer teamSize);

    List<Participant> getTournamentParticipantTeams(Long tournamentId);

    void updateHasRated(Long userId, Long tournamentId);
}
