package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class SetMatchResultsForm {
    
    @NotNull
    private Long matchId;
    
    @NotNull
    private Long tournamentId;
    
    @NotNull
    private Integer localScore;

    @NotNull
    private Integer visitorScore;

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
    
    public Integer getLocalScore() {
        return localScore;
    }

    public void setLocalScore(Integer localScore) {
        this.localScore = localScore;
    }

    public Integer getVisitorScore() {
        return visitorScore;
    }

    public void setVisitorScore(Integer visitorScore) {
        this.visitorScore = visitorScore;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }
}
