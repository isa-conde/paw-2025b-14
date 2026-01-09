package ar.edu.itba.paw.webapp.dto.params;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.QueryParam;

public class VerifyEmailParams {

    @QueryParam("userId")
    @NotBlank
    private long userId;

    @QueryParam("token")
    @NotBlank
    private long token;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getToken() {
        return token;
    }

    public void setToken(long token) {
        this.token = token;
    }
}
