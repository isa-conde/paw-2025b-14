package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.PasswordMatches;
import ar.edu.itba.paw.webapp.validation.PasswordValidation;
import ar.edu.itba.paw.webapp.validation.SameAsOldPassword;

import javax.validation.constraints.NotNull;

@SameAsOldPassword
@PasswordMatches
public class ResetPasswordForm implements HasPasswordMatcher {

    @PasswordValidation
    @NotNull
    private String password;

    @NotNull
    private String repeatPassword;

    private Long userId;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
