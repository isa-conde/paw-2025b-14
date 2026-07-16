package ar.edu.itba.paw.webapp.exception;

import org.springframework.security.core.AuthenticationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {

    @Override
    public Response toResponse(AuthenticationException exception) {
        return ApiErrorFactory.unauthorized("Invalid credentials");
    }
}
