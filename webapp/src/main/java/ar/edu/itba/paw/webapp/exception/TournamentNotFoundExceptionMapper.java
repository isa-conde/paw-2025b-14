package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.TournamentNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TournamentNotFoundExceptionMapper implements ExceptionMapper<TournamentNotFoundException> {

    @Override
    public Response toResponse(TournamentNotFoundException exception) {
        return ApiErrorFactory.notFound("Tournament not found");
    }
}
