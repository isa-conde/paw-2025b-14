package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.TournamentNotFoundException;
import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
@Service
public class MailServiceImpl implements MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private MessageSource messageSource;

    @Value("${app.baseUrl}")
    private String baseUrl;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TournamentDao tournamentDao;

    private static final String CROWN_CID = "crown";
    private static final String CROWN_CLASSPATH = "images/crown.png";
    private static final String DISCORD_CID = "discord";
    private static final String DISCORD_CLASSPATH = "images/crown.png";

    @Async
    @Override
    public void sendTournamentCreatedEmail(Long tournamentId, String userName, String tournamentName, String recipient) {
        User user = userDao.findByUsername(userName).orElseThrow(UserNotFoundException::new);
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("userName", userName);
        ctx.setVariable("tournamentName", tournamentName);
        String tournamentLink = baseUrl + "/tournament/" + tournamentId.toString();
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("tournament-creation-confirmation", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentCreationConfirmation.subject",
                new Object[]{tournamentName},
                locale
        );
        sendEmail(recipient, subject, body, false);
    }

    @Async
    @Override
    public void sendTournamentJoinedEmail(Long tournamentId, String userName, String tournamentName, String recipient, String creatorMail) {
        User user = userDao.findByUsername(userName).orElseThrow(UserNotFoundException::new);
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("userName", userName);
        ctx.setVariable("tournamentName", tournamentName);
        String tournamentLink = baseUrl + "/tournament/" + tournamentId;
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("creatorMail", creatorMail);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("tournament-joined-confirmation", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentJoinedConfirmation.subject",
                new Object[]{tournamentName},
                locale);

        sendEmail(recipient, subject, body, false);
    }

    @Async
    @Override
    public void sendTournamentJoinedOwnerEmail(Long tournamentId, String ownerUsername, String joinerUsername, String tournamentName, String recipientOwnerEmail) {

        User user = userDao.findByUsername(ownerUsername).orElseThrow(UserNotFoundException::new);
        Locale locale = toLocale(user.getLocale());

        Context ctx = new Context(locale);
        ctx.setVariable("joinerName", joinerUsername);
        ctx.setVariable("tournamentName", tournamentName);
        ctx.setVariable("tournamentLink", baseUrl + "/tournament/" + tournamentId);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("tournament-joined-owner-notification", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentJoinedOwner.subject",
                new Object[]{tournamentName},
                locale
        );

        sendEmail(recipientOwnerEmail, subject, body, false);
    }

    @Async
    @Override
    public void sendTournamentTeamJoinedOwnerEmail(Long tournamentId, String ownerUsername, String teamName, String tournamentName, String recipientOwnerEmail) {

        User user = userDao.findByUsername(ownerUsername).orElseThrow(UserNotFoundException::new);
        Locale locale = toLocale(user.getLocale());

        Context ctx = new Context(locale);
        ctx.setVariable("teamName", teamName);
        ctx.setVariable("tournamentName", tournamentName);
        ctx.setVariable("tournamentLink", baseUrl + "/tournament/" + tournamentId);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("tournament-team-joined-owner-notification", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentTeamJoinedOwner.subject",
                new Object[]{tournamentName},
                locale
        );

        sendEmail(recipientOwnerEmail, subject, body, false);
    }


    @Async
    @Override
    public void sendVerificationEmail(Long userId, String userName, long token, String recipient) {
        User user = userDao.findByUsername(userName).orElseThrow(UserNotFoundException::new);
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("userName", userName);
        String verificationUrl = baseUrl + "/verify/confirm?token=" + token + "&userId=" + userId.toString();
        ctx.setVariable("verificationUrl", verificationUrl);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("verification", ctx);

        String subject = messageSource.getMessage(
                "email.verification.subject",
                null,
                locale);

        sendEmail(recipient, subject, body, false);
    }

    @Async
    @Override
    public void sendResetPasswordEmail(long userId, long token, String recipient) {
        User user = userDao.findById(userId).orElseThrow(UserNotFoundException::new);
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        String resetPasswordUrl = baseUrl + "/forgotPassword/reset?token=" + token;
        ctx.setVariable("resetPasswordUrl", resetPasswordUrl);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("reset-password", ctx);

        String subject = messageSource.getMessage(
                "email.resetPassword.subject",
                null,
                locale);

        sendEmail(recipient, subject, body, false);
    }

    @Async
    @Override
    public void sendTournamentStartedEmail(Tournament tournament, String username, String creatorMail, String recipient) {
        User user = userDao.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("userName", username);
        ctx.setVariable("tournamentName", tournament.getName());
        String tournamentLink = baseUrl + "/tournament/" + tournament.getId();
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("serverName", tournament.getServerName());
        ctx.setVariable("serverPassword", tournament.getServerPassword());
        ctx.setVariable("discordChannel", tournament.getDiscordChannel());
        ctx.setVariable("creatorMail", creatorMail);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("tournament-started-notification", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentStartedNotification.subject",
                new Object[]{tournament.getName()},
                locale);

        sendEmail(recipient, subject, body, false);
    }

    @Async
    @Override
    public void sendTournamentEndedEmail(Long tournamentId, String username, String tournamentName, String recipient) {
        User user = userDao.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("userName", username);
        ctx.setVariable("tournamentName", tournamentName);
        String tournamentLink = baseUrl + "/tournament/" + tournamentId;
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("tournament-ended-notification", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentEndedNotification.subject",
                new Object[]{tournamentName},
                locale);

        sendEmail(recipient, subject, body, false);
    }

    @Async
    @Override
    public void sendTournamentWinnerEmail(Long tournamentId, String username, String tournamentName, String recipient) {
        User user = userDao.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("userName", username);
        ctx.setVariable("tournamentName", tournamentName);
        String tournamentLink = baseUrl + "/tournament/" + tournamentId;
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("tournament-winner-notification", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentWinnerNotification.subject",
                new Object[]{tournamentName},
                locale);

        sendEmail(recipient, subject, body, false);
    }

    @Async
    @Override
    public void sendContactOwnerEmail(Tournament tournament, User user, String emailSubject, String emailBody, User creator) {
        Locale locale = toLocale(creator.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("username", user.getUsername());
        ctx.setVariable("userEmail", user.getEmail());
        ctx.setVariable("creatorUsername", creator.getUsername());
        ctx.setVariable("tournamentName", tournament.getName());
        ctx.setVariable("subject", emailSubject);
        ctx.setVariable("body", emailBody);
        String tournamentLink = baseUrl + "/tournament/" + tournament.getId();
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("contact-owner", ctx);

        String subject = messageSource.getMessage(
                "email.contactOwner.subject",
                new Object[]{tournament.getName()},
                locale);

        sendEmail(creator.getEmail(), subject, body, false);
    }

    @Async
    @Override
    public void sendListEmail(long tournamentId, List<Participant> participantList) {
        Tournament tournament = tournamentDao.findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        User creator = userDao.findById(tournament.getCreatorId()).orElseThrow(UserNotFoundException::new);
        Locale locale = toLocale(creator.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("tournament", tournament);
        ctx.setVariable("participantList", participantList);
        String tournamentLink = baseUrl + "/tournament?tournamentId=" + tournament.getId();
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String mailTemplate = participantList.getFirst().getTeam() == null ? "participant-list" : "participant-list-with-teams";

        String body = templateEngine.process(mailTemplate, ctx);

        String subject = messageSource.getMessage(
                "email.participantList.subject",
                new Object[]{tournament.getName()},
                locale);

        sendEmail(creator.getEmail(), subject, body, false);
    }

    @Async
    @Override
    public void sendRemovedFromTournamentEmail(Long tournamentId, String userName, String tournamentName, String recipient){
        Optional<User> optionalUser = userDao.findByUsername(userName);
        if (optionalUser.isEmpty()){
            LOGGER.warn("User {} not found", userName);
            return;
        }
        User user = optionalUser.get();
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("userName", userName);
        ctx.setVariable("tournamentName", tournamentName);
        String pageLink = baseUrl;
        ctx.setVariable("pageLink", pageLink);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("removed-from-tournament", ctx);

        String subject = messageSource.getMessage(
                "email.removedFromTournament.subject",
                new Object[]{tournamentName},
                locale);

        sendEmail(recipient, subject, body, false);
    }

    @Async
    @Override
    public void sendServerInfoUpdated(Tournament tournament, String userName, String recipient){
        Optional<User> optionalUser = userDao.findByUsername(userName);
        if (optionalUser.isEmpty()){
            LOGGER.warn("User {} not found", userName);
            return;
        }
        User user = optionalUser.get();
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("userName", userName);
        ctx.setVariable("tournamentName", tournament.getName());
        String tournamentLink = baseUrl + "/tournament/" + tournament.getId();
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("server-info-updated", ctx);
        String subject = messageSource.getMessage(
                "email.serverInfoUpdated.subject",
                new Object[]{tournament.getName()},
                locale);

        sendEmail(recipient, subject, body, false);
    }

    @Async
    @Override
    public void sendTournamentAbandonedEmail(User creator, User participant, Tournament tournament) {
        Locale locale = toLocale(creator.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("creator", creator);
        ctx.setVariable("participant", participant);
        ctx.setVariable("tournament", tournament);
        String tournamentLink = baseUrl + "/tournament?tournamentId=" + tournament.getId();
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("tournament-abandoned-owner-notification", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentAbandoned.subject",
                new Object[]{tournament.getName()},
                locale);

        LOGGER.debug("Information for tournament abandoned email has correctly been set");
        sendEmail(creator.getEmail(), subject, body, false);
    }

    @Async
    @Override
    public void sendDiscordLinkUpdated(Tournament tournament, String userName, String recipient){
        Optional<User> optionalUser = userDao.findByUsername(userName);
        if (optionalUser.isEmpty()){
            LOGGER.warn("User {} not found", userName);
            return;
        }
        User user = optionalUser.get();
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("userName", userName);
        ctx.setVariable("tournamentName", tournament.getName());
        String tournamentLink = baseUrl + "/tournament/" + tournament.getId();
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);
        ctx.setVariable("discordCid", "cid:" + DISCORD_CID);

        String body = templateEngine.process("discord-channel-updated", ctx);

        String subject = messageSource.getMessage(
                "email.discordUpdated.subject",
                new Object[]{tournament.getName()},
                locale);

        sendEmail(recipient, subject, body, true);
    }


    private void sendEmail(String recipient, String subject, String body, Boolean hasDiscordLogo) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body, true);
            attachCrownInline(helper);
            if(hasDiscordLogo){
                attachDiscordInline(helper);
            }
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to build email", e);
        }
    }

    private Locale toLocale(String code) {
        Locale loc = Locale.forLanguageTag(code.replace('_', '-'));
        return loc.getLanguage().isEmpty() ? Locale.getDefault() : loc;
    }

    private void attachCrownInline(MimeMessageHelper helper) {
        org.springframework.core.io.ClassPathResource crown =
                new org.springframework.core.io.ClassPathResource(CROWN_CLASSPATH);
        if (!crown.exists()) {
            LOGGER.warn("Crown image not found in classpath at {}", CROWN_CLASSPATH);
            return;
        }
        try {
            helper.addInline(CROWN_CID, crown, "image/png");
        } catch (MessagingException e) {
            LOGGER.warn("Failed to attach crown inline image: {}", e.getMessage());
        }
    }

    private void attachDiscordInline(MimeMessageHelper helper) {
        org.springframework.core.io.ClassPathResource discord =
                new org.springframework.core.io.ClassPathResource(DISCORD_CLASSPATH);
        if (!discord.exists()) {
            LOGGER.warn("Discord image not found in classpath at {}", DISCORD_CLASSPATH);
            return;
        }
        try {
            helper.addInline(DISCORD_CID, discord, "image/png");
        } catch (MessagingException e) {
            LOGGER.warn("Failed to attach discord inline image: {}", e.getMessage());
        }
    }
}
