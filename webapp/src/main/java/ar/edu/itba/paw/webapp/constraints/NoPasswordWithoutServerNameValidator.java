package ar.edu.itba.paw.webapp.constraints;

import ar.edu.itba.paw.webapp.form.HasServer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoPasswordWithoutServerNameValidator implements ConstraintValidator<NoPasswordWithoutServerNameConstraint, HasServer> {
    @Override
    public void initialize(NoPasswordWithoutServerNameConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(HasServer form, ConstraintValidatorContext context) {
        boolean isValid = true;
        String serverName = form.getServerName();
        String serverPassword = form.getServerPassword();

        if ((serverName == null || serverName.isEmpty()) && (serverPassword != null && !serverPassword.isEmpty())) {
            isValid = false;

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.tournamentForm.noServerName}")
                    .addConstraintViolation();
        }
        return isValid;
    }
}
