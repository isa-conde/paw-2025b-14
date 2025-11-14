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
                "^https://(discord\\.com|discordapp\\.com)/channels/(@me|\\d+)/\\d+$";

        return value.matches(regex);
    }
}