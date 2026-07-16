package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.TokenNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TokenNotFoundExceptionMapper implements ExceptionMapper<TokenNotFoundException> {

    @Override
    public Response toResponse(TokenNotFoundException exception) {
        return ApiErrorFactory.notFound("Token not found");
    }
}
