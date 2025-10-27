package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.User;

public interface MailService {

    void sendTournamentCreatedEmail(Long tournamentId, String userName, String tournamentName, String recipient);

    void sendTournamentJoinedEmail(Long tournamentId, String userName, String tournamentName, String recipient, String creatorMail);

    void sendTournamentJoinedOwnerEmail(Long tournamentId, String ownerUsername, String joinerUsername, String tournamentName, String recipientOwnerEmail);

    void sendTournamentTeamJoinedOwnerEmail(Long tournamentId, String ownerUsername, String teamName, String tournamentName, String recipientOwnerEmail);

    void sendTournamentStartedEmail(Long tournamentId, String username, String tournamentName, String creatorMail, String recipient);

    void sendTournamentEndedEmail(Long tournamentId, String username, String tournamentName, String recipient);

    void sendTournamentWinnerEmail(Long tournamentId, String username, String tournamentName, String recipient);

    void sendVerificationEmail(Long userId, String userName, Long token, String recipient);

    void sendResetPasswordEmail(Long userId, Long token, String recipient);

    void sendContactOwnerEmail(Tournament tournament, User user, String emailSubject, String emailBody, User creator);

}
