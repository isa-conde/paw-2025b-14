package ar.edu.itba.paw.webapp.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        Response original = exception.getResponse();
        Response.StatusType status = original == null
                ? Response.Status.INTERNAL_SERVER_ERROR
                : original.getStatusInfo();
        Response.ResponseBuilder builder = Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(ApiErrorFactory.error(status, defaultMessage(status)));

        if (original != null) {
            copyHeaders(original.getHeaders(), builder);
        }

        return builder.build();
    }

    private void copyHeaders(MultivaluedMap<String, Object> headers, Response.ResponseBuilder builder) {
        for (String headerName : headers.keySet()) {
            if ("Content-Type".equalsIgnoreCase(headerName)) {
                continue;
            }
            List<Object> values = headers.get(headerName);
            if (values == null) {
                continue;
            }
            for (Object value : values) {
                builder.header(headerName, value);
            }
        }
    }

    private String defaultMessage(Response.StatusType status) {
        switch (status.getStatusCode()) {
            case 400:
                return "Bad request";
            case 404:
                return "Resource not found";
            case 405:
                return "Method not allowed";
            case 406:
                return "Requested media type is not acceptable";
            case 415:
                return "Unsupported media type";
            default:
                if (status.getFamily() == Response.Status.Family.SERVER_ERROR) {
                    return "Unexpected server error";
                }
                return status.getReasonPhrase();
        }
    }
}
