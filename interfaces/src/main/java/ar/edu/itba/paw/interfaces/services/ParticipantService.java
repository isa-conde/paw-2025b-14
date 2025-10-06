package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Participant;

import java.util.List;

public interface ParticipantService {

    public void joinTournamentUser(Long user_id, Long tournament_id);

    public Participant getTournamentParticipantByUserId(Long tournament_id, Long user_id);

    Boolean hasJoined(Long userId, Long tournamentId);

    public void leaveTournament(Long user_id, Long tournament_id);

    void swapGroups(Long tournament_id, Long user1, Long user2);

    List<Participant> getTournamentParticipants(Long tournamentId, Integer teamSize);

    Integer getTournamentGroups(Long tournamentId);

    void joinTournamentTeam(Long tournamentId, Long teamId, List<Long> participants);
}
