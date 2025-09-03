package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("tvarettoni@itba.edu.ar", "isconde@itba.edu.ar", "ncanzonieri@itba.edu.ar", "btaccone@itba.edu.ar");
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

}
