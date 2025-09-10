package ar.edu.itba.paw.interfaces.exception;

public class InvalidDatesException extends BusinessException{
    public InvalidDatesException(){
        super("Dates are invalid");
    }
}
