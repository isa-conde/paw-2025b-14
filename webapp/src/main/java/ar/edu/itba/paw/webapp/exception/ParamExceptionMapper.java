package ar.edu.itba.paw.webapp.exception;

import org.glassfish.jersey.server.ParamException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ParamExceptionMapper implements ExceptionMapper<ParamException> {

    @Override
    public Response toResponse(ParamException exception) {
        String parameterName = exception.getParameterName();
        if (parameterName == null || parameterName.isBlank()) {
            return ApiErrorFactory.badRequest("Invalid request parameter");
        }
        return ApiErrorFactory.badRequest("Invalid request parameter: " + parameterName);
    }
}
