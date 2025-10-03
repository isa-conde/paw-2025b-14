package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.ParticipantUserInfo;

import java.util.List;
import java.util.Map;

public interface ParticipantDao {

    void joinTournamentUser(Long user_id, Long tournament_id);

    List<ParticipantUser> getTournamentParticipantUsers(Long tournament_id);

    ParticipantUser getTournamentParticipantByUserId(Long tournament_id, Long user_id);

    Boolean hasJoined(Long userId, Long tournamentId);

    void leaveTournamentUser(Long user_id, Long tournament_id);

    void updateGroupNumberForUsers(long tournamentId, int groupNumber, List<Long> userIds);

    void swapGroups(Long tournament_id, Long user1, Long user2, Integer group1, Integer group2);

    List<ParticipantUser> getTournamentParticipantsByPoints(Long tournamentId, Integer group_number, Integer points);

    Integer getTournamentMaxPoints(Long tournamentId);

    Integer getTournamentSecondMaxPoints(Long tournamentId);

    Integer getTournamentGroups(Long tournamentId);

    List<ParticipantUserInfo> getTournamentsParticipantUsersInfo(Long tournament_id);

    Integer getGroupNumber(Long tournament_id, Long user_id);

    void sumPoints(Long tournamentId, Long userId, Integer points);
}
