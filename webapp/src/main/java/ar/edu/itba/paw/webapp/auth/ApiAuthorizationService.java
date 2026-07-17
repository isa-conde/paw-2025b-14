package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.exception.ParticipantNotFoundException;
import ar.edu.itba.paw.interfaces.exception.TeamNotFoundException;
import ar.edu.itba.paw.interfaces.exception.TournamentNotFoundException;
import ar.edu.itba.paw.interfaces.services.ParticipantService;
import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ApiAuthorizationService {

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private ParticipantService participantService;

    public void assertCurrentUser(long userId) {
        if (!Objects.equals(currentUserProvider.getCurrentUserId(), userId)) {
            throw new AccessDeniedException("Only the resource owner can perform this action");
        }
    }

    public void assertTournamentCreator(long tournamentId) {
        Tournament tournament = tournamentService.findById(tournamentId)
                .orElseThrow(TournamentNotFoundException::new);
        if (!Objects.equals(tournament.getCreatorId(), currentUserProvider.getCurrentUserId())) {
            throw new AccessDeniedException("Only the tournament creator can perform this action");
        }
    }

    public void assertTeamOwner(long teamId) {
        Team team = teamService.findById(teamId).orElseThrow(TeamNotFoundException::new);
        if (team.getOwner() == null || !Objects.equals(team.getOwner().getId(), currentUserProvider.getCurrentUserId())) {
            throw new AccessDeniedException("Only the team owner can perform this action");
        }
    }

    public void assertCanDeleteParticipant(long tournamentId, long participantId) {
        Tournament tournament = tournamentService.findById(tournamentId)
                .orElseThrow(TournamentNotFoundException::new);
        if (Objects.equals(tournament.getCreatorId(), currentUserProvider.getCurrentUserId())) {
            return;
        }

        Participant participant = participantService.getTournamentParticipant(tournamentId, participantId);
        if (ownsParticipant(participant)) {
            return;
        }

        throw new AccessDeniedException("Only the tournament creator or participant owner can perform this action");
    }

    public boolean ownsParticipant(Participant participant) {
        if (participant == null) {
            throw new ParticipantNotFoundException();
        }

        long currentUserId = currentUserProvider.getCurrentUserId();
        if (participant.getUser() != null && Objects.equals(participant.getUser().getId(), currentUserId)) {
            return true;
        }

        return participant.getTeam() != null
                && participant.getTeam().getOwner() != null
                && Objects.equals(participant.getTeam().getOwner().getId(), currentUserId);
    }
}
