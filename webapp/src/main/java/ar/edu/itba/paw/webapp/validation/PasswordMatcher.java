package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.form.HasPasswordMatcher;
import ar.edu.itba.paw.webapp.form.UserForm;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatcher implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true;
        }

        HasPasswordMatcher form = (HasPasswordMatcher) obj;
        String password = form.getPassword();
        String repeatPassword = form.getRepeatPassword();

        if (password == null || repeatPassword == null) {
            return true;
        }

        boolean passwordsMatch = password.equals(repeatPassword);

        if (!passwordsMatch) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate("{error.passwordsDontMatch}")
                   .addPropertyNode("repeatPassword")
                   .addConstraintViolation();
        }

        return passwordsMatch;
    }
}
