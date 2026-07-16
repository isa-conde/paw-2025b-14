package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.TournamentAlreadyStartedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TournamentAlreadyStartedExceptionMapper implements ExceptionMapper<TournamentAlreadyStartedException> {

    @Override
    public Response toResponse(TournamentAlreadyStartedException exception) {
        return ConflictMessages.conflict(exception, "Tournament already started");
    }
}
