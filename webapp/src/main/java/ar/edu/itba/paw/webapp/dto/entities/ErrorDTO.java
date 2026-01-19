package ar.edu.itba.paw.webapp.dto.entities;

import javax.ws.rs.core.Response;

public class ErrorDTO {

    private int status;
    private String error;
    private String message;

    public ErrorDTO() {}

    public ErrorDTO(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public static ErrorDTO of(Response.Status status, String message) {
        return new ErrorDTO(
                status.getStatusCode(),
                status.getReasonPhrase(),
                message
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
}
