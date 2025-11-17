package ar.edu.itba.paw.interfaces.exception;

public class NameAlreadyUsedException extends BusinessException {
    public NameAlreadyUsedException(String name) {
        super("Name: " + name + " is taken");
    }
}
