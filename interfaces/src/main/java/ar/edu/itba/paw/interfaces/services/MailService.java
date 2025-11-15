package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface MailService {

    void sendTournamentCreatedEmail(Long tournamentId, String userName, String tournamentName, String recipient);

    void sendTournamentJoinedEmail(Long tournamentId, String userName, String tournamentName, String recipient, String creatorMail);

    void sendTournamentJoinedOwnerEmail(Long tournamentId, String ownerUsername, String joinerUsername, String tournamentName, String recipientOwnerEmail);

    void sendTournamentTeamJoinedOwnerEmail(Long tournamentId, String ownerUsername, String teamName, String tournamentName, String recipientOwnerEmail);

    void sendTournamentStartedEmail(Tournament tournament, String username, String creatorMail, String recipient);

    void sendTournamentEndedEmail(Long tournamentId, String username, String tournamentName, String recipient);

    void sendTournamentWinnerEmail(Long tournamentId, String username, String tournamentName, String recipient);

    void sendVerificationEmail(Long userId, String userName, Long token, String recipient);

    void sendResetPasswordEmail(Long userId, Long token, String recipient);

    void sendContactOwnerEmail(Tournament tournament, User user, String emailSubject, String emailBody, User creator);

    void sendRemovedFromTournamentEmail(Long tournamentId, String userName, String tournamentName, String recipient);

    void sendServerInfoUpdated(Tournament tournament, String userName, String recipient);

    void sendDiscordLinkUpdated(Tournament tournament, String userName, String recipient);

    void sendListEmail(Long tournamentId, List<Participant> participantList);

    void sendTournamentAbandonedEmail(User creator, User participant, Tournament tournament);
}
