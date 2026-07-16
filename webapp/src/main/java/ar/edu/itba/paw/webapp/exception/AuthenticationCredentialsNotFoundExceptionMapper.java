package ar.edu.itba.paw.webapp.exception;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationCredentialsNotFoundExceptionMapper implements ExceptionMapper<AuthenticationCredentialsNotFoundException> {

    @Override
    public Response toResponse(AuthenticationCredentialsNotFoundException exception) {
        return ApiErrorFactory.unauthorized("Authentication is required to access this resource");
    }
}
