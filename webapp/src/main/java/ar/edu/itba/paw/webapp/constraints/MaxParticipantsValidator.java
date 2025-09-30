package ar.edu.itba.paw.webapp.constraints;

import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.webapp.form.EditTournamentForm;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class MaxParticipantsValidator implements ConstraintValidator<MaxParticipantsNotBelowCurrent, EditTournamentForm> {

    private final TournamentService ts;
    public MaxParticipantsValidator(TournamentService ts){ this.ts = ts; }

    @Override
    public boolean isValid(EditTournamentForm form, ConstraintValidatorContext ctx) {
        if (form == null || form.getTournamentId() == null || form.getMax_participants() == null) return true;
        int current = ts.tournamentParticipantsCount(form.getTournamentId());
        if (form.getMax_participants() < current) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(
                            "Max participants cannot be less than current participants (" + current + ").")
                    .addPropertyNode("max_participants")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}

