package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Participant;

import java.util.List;

public interface ParticipantService {

    void joinTournamentUser(long userId, long tournamentId);

    boolean hasJoined(Long userId, Long tournamentId);

    void leaveTournament(Long userId, Long tournamentId);

    void swapGroups(Long tournamentId, Long user1, Long user2);

    List<Participant> getTournamentParticipants(Long tournamentId, Integer teamSize);

    int getTournamentGroups(Long tournamentId);

    void joinTournamentTeam(long tournamentId, long teamId, List<Long> participants);

    boolean participantHasRatedTournament(Long tournamentId, Long userId);

    void updateCreatorRating(Long tournamentId, Long creatorId, Long reviewerId, Float rating);

    void removeParticipant(long tournamentId, Long participantId);
}
