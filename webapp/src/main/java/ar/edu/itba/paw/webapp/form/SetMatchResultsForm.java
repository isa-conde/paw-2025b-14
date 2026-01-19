package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.NoTieOnEliminationConstraint;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@NoTieOnEliminationConstraint
public class SetMatchResultsForm {
    
    @NotNull
    private Long matchId;
    
    @NotNull
    private Long tournamentId;

    @NotNull(message = "{setMatchResultsForm.localScore.notNull}")
    @Min(value = 0, message = "{setMatchResultsForm.localScore.min}")
    private Integer localScore;

    @NotNull(message = "{setMatchResultsForm.visitorScore.notNull}")
    @Min(value = 0, message = "{setMatchResultsForm.visitorScore.min}")
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
