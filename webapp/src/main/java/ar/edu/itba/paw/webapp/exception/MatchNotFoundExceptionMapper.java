package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.MatchNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MatchNotFoundExceptionMapper implements ExceptionMapper<MatchNotFoundException> {

    @Override
    public Response toResponse(MatchNotFoundException exception) {
        return ApiErrorFactory.notFound("Match not found");
    }
}
