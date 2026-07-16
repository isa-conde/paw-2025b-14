package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.MatchWinnerAlreadySetException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MatchWinnerAlreadySetExceptionMapper implements ExceptionMapper<MatchWinnerAlreadySetException> {

    @Override
    public Response toResponse(MatchWinnerAlreadySetException exception) {
        return ConflictMessages.conflict(exception, "Match winner has already been set");
    }
}
