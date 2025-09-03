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
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("tvarettoni@itba.edu.ar", "isconde@itba.edu.ar", "ncanzonieri@itba.edu.ar", "btaccone@itba.edu.ar");
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Override
    public void sendTournamentCreatedEmail(String userName,
                                           String tournamentName,
                                           String tournamentLink,
                                           String recipient) {
        Context ctx = new Context();
        ctx.setVariable("userName", userName);
        ctx.setVariable("tournamentName", tournamentName);
        ctx.setVariable("tournamentLink", tournamentLink);

        String body = templateEngine.process("tournament-creation-confirmation", ctx);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            helper.setTo(recipient);
            helper.setSubject("Tournament Created");
            helper.setText(body, true);

            LOGGER.info("Sending tournament creation email to {}", recipient);
            mailSender.send(mimeMessage);
            LOGGER.info("Tournament creation email successfully sent to {}", recipient);
        } catch (MessagingException e) {
            LOGGER.error("Failed to construct tournament creation email for {}", recipient, e);
            throw new RuntimeException("Failed to build email", e);
        } catch (MailException e) {
            LOGGER.error("Failed to send tournament creation email to {}", recipient, e);
            throw e;
        }
    }

}
