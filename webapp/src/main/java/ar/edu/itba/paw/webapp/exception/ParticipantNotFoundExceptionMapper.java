package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.ParticipantNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ParticipantNotFoundExceptionMapper implements ExceptionMapper<ParticipantNotFoundException> {

    @Override
    public Response toResponse(ParticipantNotFoundException exception) {
        return ApiErrorFactory.notFound("Participant not found");
    }
}
