package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TeamNameNotTakenValidator implements ConstraintValidator<TeamNameNotTakenConstraint, String> {

    @Autowired
    private TeamService teamService;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null || name.trim().isEmpty()) {
            return true;
        }
        return !teamService.teamNameTaken(name);
    }
}
