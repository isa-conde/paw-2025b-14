package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.constraints.ExistingUsersContraint;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class JoinTournamentTeamForm {
    public interface StepOne {}
    public interface StepTwo {}

    @NotNull(groups = StepOne.class)
    private Long teamId;
    @NotNull(groups = StepTwo.class)
    private Long tournamentId;
    @NotNull(groups = StepTwo.class)
    @ExistingUsersContraint
    private List<String> members;

    public Long getTeamId() {
        return teamId;
    }
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
    public Long getTournamentId() {
        return tournamentId;
    }
    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }
    public List<String> getMembers() {
        return members;
    }
    public void setMembers(List<String> members) {
        this.members = members;
    }
}
