package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.ParticipantAlreadyRatedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ParticipantAlreadyRatedExceptionMapper implements ExceptionMapper<ParticipantAlreadyRatedException> {

    @Override
    public Response toResponse(ParticipantAlreadyRatedException exception) {
        return ConflictMessages.conflict(exception, "Participant already rated this tournament");
    }
}
