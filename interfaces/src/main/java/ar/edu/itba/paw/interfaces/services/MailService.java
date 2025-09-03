package ar.edu.itba.paw.interfaces.services;

public interface MailService {

    void sendSimpleMessage(String to, String subject, String text);

    void sendTournamentCreatedEmail(String userName,
                                    String tournamentName,
                                    String tournamentLink,
                                    String recipient);

    void sendTournamentJoinedEmail(String userName,
                                 String tournamentName,
                                 String tournamentLink,
                                 String recipient,
                                 String creatorMail);

}
