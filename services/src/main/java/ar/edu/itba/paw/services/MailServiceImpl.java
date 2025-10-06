package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.MailService;
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

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private MessageSource messageSource;

    @Value("${app.baseUrl}")
    private String baseUrl;

    @Async
    @Override
    public void sendTournamentCreatedEmail(Long tournamentId, String userName, String tournamentName, String recipient) {
        Locale locale = Locale.getDefault();
        Context ctx = new Context(locale);
        ctx.setVariable("userName", userName);
        ctx.setVariable("tournamentName", tournamentName);
        String tournamentLink = baseUrl + "/tournament?tournamentId=" + tournamentId.toString();
        ctx.setVariable("tournamentLink", tournamentLink);

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
        Locale locale = Locale.getDefault();
        Context ctx = new Context(locale);
        ctx.setVariable("userName", userName);
        ctx.setVariable("tournamentName", tournamentName);
        String tournamentLink = baseUrl + "/tournament?tournamentId=" + tournamentId;
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("creatorMail", creatorMail);

        String body = templateEngine.process("tournament-joined-confirmation", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentJoinedConfirmation.subject",
                new Object[]{tournamentName},
                locale);

        sendEmail(recipient, subject, body);
    }

    @Async
    @Override
    public void sendVerificationEmail(Long userId, String userName, Long token, String recipient) {
        Locale locale = Locale.getDefault();
        Context ctx = new Context(locale);
        ctx.setVariable("userName", userName);
        String verificationUrl = baseUrl + "/verify/confirm?token=" + token.toString() + "&userId=" + userId.toString();
        ctx.setVariable("verificationUrl", verificationUrl);

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
        Locale locale = Locale.getDefault();
        Context ctx = new Context(locale);
        String resetPasswordUrl = baseUrl + "/forgotPassword/reset?token=" + token.toString() + "&userId=" + userId;
        ctx.setVariable("resetPasswordUrl", resetPasswordUrl);

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
        Locale locale = Locale.getDefault();
        Context ctx = new Context(locale);
        ctx.setVariable("userName", username);
        ctx.setVariable("tournamentName", tournamentName);
        String tournamentLink = baseUrl + "/tournament?tournamentId=" + tournamentId;
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("creatorMail", creatorMail);

        String body = templateEngine.process("tournament-started-notification", ctx);

        String subject = messageSource.getMessage(
                "email.tournamentStartedNotification.subject",
                new Object[]{tournamentName},
                locale);

        sendEmail(recipient, subject, body);
    }


    private void sendEmail(String recipient, String subject, String body) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to build email", e);
        } catch (MailException e) {
            throw e;
        }
    }

}
