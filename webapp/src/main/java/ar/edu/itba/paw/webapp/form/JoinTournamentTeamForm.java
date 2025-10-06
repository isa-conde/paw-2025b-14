package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.constraints.MembersBelongToTeamConstraint;
import ar.edu.itba.paw.webapp.constraints.MembersCountConstraint;
import ar.edu.itba.paw.webapp.constraints.MembersNotInTournamentConstraint;

import javax.validation.constraints.NotNull;
import java.util.List;

@MembersCountConstraint(groups = JoinTournamentTeamForm.StepTwo.class)
@MembersBelongToTeamConstraint(groups = JoinTournamentTeamForm.StepTwo.class)
@MembersNotInTournamentConstraint(groups = JoinTournamentTeamForm.StepTwo.class)
public class JoinTournamentTeamForm {
    public interface StepOne {}
    public interface StepTwo {}

    @NotNull(message = "{team.selection.empty}", groups = StepOne.class)
    private Long teamId;
    @NotNull(groups = StepOne.class)
    private Long tournamentId;
    @NotNull(groups = StepTwo.class)
    private List<Long> members;

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
    public List<Long> getMembers() {
        return members;
    }
    public void setMembers(List<Long> members) {
        this.members = members;
    }
}
