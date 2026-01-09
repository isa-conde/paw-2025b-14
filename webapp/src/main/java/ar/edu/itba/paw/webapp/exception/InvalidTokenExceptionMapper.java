package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.InvalidTokenException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class InvalidTokenExceptionMapper implements ExceptionMapper<InvalidTokenException> {

    @Override
    public Response toResponse(InvalidTokenException exception) {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
