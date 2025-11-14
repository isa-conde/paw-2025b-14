package ar.edu.itba.paw.webapp.constraints;

import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.webapp.form.EditTournamentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

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

        Optional<Tournament> optTournament = tournamentService.findById(form.getTournamentId());
        if (optTournament.isEmpty()) return true;
        Tournament tournament = optTournament.get();

        boolean started = Boolean.TRUE.equals(tournament.getTournamentStarted());
        boolean finished = Boolean.TRUE.equals(tournament.getFinished());

        boolean valid = true;

        if (started && form.getStartDate() != null) {
            ctx.buildConstraintViolationWithTemplate("{tournament.start.mustBeNullIfStarted}")
                    .addPropertyNode("startDate").addConstraintViolation();
            valid = false;
        } else if (!started && form.getStartDate() == null) {
            ctx.buildConstraintViolationWithTemplate("{tournament.start.requiredIfNotStarted}")
                    .addPropertyNode("startDate").addConstraintViolation();
            valid = false;
        }

        if (finished && form.getEndDate() != null) {
            ctx.buildConstraintViolationWithTemplate("{tournament.end.mustBeNullIfFinished}")
                    .addPropertyNode("endDate").addConstraintViolation();
            valid = false;
        } else if (!finished && form.getEndDate() == null) {
            ctx.buildConstraintViolationWithTemplate("{tournament.end.requiredIfNotFinished}")
                    .addPropertyNode("endDate").addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}