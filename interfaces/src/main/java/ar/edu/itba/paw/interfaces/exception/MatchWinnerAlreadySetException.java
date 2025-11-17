package ar.edu.itba.paw.interfaces.exception;

public class MatchWinnerAlreadySetException extends RuntimeException {
    public MatchWinnerAlreadySetException() {
        super("Match winner has already been set");
    }
}
