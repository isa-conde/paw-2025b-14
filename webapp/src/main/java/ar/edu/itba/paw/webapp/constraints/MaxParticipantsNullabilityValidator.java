package ar.edu.itba.paw.webapp.constraints;

import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.webapp.form.EditTournamentForm;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class MaxParticipantsNullabilityValidator
        implements ConstraintValidator<MaxParticipantsNullabilityConstraint, EditTournamentForm> {

    private final TournamentService ts;

    public MaxParticipantsNullabilityValidator(TournamentService ts) {
        this.ts = ts;
    }

    @Override
    public boolean isValid(EditTournamentForm form, ConstraintValidatorContext ctx) {
        if (form == null || form.getTournamentId() == null) return true;

        var opt = ts.findById(form.getTournamentId());
        if (opt.isEmpty()) return true;

        boolean openInscriptions = Boolean.TRUE.equals(opt.get().getOpenInscriptions());

        boolean ok;
        if (openInscriptions) {
            ok = (form.getMaxParticipants() != null);
            if (!ok) {
                ctx.disableDefaultConstraintViolation();
                ctx.buildConstraintViolationWithTemplate("{tournament.maxParticipants.requiredIfOpen}")
                        .addPropertyNode("maxParticipants")
                        .addConstraintViolation();
            }
        } else {
            ok = (form.getMaxParticipants() == null);
            if (!ok) {
                ctx.disableDefaultConstraintViolation();
                ctx.buildConstraintViolationWithTemplate("{tournament.maxParticipants.mustBeNullIfClosed}")
                        .addPropertyNode("maxParticipants")
                        .addConstraintViolation();
            }
        }
        return ok;
    }
}
