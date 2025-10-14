package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.ids.MatchId;

import javax.persistence.*;

@Entity
@Table(name = "match")
public class Match {

    @EmbeddedId
    private final MatchId id;

    @MapsId("tournamentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "local_id")
    private final Long localId;

    @ManyToOne
    @JoinColumn(name = "visitor_id")
    private final Long visitorId;

    @Column(name = "local_score")
    private final Integer localScore;

    @Column(name = "visitor_score")
    private final Integer visitorScore;

    @Column(name = "winner")
    private final Integer winner;

    @Column(name = "stage")
    private final Integer stage;

    @Column(name = "is_group_stage")
    private final Boolean isGroupStage;

    public Match(MatchId id, Long localId, Long visitorId,
                            Integer localScore, Integer visitorScore, Integer winner, Integer stage, Boolean isGroupStage) {
        this.id = id;
        this.localId = localId;
        this.visitorId = visitorId;
        this.localScore = localScore;
        this.visitorScore = visitorScore;
        this.winner = winner;
        this.stage = stage;
        this.isGroupStage = isGroupStage;
    }

    public Long getId() {
        return id;
    }

    public Long getLocalId() {
        return localId;
    }

    public Long getVisitorId() {
        return visitorId;
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
}

