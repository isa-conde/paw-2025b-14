package ar.edu.itba.paw.webapp.dto.entities;

import javax.ws.rs.core.Response;
import java.util.Map;

public class ErrorDTO {

    private int status;
    private String error;
    private String message;
    private Map<String, String> details;

    public ErrorDTO() {}

    public ErrorDTO(int status, String error, String message) {
        this(status, error, message, null);
    }

    public ErrorDTO(int status, String error, String message, Map<String, String> details) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
    }

    public static ErrorDTO of(Response.Status status, String message) {
        return of(status, message, null);
    }

    public static ErrorDTO of(Response.Status status, String message, Map<String, String> details) {
        return of((Response.StatusType) status, message, details);
    }

    public static ErrorDTO of(Response.StatusType status, String message) {
        return of(status, message, null);
    }

    public static ErrorDTO of(Response.StatusType status, String message, Map<String, String> details) {
        return new ErrorDTO(
                status.getStatusCode(),
                status.getReasonPhrase(),
                message,
                details
        );
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getDetails() {
        return details;
    }
}
