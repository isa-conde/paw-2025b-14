package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.TournamentNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class TournamentNotFoundExceptionMapper implements ExceptionMapper<TournamentNotFoundException> {

    @Override
    public Response toResponse(TournamentNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
