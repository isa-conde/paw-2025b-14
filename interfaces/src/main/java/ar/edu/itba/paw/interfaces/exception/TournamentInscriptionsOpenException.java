package ar.edu.itba.paw.interfaces.exception;

public class TournamentInscriptionsOpenException extends BusinessException {
    public TournamentInscriptionsOpenException() {
        super("Tournament inscriptions must be closed before starting");
    }
}
