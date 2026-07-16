package ar.edu.itba.paw.webapp.exception;

import org.springframework.security.access.AccessDeniedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {

    @Override
    public Response toResponse(AccessDeniedException exception) {
        return ApiErrorFactory.response(Response.Status.FORBIDDEN, "You do not have permission to access this resource");
    }
}
