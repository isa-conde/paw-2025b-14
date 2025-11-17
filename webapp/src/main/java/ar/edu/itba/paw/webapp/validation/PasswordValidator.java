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
            context.buildConstraintViolationWithTemplate("{error.registerForm.invalidPasswordLength}")
                   .addConstraintViolation();
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            context.buildConstraintViolationWithTemplate("{error.registerForm.invalidPasswordLowercase}")
                   .addConstraintViolation();
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            context.buildConstraintViolationWithTemplate("{error.registerForm.invalidPasswordUppercase}")
                   .addConstraintViolation();
            return false;
        }

        if (!password.matches(".*[0-9].*")) {
            context.buildConstraintViolationWithTemplate("{error.registerForm.invalidPasswordDigit}")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}
