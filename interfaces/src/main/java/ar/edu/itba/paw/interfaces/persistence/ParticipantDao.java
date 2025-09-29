package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.ParticipantUser;

import java.util.List;

public interface ParticipantDao {

    void joinTournamentUser(Long user_id, Long tournament_id);

    List<ParticipantUser> getTournamentParticipantUsers(Long tournament_id);

    ParticipantUser getTournamentParticipantByUserId(Long tournament_id, Long user_id);

    Boolean hasJoined(Long userId, Long tournamentId);


}
