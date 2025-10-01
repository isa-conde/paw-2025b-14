package ar.edu.itba.paw.interfaces.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super("errorExceptionPage.message.invalidToken");
    }
}
