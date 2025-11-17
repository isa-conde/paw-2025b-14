package ar.edu.itba.paw.interfaces.exception;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException() {
        super("errorExceptionPage.message.invalidToken");
    }
}
