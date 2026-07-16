package ar.edu.itba.paw.webapp.exception;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, String> details = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String field = fieldName(violation.getPropertyPath().toString());
            details.putIfAbsent(field, violation.getMessage());
        }
        return ApiErrorFactory.validation(details);
    }

    private String fieldName(String propertyPath) {
        if (propertyPath == null || propertyPath.isBlank()) {
            return "request";
        }
        int index = propertyPath.lastIndexOf('.');
        if (index >= 0 && index + 1 < propertyPath.length()) {
            return propertyPath.substring(index + 1);
        }
        return propertyPath;
    }
}
