package ar.edu.itba.paw.interfaces.services;

public interface MailService {

    void sendTournamentCreatedEmail(Long tournamentId, String userName, String tournamentName, String recipient);

    void sendTournamentJoinedEmail(Long tournamentId, String userName, String tournamentName, String recipient, String creatorMail);

    void sendTournamentStartedEmail(Long tournamentId, String username, String tournamentName, String creatorMail, String recipient);

    void sendVerificationEmail(Long userId, String userName, Long token, String recipient);

    void sendResetPasswordEmail(Long userId, Long token, String recipient);

}
