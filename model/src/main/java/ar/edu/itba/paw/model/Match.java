package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.ids.MatchId;

import javax.persistence.*;

@Entity
@Table(name = "match")
public class Match {

    @EmbeddedId
    private MatchId id;

    @MapsId("tournamentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "local_id")
    private Participant local;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "visitor_id")
    private Participant visitor;

    @Column(name = "local_score")
    private Integer localScore;

    @Column(name = "visitor_score")
    private Integer visitorScore;

    @Column(name = "winner")
    private Integer winner;

    @Column(name = "stage")
    private Integer stage;

    @Column(name = "is_group_stage")
    private Boolean isGroupStage;

    public Match(MatchId id, Integer localScore, Integer visitorScore, Integer winner, Integer stage, Boolean isGroupStage) {
        this.id = id;
        this.localScore = localScore;
        this.visitorScore = visitorScore;
        this.winner = winner;
        this.stage = stage;
        this.isGroupStage = isGroupStage;
    }

    public Match(){}

    public Long getId() {
        return id.getId();
    }

    public Long getTournamentId() {
        return id.getTournamentId();
    }

    public Long getLocalId() {
        return local.getId();
    }

    public Long getVisitorId() {
        return visitor.getId();
    }

    public Integer getLocalScore() {
        return localScore;
    }

    public Integer getVisitorScore() {
        return visitorScore;
    }

    public Integer getWinner() {
        return winner;
    }

    public Integer getStage() {
        return stage;
    }

    public Boolean getIsGroupStage() {
        return isGroupStage;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Boolean getGroupStage() {
        return isGroupStage;
    }

    public Participant getLocal() {
        return local;
    }

    public Participant getVisitor() {
        return visitor;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public void setWinner(Integer winner) {
        this.winner = winner;
    }

    public void setGroupStage(Boolean groupStage) {
        isGroupStage = groupStage;
    }

    public void setLocal(Participant local) {
        this.local = local;
    }

    public void setLocalScore(Integer localScore) {
        this.localScore = localScore;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }

    public void setVisitor(Participant visitor) {
        this.visitor = visitor;
    }

    public void setVisitorScore(Integer visitorScore) {
        this.visitorScore = visitorScore;
    }
}

