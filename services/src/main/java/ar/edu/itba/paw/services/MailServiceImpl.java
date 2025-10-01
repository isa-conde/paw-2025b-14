package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Component
@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

    @Override
    public void sendTournamentCreatedEmail(String userName, String tournamentName, String tournamentLink, String recipient) {
        Context ctx = new Context();
        ctx.setVariable("userName", userName);
        ctx.setVariable("tournamentName", tournamentName);
        ctx.setVariable("tournamentLink", tournamentLink);

        String body = templateEngine.process("tournament-creation-confirmation", ctx);

        sendEmail(recipient, "You just created " + tournamentName + "!", body);
    }

    @Override
    public void sendTournamentJoinedEmail(String userName, String tournamentName, String tournamentLink, String creatorMail, String recipient) {
        Context ctx = new Context();
        ctx.setVariable("userName", userName);
        ctx.setVariable("tournamentName", tournamentName);
        ctx.setVariable("tournamentLink", tournamentLink);
        ctx.setVariable("creatorMail", creatorMail);

        String body = templateEngine.process("tournament-joined-confirmation", ctx);

        sendEmail(recipient, "You just joined " + tournamentName + "!", body);
    }

    @Override
    public void sendVerificationEmail(Long userId, String userName, Long token, String recipient, String baseUrl) {
        Context ctx = new Context();
        ctx.setVariable("userName", userName);
        String verificationUrl = baseUrl + "/verify/confirm?token=" + token.toString() + "&userId=" + userId.toString();
        ctx.setVariable("verificationUrl", verificationUrl);

        String body = templateEngine.process("verification-email", ctx);

        sendEmail(recipient, "Email Verification", body);
    }

    @Override
    public void sendResetPasswordEmail(Long userId, Long token, String recipient, String baseUrl) {
        Context ctx = new Context();
        String resetPasswordUrl = baseUrl + "/forgotPassword/reset?token=" + token.toString() + "&userId=" + userId;
        ctx.setVariable("resetPasswordUrl", resetPasswordUrl);

        String body = templateEngine.process("reset-password", ctx);

        sendEmail(recipient, "Reset your Password", body);
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
