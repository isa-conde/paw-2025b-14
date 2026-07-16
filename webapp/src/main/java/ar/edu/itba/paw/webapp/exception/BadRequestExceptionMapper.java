package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.ScoresInvalidException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

final class BadRequestMessages {

    private BadRequestMessages() {
    }

    static Response badRequest(RuntimeException exception, String defaultMessage) {
        return ApiErrorFactory.badRequest(ApiErrorFactory.messageOrDefault(exception, defaultMessage));
    }
}

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<ScoresInvalidException> {

    @Override
    public Response toResponse(ScoresInvalidException exception) {
        return BadRequestMessages.badRequest(exception, "Scores are invalid");
    }
}
