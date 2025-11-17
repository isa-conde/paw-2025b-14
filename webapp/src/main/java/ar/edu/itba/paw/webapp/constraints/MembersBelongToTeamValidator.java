package ar.edu.itba.paw.webapp.constraints;

import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.form.JoinTournamentTeamForm;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MembersBelongToTeamValidator implements ConstraintValidator<MembersBelongToTeamConstraint, JoinTournamentTeamForm> {

    private final TeamService teamService;
    private final MessageSource messageSource;

    public MembersBelongToTeamValidator(TeamService teamService,
                                        MessageSource messageSource) {
        this.teamService = teamService;
        this.messageSource = messageSource;
    }

    @Override
    public boolean isValid(JoinTournamentTeamForm form, ConstraintValidatorContext ctx) {
        if (form == null || form.getTeamId() == null || form.getMembers() == null) return true;

        Set<Long> allowed = teamService.getTeamMembers(form.getTeamId()).stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        List<Long> invalid = form.getMembers().stream()
                .filter(Objects::nonNull).distinct()
                .filter(id -> !allowed.contains(id))
                .toList();

        if (invalid.isEmpty()) return true;

        String msg = messageSource.getMessage(
                "team.selection.not_in_team", null,
                LocaleContextHolder.getLocale());

        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(msg)
                .addPropertyNode("members")
                .addConstraintViolation();

        return false;
    }
}

