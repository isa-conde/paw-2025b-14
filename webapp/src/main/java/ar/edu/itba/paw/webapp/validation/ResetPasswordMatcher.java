package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.form.ResetPasswordForm;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ResetPasswordMatcher implements ConstraintValidator<ResetPasswordMatches, Object> {

    @Override
    public void initialize(ResetPasswordMatches constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true;
        }

        ResetPasswordForm resetPasswordForm = (ResetPasswordForm) obj;
        String newPassword = resetPasswordForm.getNewPassword();
        String confirmNewPassword = resetPasswordForm.getConfirmNewPassword();

        if (newPassword == null || confirmNewPassword == null) {
            return true;
        }

        boolean passwordsMatch = newPassword.equals(confirmNewPassword);

        if (!passwordsMatch) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate("{error.passwordsDontMatch}")
                   .addPropertyNode("confirmNewPassword")
                   .addConstraintViolation();
        }

        return passwordsMatch;
    }
}
