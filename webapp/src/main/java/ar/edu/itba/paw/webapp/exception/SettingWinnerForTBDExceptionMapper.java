package ar.edu.itba.paw.webapp.exception;

import ar.edu.itba.paw.interfaces.exception.SettingWinnerForTBDException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SettingWinnerForTBDExceptionMapper implements ExceptionMapper<SettingWinnerForTBDException> {

    @Override
    public Response toResponse(SettingWinnerForTBDException exception) {
        return BadRequestMessages.badRequest(exception, "Cannot set a winner for a TBD match");
    }
}
