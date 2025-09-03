package ar.edu.itba.paw.interfaces.services;

public interface MailService {

    void sendSimpleMessage(String to, String subject, String text);

}
