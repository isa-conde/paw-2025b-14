package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.TeamNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TeamNotFoundExceptionMapper implements ExceptionMapper<TeamNotFoundException> {

    @Override
    public Response toResponse(TeamNotFoundException exception) {
        return ApiErrorFactory.notFound("Team not found");
    }
}
