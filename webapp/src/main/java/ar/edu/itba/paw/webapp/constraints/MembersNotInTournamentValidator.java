// ar/edu/itba/paw/webapp/constraints/MembersNotInTournamentValidator.java
package ar.edu.itba.paw.webapp.constraints;

import ar.edu.itba.paw.interfaces.services.ParticipantService;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.form.JoinTournamentTeamForm;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MembersNotInTournamentValidator implements ConstraintValidator<MembersNotInTournamentConstraint, JoinTournamentTeamForm> {

    private final ParticipantService participantService;
    private final UserDao userDao;
    private final MessageSource messageSource;

    public MembersNotInTournamentValidator(ParticipantService participantService,
                                           UserDao userDao,
                                           MessageSource messageSource) {
        this.participantService = participantService;
        this.userDao = userDao;
        this.messageSource = messageSource;
    }

    @Override
    public boolean isValid(JoinTournamentTeamForm form, ConstraintValidatorContext ctx) {
        if (form == null || form.getTournamentId() == null || form.getMembers() == null) {
            return true;
        }

        final Long tournamentId = form.getTournamentId();

        List<Long> memberIds = form.getMembers().stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (memberIds.isEmpty()) {
            return true;
        }

        List<Long> alreadyIn = memberIds.stream()
                .filter(uid -> participantService.hasJoined(uid, tournamentId))
                .toList();

        if (alreadyIn.isEmpty()) {
            return true;
        }

        Map<Long, String> namesById = new HashMap<>();
        for (Long uid : alreadyIn) {
            Optional<User> u = userDao.findById(uid);
            namesById.put(uid, u.map(User::getUsername).orElse("#" + uid));
        }
        String joinedNames = alreadyIn.stream()
                .map(namesById::get)
                .collect(Collectors.joining(", "));

        String msg = messageSource.getMessage(
                "members.already_in_tournament",
                new Object[]{ joinedNames, alreadyIn.size() },
                LocaleContextHolder.getLocale()
        );

        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(msg)
                .addPropertyNode("members")
                .addConstraintViolation();
;

        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(msg)
                .addPropertyNode("members")
                .addConstraintViolation();

        return false;
    }
}