package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.InvalidCommentException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidCommentExceptionMapper implements ExceptionMapper<InvalidCommentException> {
    @Override
    public Response toResponse(InvalidCommentException exception) {
        return ApiErrorFactory.conflict("Users cannot comment on their own profile");
    }
}
