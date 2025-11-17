package ar.edu.itba.paw.interfaces.exception;

public class TournamentNotFoundException extends RuntimeException {
    public TournamentNotFoundException() {
        super("Tournament not found");
    }
}
