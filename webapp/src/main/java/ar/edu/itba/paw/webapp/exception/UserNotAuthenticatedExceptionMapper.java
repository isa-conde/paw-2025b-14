package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.UserNotAuthenticatedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserNotAuthenticatedExceptionMapper implements ExceptionMapper<UserNotAuthenticatedException> {

    @Override
    public Response toResponse(UserNotAuthenticatedException exception) {
        return ApiErrorFactory.unauthorized("Authentication is required to access this resource");
    }
}
