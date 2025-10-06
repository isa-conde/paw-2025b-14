package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.model.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

@Component
public class UniqueTeamNameOnEditValidator implements ConstraintValidator<UniqueTeamNameOnEdit, EditTeamForm> {

    @Autowired
    private TeamService teamService; // o TeamDao

    @Override
    public boolean isValid(EditTeamForm form, ConstraintValidatorContext context) {
        if (form == null) return true;

        String newName = form.getName();
        Long teamId = form.getTeamId();

        if (newName == null || newName.isBlank()) return true; // @NotBlank se encarga

        Optional<Team> currentTeamOpt = teamService.getById(teamId);
        if (currentTeamOpt.isEmpty()) return true;

        Team currentTeam = currentTeamOpt.get();

        // Si el nombre no cambió → válido
        if (newName.equalsIgnoreCase(currentTeam.getName())) return true;

        // Si el nombre ya existe en otro equipo → inválido
        boolean taken = teamService.teamNameTaken(newName);
        if (taken) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{team.create.error.nameTaken}")
                    .addPropertyNode("name") // Apunta al campo 'name' del form
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}

