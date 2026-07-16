package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.GameFormatNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GameFormatNotFoundExceptionMapper implements ExceptionMapper<GameFormatNotFoundException> {

    @Override
    public Response toResponse(GameFormatNotFoundException exception) {
        return ApiErrorFactory.notFound("Game format not found");
    }
}
