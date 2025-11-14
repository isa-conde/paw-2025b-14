package ar.edu.itba.paw.interfaces.exception;

public class UserNotAuthenticatedException extends RuntimeException {
  public UserNotAuthenticatedException(String message) {
    super(message);
  }
}
