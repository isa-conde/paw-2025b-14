package ar.edu.itba.paw.interfaces.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("errorExceptionPage.message.userNotFound");
    }
}
