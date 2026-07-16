package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.UserAlreadyJoinedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

final class ConflictMessages {

    private ConflictMessages() {
    }

    static Response conflict(RuntimeException exception, String defaultMessage) {
        return ApiErrorFactory.conflict(ApiErrorFactory.messageOrDefault(exception, defaultMessage));
    }
}

@Provider
public class ConflictExceptionMapper implements ExceptionMapper<UserAlreadyJoinedException> {

    @Override
    public Response toResponse(UserAlreadyJoinedException exception) {
        return ConflictMessages.conflict(exception, "User already joined tournament");
    }
}
