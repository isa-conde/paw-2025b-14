package ar.edu.itba.paw.webapp.dto.requests;

import ar.edu.itba.paw.webapp.validation.PasswordValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class ResetPasswordRequest {

    @NotNull
    @Positive
    private Long token;

    @PasswordValidation
    @NotBlank
    private String password;

    public Long getToken() {
        return token;
    }

    public void setToken(Long token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
