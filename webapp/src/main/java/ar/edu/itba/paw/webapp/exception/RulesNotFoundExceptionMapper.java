package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.RulesNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RulesNotFoundExceptionMapper implements ExceptionMapper<RulesNotFoundException> {

    @Override
    public Response toResponse(RulesNotFoundException exception) {
        return ApiErrorFactory.notFound("Rules not found");
    }
}
