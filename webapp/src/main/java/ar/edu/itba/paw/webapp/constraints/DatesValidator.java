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

        LocalDate start = form.getStart_date();
        LocalDate end = form.getEnd_date();

        boolean valid = true;

        if (start == null || end == null) return true;

        if (start.isBefore(today)){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.tournamentForm.invalidStartDate}")
                    .addPropertyNode("start_date")
                    .addConstraintViolation();
            valid = false;
        }

        if (end.isBefore(today)){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.tournamentForm.invalidEndDate}")
                    .addPropertyNode("end_date")
                    .addConstraintViolation();
            valid = false;
        }

        if (start.isAfter(end)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.tournamentForm.invalidDates}")
                    .addPropertyNode("end_date")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
