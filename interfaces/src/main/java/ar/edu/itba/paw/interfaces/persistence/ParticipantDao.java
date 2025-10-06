package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Participant;

import java.util.List;

public interface ParticipantDao {

    void joinTournamentUser(Long user_id, Long tournament_id);

    void joinTournamentUserWithTeam(Long user_id, Long tournament_id, Long team_id);

    void joinTournamentTeam(Long tournamentId, Long teamId);

    Participant getTournamentParticipantByUserId(Long tournament_id, Long user_id);

    Boolean hasJoined(Long userId, Long tournamentId);

    void leaveTournamentUser(Long user_id, Long tournament_id);

    void leaveTournamentTeam(Long user_id, Long tournament_id);

    void updateGroupNumberForUsers(long tournamentId, int groupNumber, List<Long> userIds, Integer teamSize);

    void swapGroups(Long tournament_id, Long user1, Long user2, Integer group1, Integer group2, Integer teamSize);

    List<Participant> getTournamentParticipantsByPoints(Long tournamentId, Integer group_number, Integer points, Integer teamSize);

    Integer getTournamentMaxPoints(Long tournamentId);

    Integer getTournamentSecondMaxPoints(Long tournamentId);

    Integer getTournamentGroups(Long tournamentId);

    List<Participant> getTournamentParticipantUsers(Long tournament_id);

    Integer getGroupNumber(Long tournament_id, Long user_id, Integer teamSize);

    void sumPoints(Long tournamentId, Long userId, Integer points);

    List<Participant> getTournamentParticipantTeams(Long tournament_id);
}
