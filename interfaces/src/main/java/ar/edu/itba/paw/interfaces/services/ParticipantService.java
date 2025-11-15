package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.User;

import java.util.List;

public interface ParticipantService {

    void joinTournamentUser(Long userId, Long tournamentId);

    Boolean hasJoined(Long userId, Long tournamentId);

    void leaveTournament(Long userId, Long tournamentId);

    void swapGroups(Long tournamentId, Long user1, Long user2);

    List<Participant> getTournamentParticipants(Long tournamentId, Integer teamSize);

    Integer getTournamentGroups(Long tournamentId);

    void joinTournamentTeam(Long tournamentId, Long teamId, List<Long> participants);

    Boolean participantHasRatedTournament(Long tournamentId, Long userId);

    void updateCreatorRating(Long tournamentId, Long creatorId, Long reviewerId, Float rating);

    void removeParticipant(Long tournamentId, Long participantId);
}
