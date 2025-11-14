package ar.edu.itba.paw.webapp.constraints;

import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.webapp.form.EditTeamForm;
import ar.edu.itba.paw.webapp.form.JoinTournamentTeamForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.Optional;

@Component
public class MembersCountValidator implements ConstraintValidator<MembersCountConstraint, JoinTournamentTeamForm> {

    private final TournamentService tournamentService;
    private final MessageSource messageSource;

    public MembersCountValidator(TournamentService tournamentService,
                                 MessageSource messageSource) {
        this.tournamentService = tournamentService;
        this.messageSource = messageSource;
    }

    @Override
    public boolean isValid(JoinTournamentTeamForm form, ConstraintValidatorContext ctx) {
        if (form == null || form.getTournamentId() == null || form.getMembers() == null) return true;

        int required = Optional.ofNullable(tournamentService.getPlayersPerTeam(form.getTournamentId()))
                .orElse(1);
        int actual = (int) form.getMembers().stream().filter(Objects::nonNull).distinct().count();

        if (actual == required) return true;

        String msg = messageSource.getMessage(
                "team.selection.size.mismatch",
                new Object[]{required},
                LocaleContextHolder.getLocale());

        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(msg)
                .addPropertyNode("members")
                .addConstraintViolation();

        return false;
    }

    @Component
    public static class UniqueTeamNameOnEditValidator implements ConstraintValidator<TeamNameNotTakenConstraint.UniqueTeamNameOnEdit, EditTeamForm> {

        @Autowired
        private TeamService teamService;

        @Override
        public boolean isValid(EditTeamForm form, ConstraintValidatorContext context) {
            if (form == null) return true;

            String newName = form.getName();
            Long teamId = form.getTeamId();

            if (newName == null || newName.isBlank()) return true;

            Optional<Team> currentTeamOpt = teamService.findById(teamId);
            if (currentTeamOpt.isEmpty()) return true;

            Team currentTeam = currentTeamOpt.get();

            if (newName.equalsIgnoreCase(currentTeam.getName())) return true;

            boolean taken = teamService.teamNameTaken(newName);
            if (taken) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{team.create.error.nameTaken}")
                        .addPropertyNode("name")
                        .addConstraintViolation();
                return false;
            }

            return true;
        }
    }
}