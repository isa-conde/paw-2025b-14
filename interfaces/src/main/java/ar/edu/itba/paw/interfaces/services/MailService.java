package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;

import java.util.List;

public interface MailService {

    void sendTournamentCreatedEmail(long tournamentId, String userName, String tournamentName, String recipient);

    void sendTournamentJoinedEmail(long tournamentId, String userName, String tournamentName, String recipient, String creatorMail);

    void sendTournamentJoinedOwnerEmail(long tournamentId, String ownerUsername, String joinerUsername, String tournamentName, String recipientOwnerEmail);

    void sendTournamentTeamJoinedOwnerEmail(long tournamentId, String ownerUsername, String teamName, String tournamentName, String recipientOwnerEmail);

    void sendTournamentStartedEmail(Tournament tournament, String username, String creatorMail, String recipient);

    void sendTournamentEndedEmail(long tournamentId, String username, String tournamentName, String recipient);

    void sendTournamentWinnerEmail(long tournamentId, String username, String tournamentName, String recipient);

    void sendVerificationEmail(long userId, String userName, long token, String recipient);

    void sendResetPasswordEmail(long userId, long token, String recipient);

    void sendContactOwnerEmail(Tournament tournament, User user, String emailSubject, String emailBody, User creator);

    void sendRemovedFromTournamentEmail(long tournamentId, String userName, String tournamentName, String recipient);

    void sendLeftTournamentEmail(long tournamentId, String userName, String tournamentName, String recipient);

    void sendServerInfoUpdated(Tournament tournament, String userName, String recipient);

    void sendDiscordLinkUpdated(Tournament tournament, String userName, String recipient);

    void sendListEmail(long tournamentId, List<Participant> participantList);

    void sendTournamentAbandonedEmail(User creator, User participant, Tournament tournament);
}
