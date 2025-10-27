package ar.edu.itba.paw.webapp.constraints;

import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.webapp.constraints.DatesNullabilityConstraint;
import ar.edu.itba.paw.webapp.form.EditTournamentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class DatesNullabilityValidator implements ConstraintValidator<DatesNullabilityConstraint, EditTournamentForm> {

    private final TournamentService tournamentService;

    @Autowired
    public DatesNullabilityValidator(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @Override
    public boolean isValid(EditTournamentForm form, ConstraintValidatorContext ctx) {
        if (form == null) return true;
        ctx.disableDefaultConstraintViolation();

        var t = tournamentService.findById(form.getTournamentId()).orElse(null);
        if (t == null) {
            return true;
        }

        boolean started = Boolean.TRUE.equals(t.getTournamentStarted());
        boolean finished = Boolean.TRUE.equals(t.getFinished());

        boolean valid = true;

        if (started && form.getStart_date() != null) {
            ctx.buildConstraintViolationWithTemplate("{tournament.start.mustBeNullIfStarted}")
                    .addPropertyNode("start_date").addConstraintViolation();
            valid = false;
        } else if (!started && form.getStart_date() == null) {
            ctx.buildConstraintViolationWithTemplate("{tournament.start.requiredIfNotStarted}")
                    .addPropertyNode("start_date").addConstraintViolation();
            valid = false;
        }

        if (finished && form.getEnd_date() != null) {
            ctx.buildConstraintViolationWithTemplate("{tournament.end.mustBeNullIfFinished}")
                    .addPropertyNode("end_date").addConstraintViolation();
            valid = false;
        } else if (!finished && form.getEnd_date() == null) {
            ctx.buildConstraintViolationWithTemplate("{tournament.end.requiredIfNotFinished}")
                    .addPropertyNode("end_date").addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}