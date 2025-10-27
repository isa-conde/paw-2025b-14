package ar.edu.itba.paw.interfaces.exception;

public class EmailAlreadyUsedException extends BusinessException{
    public EmailAlreadyUsedException(String email){
        super("The email is already used");
    }
}
