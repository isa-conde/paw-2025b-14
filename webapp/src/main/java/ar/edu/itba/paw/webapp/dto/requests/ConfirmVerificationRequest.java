package ar.edu.itba.paw.webapp.dto.requests;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class ConfirmVerificationRequest {

    @NotNull
    @Positive
    private Long token;

    public Long getToken() {
        return token;
    }

    public void setToken(Long token) {
        this.token = token;
    }
}
