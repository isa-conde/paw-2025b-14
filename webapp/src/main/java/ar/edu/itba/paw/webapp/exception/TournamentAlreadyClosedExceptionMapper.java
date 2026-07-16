package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.TournamentAlreadyClosedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TournamentAlreadyClosedExceptionMapper implements ExceptionMapper<TournamentAlreadyClosedException> {

    @Override
    public Response toResponse(TournamentAlreadyClosedException exception) {
        return ConflictMessages.conflict(exception, "Tournament already closed");
    }
}
