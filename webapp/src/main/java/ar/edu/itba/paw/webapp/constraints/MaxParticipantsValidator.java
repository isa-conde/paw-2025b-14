package ar.edu.itba.paw.webapp.constraints;

import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.webapp.form.EditTournamentForm;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class MaxParticipantsValidator implements ConstraintValidator<MaxParticipantsNotBelowCurrent, EditTournamentForm> {

    private final TournamentService ts;
    public MaxParticipantsValidator(TournamentService ts){ this.ts = ts; }

    @Override
    public boolean isValid(EditTournamentForm form, ConstraintValidatorContext ctx) {
        if (form == null || form.getTournamentId() == null || form.getMaxParticipants() == null) return true;

        int current = ts.getTournamentParticipantsCount(form.getTournamentId());
        if (form.getMaxParticipants() < current) {
            ctx.disableDefaultConstraintViolation();

            HibernateConstraintValidatorContext hctx = ctx.unwrap(HibernateConstraintValidatorContext.class);
            hctx.addMessageParameter("current", current)
                    .buildConstraintViolationWithTemplate("{tournament.maxParticipants.belowCurrent}")
                    .addPropertyNode("maxParticipants")
                    .addConstraintViolation();

            return false;
        }
        return true;
    }
}