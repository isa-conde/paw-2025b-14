package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.GameNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GameNotFoundExceptionMapper implements ExceptionMapper<GameNotFoundException> {

    @Override
    public Response toResponse(GameNotFoundException exception) {
        return ApiErrorFactory.notFound("Game not found");
    }
}
