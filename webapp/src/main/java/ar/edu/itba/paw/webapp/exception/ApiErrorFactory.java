package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.webapp.dto.entities.ErrorDTO;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

public final class ApiErrorFactory {

    public static final String APPLICATION_JSON = MediaType.APPLICATION_JSON;

    private ApiErrorFactory() {
    }

    public static ErrorDTO error(Response.Status status, String message) {
        return ErrorDTO.of(status, message);
    }

    public static ErrorDTO error(Response.StatusType status, String message) {
        return ErrorDTO.of(status, message);
    }

    public static ErrorDTO error(Response.Status status, String message, Map<String, String> details) {
        return ErrorDTO.of(status, message, details);
    }

    public static Response response(Response.Status status, String message) {
        return response((Response.StatusType) status, message);
    }

    public static Response response(Response.StatusType status, String message) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(ErrorDTO.of(status, message))
                .build();
    }

    public static Response validation(Map<String, String> details) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(ErrorDTO.of(Response.Status.BAD_REQUEST, "Validation failed", details))
                .build();
    }

    public static Response badRequest(String message) {
        return response(Response.Status.BAD_REQUEST, message);
    }

    public static Response notFound(String message) {
        return response(Response.Status.NOT_FOUND, message);
    }

    public static Response conflict(String message) {
        return response(Response.Status.CONFLICT, message);
    }

    public static Response unauthorized(String message) {
        return response(Response.Status.UNAUTHORIZED, message);
    }

    public static String messageOrDefault(Throwable exception, String defaultMessage) {
        String message = exception.getMessage();
        if (message == null || message.isBlank() || message.contains("Exception") || message.contains("errorExceptionPage")) {
            return defaultMessage;
        }
        return message;
    }
}
