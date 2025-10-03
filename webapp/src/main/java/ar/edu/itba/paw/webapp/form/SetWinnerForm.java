package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class SetWinnerForm {
    
    @NotNull
    private Long matchId;
    
    @NotNull
    private Long tournamentId;
    
    @NotNull
    private Integer winner; // 1 for local, 2 for visitor

    private Integer group;
    
    
    public Long getMatchId() {
        return matchId;
    }
    
    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }
    
    public Long getTournamentId() {
        return tournamentId;
    }
    
    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }
    
    public Integer getWinner() {
        return winner;
    }
    
    public void setWinner(Integer winner) {
        this.winner = winner;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }
}
