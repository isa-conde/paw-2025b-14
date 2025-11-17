package ar.edu.itba.paw.interfaces.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException() {
        super("Game not found");
    }
}
