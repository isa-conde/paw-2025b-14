package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.MissingGroupNumberException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MissingGroupNumberExceptionMapper implements ExceptionMapper<MissingGroupNumberException> {

    @Override
    public Response toResponse(MissingGroupNumberException exception) {
        return BadRequestMessages.badRequest(exception, "Group number is required");
    }
}
