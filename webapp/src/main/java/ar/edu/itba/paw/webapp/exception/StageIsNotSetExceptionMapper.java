package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.StageIsNotSetException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class StageIsNotSetExceptionMapper implements ExceptionMapper<StageIsNotSetException> {

    @Override
    public Response toResponse(StageIsNotSetException exception) {
        return ConflictMessages.conflict(exception, "Match stage is not set");
    }
}
