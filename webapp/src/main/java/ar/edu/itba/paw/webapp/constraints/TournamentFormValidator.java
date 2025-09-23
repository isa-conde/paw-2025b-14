package ar.edu.itba.paw.webapp.constraints;

import ar.edu.itba.paw.webapp.form.TournamentForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class TournamentFormValidator implements ConstraintValidator<ValidTournamentFormConstraint, TournamentForm> {

    @Override
    public boolean isValid(TournamentForm value, ConstraintValidatorContext context) {
        if (value.getStart_date() == null || value.getEnd_date() == null){
            return true;
        }

        boolean valid = true;
        if (!value.getStart_date().isAfter(LocalDate.now()) || !value.getEnd_date().isAfter(LocalDate.now()) || !value.getStart_date().isBefore(value.getEnd_date())){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.tournamentForm.invalidDates}")
                    .addPropertyNode("start_date")
                    .addConstraintViolation();
            valid = false;
        }
        if (value.getImage().isEmpty()){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.tournamentForm.emptyImage}")
                    .addPropertyNode("image")
                    .addConstraintViolation();
            valid = false;
        }
        return valid;
    }
}
