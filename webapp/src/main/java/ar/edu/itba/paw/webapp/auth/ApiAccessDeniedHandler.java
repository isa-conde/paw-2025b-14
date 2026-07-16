package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.webapp.exception.ApiErrorFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(ApiErrorFactory.APPLICATION_JSON);
        objectMapper.writeValue(
                response.getOutputStream(),
                ApiErrorFactory.error(Response.Status.FORBIDDEN, "You do not have permission to access this resource")
        );
    }
}
