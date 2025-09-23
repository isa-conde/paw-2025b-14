package ar.edu.itba.paw.interfaces.services;

public interface MailService {

    void sendTournamentCreatedEmail(String userName,
                                    String tournamentName,
                                    String tournamentLink,
                                    String recipient);

    void sendTournamentJoinedEmail(String userName,
                                 String tournamentName,
                                 String tournamentLink,
                                 String recipient,
                                 String creatorMail);

    void sendVerificationEmail(Long userId, String userName, Long token, String recipient);

    void sendResetPasswordEmail(Long userId, Long token, String recipient);

}
