package ar.edu.itba.paw.webapp.dto.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

public class JoinTournamentTeamRequest {

    @NotNull
    @Positive
    private Long teamId;

    @NotEmpty
    private List<@NotNull @Positive Long> members;

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public List<Long> getMembers() {
        return members;
    }

    public void setMembers(List<Long> members) {
        this.members = members;
    }
}