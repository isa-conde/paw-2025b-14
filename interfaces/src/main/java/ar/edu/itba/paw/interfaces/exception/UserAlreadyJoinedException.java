package ar.edu.itba.paw.interfaces.exception;

public class UserAlreadyJoinedException extends RuntimeException {
    public UserAlreadyJoinedException() {
        super("User already participates in tournament");
    }
}
