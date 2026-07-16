package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.BusinessException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(BusinessException exception) {
        return ApiErrorFactory.conflict(ApiErrorFactory.messageOrDefault(exception, "Resource conflict"));
    }
}
