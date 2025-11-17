package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.*;

public class SwapMatchesForm {
    @NotNull @Positive
    private Long tournamentId;

    @NotNull @Positive
    private Long match1;

    @NotNull @Positive
    private Long match2;

    @NotNull @Positive
    private Long user1;

    @NotNull
    @Positive
    private Long user2;

    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long v) { this.tournamentId = v; }

    public Long getMatch1() { return match1; }
    public void setMatch1(Long v) { this.match1 = v; }

    public Long getMatch2() { return match2; }
    public void setMatch2(Long v) { this.match2 = v; }

    public Long getUser1() { return user1; }
    public void setUser1(Long v) { this.user1 = v; }

    public Long getUser2() { return user2; }
    public void setUser2(Long v) { this.user2 = v; }
}
