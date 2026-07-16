package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserNotFoundExceptionMapper implements ExceptionMapper<UserNotFoundException> {

    @Override
    public Response toResponse(UserNotFoundException exception) {
        return ApiErrorFactory.notFound("User not found");
    }

}
