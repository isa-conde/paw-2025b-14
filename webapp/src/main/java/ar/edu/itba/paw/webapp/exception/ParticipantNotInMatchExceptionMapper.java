package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.ParticipantNotInMatchException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ParticipantNotInMatchExceptionMapper implements ExceptionMapper<ParticipantNotInMatchException> {

    @Override
    public Response toResponse(ParticipantNotInMatchException exception) {
        return BadRequestMessages.badRequest(exception, "Participant is not part of the match");
    }
}
