package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.*;

public class SwapGroupsForm {
    @NotNull @Positive
    private Long tournamentId;

    @NotNull @Positive
    private Long user1;

    @NotNull @Positive
    private Long user2;

    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long v) { this.tournamentId = v; }

    public Long getUser1() { return user1; }
    public void setUser1(Long v) { this.user1 = v; }

    public Long getUser2() { return user2; }
    public void setUser2(Long v) { this.user2 = v; }
}
