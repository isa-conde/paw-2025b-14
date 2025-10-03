package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.ParticipantUserInfo;

import java.util.List;

public interface ParticipantService {

    public void joinTournamentUser(Long user_id, Long tournament_id);

    public List<ParticipantUser> getTournamentParticipantUsers(Long tournament_id);

    public ParticipantUser getTournamentParticipantByUserId(Long tournament_id, Long user_id);

    Boolean hasJoined(Long userId, Long tournamentId);

    public void leaveTournamentUser(Long user_id, Long tournament_id);

    void swapGroups(Long tournament_id, Long user1, Long user2);

    List<ParticipantUserInfo> getTournamentParticipantUsersInfo(Long tournamentId);

    Integer getTournamentGroups(Long tournamentId);
}
