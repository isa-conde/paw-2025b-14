package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.exception.TournamentNotFoundException;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.webapp.form.SetMatchResultsForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class NoTieOnEliminationValidator implements ConstraintValidator<NoTieOnEliminationConstraint, SetMatchResultsForm> {

    private final TournamentDao tournamentDao;

    @Autowired
    public NoTieOnEliminationValidator(TournamentDao tournamentDao) {
        this.tournamentDao = tournamentDao;
    }

    @Override
    public boolean isValid(SetMatchResultsForm form, ConstraintValidatorContext context) {
        if (form == null) return true;

        Long tournamentId = form.getTournamentId();
        Integer local = form.getLocalScore();
        Integer visitor = form.getVisitorScore();

        if (tournamentId == null || local == null || visitor == null) {
            return true;
        }

        Tournament tournament = tournamentDao.findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        Structure structure = tournament.getStructure();
        Boolean isGroupStage = tournament.getIsGroupStage();
        if (structure.equals(Structure.ELIMINATION) || (structure.equals(Structure.HYBRID) && isGroupStage.equals(Boolean.FALSE))) {
            boolean tie = local.equals(visitor);
            if (tie) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{setMatchResultsForm.noTieOnEliminationConstraint}")
                        .addPropertyNode("localScore")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
