package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.webapp.form.ResetPasswordForm;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SameAsOldPasswordValidator implements ConstraintValidator<SameAsOldPassword, ResetPasswordForm> {

    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(ResetPasswordForm form, ConstraintValidatorContext context) {
        if(form.getPassword() == null || form.getUserId() == null) return true;

        boolean isSameAsOld = userService.sameAsOldPassword(form.getPassword(), form.getUserId());

        if (isSameAsOld) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate("{passwordReset.error.sameAsOld}")
                    .addPropertyNode("newPassword")
                    .addConstraintViolation();
        }

        return !isSameAsOld;
    }
}
