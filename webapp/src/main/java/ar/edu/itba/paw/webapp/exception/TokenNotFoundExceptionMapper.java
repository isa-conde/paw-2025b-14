package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.TokenNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class TokenNotFoundExceptionMapper implements ExceptionMapper<TokenNotFoundException> {

    @Override
    public Response toResponse(TokenNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
