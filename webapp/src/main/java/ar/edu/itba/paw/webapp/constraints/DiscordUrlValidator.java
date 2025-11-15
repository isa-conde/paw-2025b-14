package ar.edu.itba.paw.webapp.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DiscordUrlValidator implements ConstraintValidator<DiscordUrlConstraint, String> {
    @Override
    public void initialize(DiscordUrlConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (value == null || value.isEmpty()){
            return true;
        }
        if (!isDiscordChannelUrl(value)){
            isValid = false;

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.tournamentForm.notADiscordLink}")
                    .addConstraintViolation();
        }
        return isValid;
    }

    private boolean isDiscordChannelUrl(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        String regex =
                "^https://(discord\\.gg|discord(?:app)?\\.com)/[A-Za-z0-9/@._-]+$";

        return value.matches(regex);
    }
}