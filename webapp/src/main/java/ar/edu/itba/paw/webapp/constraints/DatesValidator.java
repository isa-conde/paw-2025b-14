package ar.edu.itba.paw.webapp.constraints;

import ar.edu.itba.paw.webapp.form.HasDates;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DatesValidator implements ConstraintValidator<DatesConstraint, HasDates> {
    @Override
    public void initialize(DatesConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(HasDates form, ConstraintValidatorContext context) {
        if (form == null) return true;

        LocalDate today = LocalDate.now();

        LocalDate start = form.getStartDate();
        LocalDate end = form.getEndDate();

        boolean valid = true;

        if (start == null || end == null) return true;

        if (start.isBefore(today)){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.tournamentForm.invalidStartDate}")
                    .addPropertyNode("startDate")
                    .addConstraintViolation();
            valid = false;
        }

        if (end.isBefore(today)){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.tournamentForm.invalidEndDate}")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            valid = false;
        }

        if (start.isAfter(end)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.tournamentForm.invalidDates}")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
