package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.DrawInEliminationMatchException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DrawInEliminationMatchExceptionMapper implements ExceptionMapper<DrawInEliminationMatchException> {

    @Override
    public Response toResponse(DrawInEliminationMatchException exception) {
        return BadRequestMessages.badRequest(exception, "Elimination matches cannot end in a draw");
    }
}
