package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Match.PointsPair;
import ar.edu.itba.paw.model.Participant;

import java.util.List;

public interface ParticipantDao {

    void joinTournamentUser(Long user_id, Long tournament_id);

    void joinTournamentUserWithTeam(Long user_id, Long tournament_id, Long team_id);

    void joinTournamentTeam(Long tournamentId, Long teamId);

    Participant getTournamentParticipantById(Long tournament_id, Long participant_id, Integer teamSize);

    Boolean hasJoined(Long userId, Long tournamentId);

    Boolean hasRated(Long userId, Long tournamentId);

    void leaveTournamentUser(Long user_id, Long tournament_id);

    void leaveTournamentTeam(Long team_id, Long tournament_id);

    void updateGroupNumberForUsers(long tournamentId, int groupNumber, List<Long> userIds, Integer teamSize);

    void swapGroups(Long tournament_id, Long user1, Long user2, Integer group1, Integer group2, Integer teamSize);

    List<Participant> getTournamentParticipantsByPointsPair(Long tournamentId, Integer group_number, PointsPair pointsPair, Integer teamSize);

    PointsPair getTournamentMaxPointsPairGroup(Long tournamentId, Integer group);

    PointsPair getTournamentSecondMaxPointsPairGroup(Long tournamentId, Integer group);

    Integer getTournamentGroups(Long tournamentId);

    List<Participant> getTournamentParticipantUsers(Long tournament_id);

    Integer getGroupNumber(Long tournament_id, Long user_id, Integer teamSize);

    void sumPoints(Long tournamentId, Long userId, Integer points, Integer scoreDifference, Integer teamSize);

    List<Participant> getTournamentParticipantTeams(Long tournament_id);

    void updateHasRated(Long userId, Long tournamentId);
}
