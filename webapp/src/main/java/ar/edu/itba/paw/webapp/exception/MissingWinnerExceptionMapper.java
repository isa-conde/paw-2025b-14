package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.MissingWinnerException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MissingWinnerExceptionMapper implements ExceptionMapper<MissingWinnerException> {

    @Override
    public Response toResponse(MissingWinnerException exception) {
        return BadRequestMessages.badRequest(exception, "Match winner is required");
    }
}
