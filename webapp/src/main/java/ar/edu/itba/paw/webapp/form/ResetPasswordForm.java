package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.PasswordMatches;
import ar.edu.itba.paw.webapp.validation.PasswordValidation;
import ar.edu.itba.paw.webapp.validation.ResetPasswordMatches;
import ar.edu.itba.paw.webapp.validation.SameAsOldPassword;

import javax.validation.constraints.NotNull;

@SameAsOldPassword
@ResetPasswordMatches
public class ResetPasswordForm {

    @PasswordValidation
    @NotNull
    private String newPassword;

    @NotNull
    private String confirmNewPassword;

    private Long userId;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
