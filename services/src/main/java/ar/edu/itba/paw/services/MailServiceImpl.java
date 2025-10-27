package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

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

    private static final String CROWN_CID = "crown";
    private static final String CROWN_CLASSPATH = "images/crown.png";

    @Async
    @Override
    public void sendTournamentCreatedEmail(Long tournamentId, String userName, String tournamentName, String recipient) {
        User user = userDao.findByUsername(userName).get();
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("userName", userName);
        ctx.setVariable("tournamentName", tournamentName);
        String tournamentLink = baseUrl + "/tournament?tournamentId=" + tournamentId.toString();
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("tournament-creation-confirmation", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentCreationConfirmation.subject",
                new Object[]{tournamentName},
                locale
        );
        sendEmail(recipient, subject, body);
    }

    @Async
    @Override
    public void sendTournamentJoinedEmail(Long tournamentId, String userName, String tournamentName, String recipient, String creatorMail) {
        User user = userDao.findByUsername(userName).get();
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("userName", userName);
        ctx.setVariable("tournamentName", tournamentName);
        String tournamentLink = baseUrl + "/tournament?tournamentId=" + tournamentId;
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("creatorMail", creatorMail);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("tournament-joined-confirmation", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentJoinedConfirmation.subject",
                new Object[]{tournamentName},
                locale);

        sendEmail(recipient, subject, body);
    }

    @Async
    @Override
    public void sendTournamentJoinedOwnerEmail(Long tournamentId, String ownerUsername, String joinerUsername, String tournamentName, String recipientOwnerEmail) {

        User user = userDao.findByUsername(ownerUsername).get();
        Locale locale = toLocale(user.getLocale());

        Context ctx = new Context(locale);
        ctx.setVariable("joinerName", joinerUsername);
        ctx.setVariable("tournamentName", tournamentName);
        ctx.setVariable("tournamentLink", baseUrl + "/tournament?tournamentId=" + tournamentId);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("tournament-joined-owner-notification", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentJoinedOwner.subject",
                new Object[]{tournamentName},
                locale
        );

        sendEmail(recipientOwnerEmail, subject, body);
    }

    @Async
    @Override
    public void sendTournamentTeamJoinedOwnerEmail(Long tournamentId, String ownerUsername, String teamName, String tournamentName, String recipientOwnerEmail) {

        User user = userDao.findByUsername(ownerUsername).get();
        Locale locale = toLocale(user.getLocale());

        Context ctx = new Context(locale);
        ctx.setVariable("teamName", teamName);
        ctx.setVariable("tournamentName", tournamentName);
        ctx.setVariable("tournamentLink", baseUrl + "/tournament?tournamentId=" + tournamentId);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("tournament-team-joined-owner-notification", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentTeamJoinedOwner.subject",
                new Object[]{tournamentName},
                locale
        );

        sendEmail(recipientOwnerEmail, subject, body);
    }


    @Async
    @Override
    public void sendVerificationEmail(Long userId, String userName, Long token, String recipient) {
        User user = userDao.findByUsername(userName).get();
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("userName", userName);
        String verificationUrl = baseUrl + "/verify/confirm?token=" + token.toString() + "&userId=" + userId.toString();
        ctx.setVariable("verificationUrl", verificationUrl);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("verification", ctx);

        String subject = messageSource.getMessage(
                "email.verification.subject",
                null,
                locale);

        sendEmail(recipient, subject, body);
    }

    @Async
    @Override
    public void sendResetPasswordEmail(Long userId, Long token, String recipient) {
        User user = userDao.findById(userId).get();
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        String resetPasswordUrl = baseUrl + "/forgotPassword/reset?token=" + token.toString() + "&userId=" + userId;
        ctx.setVariable("resetPasswordUrl", resetPasswordUrl);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("reset-password", ctx);

        String subject = messageSource.getMessage(
                "email.resetPassword.subject",
                null,
                locale);

        sendEmail(recipient, subject, body);
    }

    @Async
    @Override
    public void sendTournamentStartedEmail(Long tournamentId, String username, String tournamentName, String creatorMail, String recipient) {
        User user = userDao.findByUsername(username).get();
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("userName", username);
        ctx.setVariable("tournamentName", tournamentName);
        String tournamentLink = baseUrl + "/tournament?tournamentId=" + tournamentId;
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("creatorMail", creatorMail);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("tournament-started-notification", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentStartedNotification.subject",
                new Object[]{tournamentName},
                locale);

        sendEmail(recipient, subject, body);
    }

    @Async
    @Override
    public void sendTournamentEndedEmail(Long tournamentId, String username, String tournamentName, String recipient) {
        User user = userDao.findByUsername(username).get();
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("userName", username);
        ctx.setVariable("tournamentName", tournamentName);
        String tournamentLink = baseUrl + "/tournament?tournamentId=" + tournamentId;
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("tournament-ended-notification", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentEndedNotification.subject",
                new Object[]{tournamentName},
                locale);

        sendEmail(recipient, subject, body);
    }

    @Async
    @Override
    public void sendTournamentWinnerEmail(Long tournamentId, String username, String tournamentName, String recipient) {
        User user = userDao.findByUsername(username).get();
        Locale locale = toLocale(user.getLocale());
        Context ctx = new Context(locale);
        ctx.setVariable("userName", username);
        ctx.setVariable("tournamentName", tournamentName);
        String tournamentLink = baseUrl + "/tournament?tournamentId=" + tournamentId;
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("tournament-winner-notification", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentWinnerNotification.subject",
                new Object[]{tournamentName},
                locale);

        sendEmail(recipient, subject, body);
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
        String tournamentLink = baseUrl + "/tournament?tournamentId=" + tournament.getId();
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("crownCid", "cid:" + CROWN_CID);

        String body = templateEngine.process("contact-owner", ctx);

        String subject = messageSource.getMessage(
                "email.contactOwner.subject",
                new Object[]{tournament.getName()},
                locale);

        sendEmail(creator.getEmail(), subject, body);
    }

    private void sendEmail(String recipient, String subject, String body) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body, true);
            attachCommonInlines(helper);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to build email", e);
        } catch (MailException e) {
            throw e;
        }
    }

    private Locale toLocale(String code) {
        Locale loc = Locale.forLanguageTag(code.replace('_', '-'));
        return loc.getLanguage().isEmpty() ? Locale.getDefault() : loc;
    }

    private void attachCommonInlines(MimeMessageHelper helper) {
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

}
