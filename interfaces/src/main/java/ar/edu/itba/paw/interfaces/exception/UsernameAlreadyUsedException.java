package ar.edu.itba.paw.interfaces.exception;

public class UsernameAlreadyUsedException extends BusinessException{
    public UsernameAlreadyUsedException(String username){
        super("Username " + username + " is taken");
    }
}
