package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Participant;

import java.util.List;

public interface ParticipantService {

    void joinTournamentUser(long userId, long tournamentId);

    boolean hasJoined(Long userId, Long tournamentId);

    void leaveTournament(long userId, long tournamentId);

    void swapGroups(long tournamentId, long user1, long user2);

    List<Participant> getTournamentParticipants(long tournamentId, Integer teamSize);

    int getTournamentGroups(long tournamentId);

    void joinTournamentTeam(long tournamentId, long teamId, List<Long> participants);

    boolean participantHasRatedTournament(long userId, long tournamentId);

    void updateCreatorRating(long tournamentId, long creatorId, long reviewerId, float rating);

    void removeParticipant(long tournamentId, Long participantId);
}
