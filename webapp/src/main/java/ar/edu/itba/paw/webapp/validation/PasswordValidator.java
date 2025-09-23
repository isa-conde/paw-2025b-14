package ar.edu.itba.paw.webapp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordValidation, String> {

    @Override
    public void initialize(PasswordValidation constraintAnnotation) {

    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        if (password.length() < 8 || password.length() > 20) {
            context.buildConstraintViolationWithTemplate("Password must be between 8 and 20 characters long")
                   .addConstraintViolation();
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            context.buildConstraintViolationWithTemplate("Password must contain at least one lowercase letter")
                   .addConstraintViolation();
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            context.buildConstraintViolationWithTemplate("Password must contain at least one uppercase letter")
                   .addConstraintViolation();
            return false;
        }

        if (!password.matches(".*[0-9].*")) {
            context.buildConstraintViolationWithTemplate("Password must contain at least one digit")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}
