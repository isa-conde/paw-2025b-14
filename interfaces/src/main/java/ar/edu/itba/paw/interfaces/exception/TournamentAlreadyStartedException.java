package ar.edu.itba.paw.interfaces.exception;

public class TournamentAlreadyStartedException extends RuntimeException {
    public TournamentAlreadyStartedException() {
        super("This tournament has already started");
    }
}
