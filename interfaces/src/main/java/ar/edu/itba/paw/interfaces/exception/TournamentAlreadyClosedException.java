package ar.edu.itba.paw.interfaces.exception;

public class TournamentAlreadyClosedException extends RuntimeException {
    public TournamentAlreadyClosedException() {
        super("This tournament's inscriptions have already closed");
    }
}
