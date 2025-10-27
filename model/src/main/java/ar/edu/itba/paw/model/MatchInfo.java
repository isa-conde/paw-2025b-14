package ar.edu.itba.paw.model;

public class MatchInfo {

    private final Long id;
    private final Long tournamentId;
    private final Long local_id;
    private final Long visitor_id;
    private Participant local;
    private Participant visitor;
    private final Integer localScore;
    private final Integer visitorScore;
    private final Integer winner;
    private final Integer stage;
    private final Integer groupNumber;
    private final Boolean isGroupStage;

    public MatchInfo(Long id, Long tournamentId, Long local_id, Long visitor_id, Integer localScore, Integer visitorScore, Integer winner, Integer stage, Integer groupNumber, Boolean isGroupStage) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.local_id = local_id;
        this.visitor_id = visitor_id;
        this.localScore = localScore;
        this.visitorScore = visitorScore;
        this.winner = winner;
        this.stage = stage;
        this.groupNumber = groupNumber;
        this.isGroupStage = isGroupStage;
    }

    public Long getId() {
        return id;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public Long getLocalId() {
        return local_id;
    }

    public Long getVisitorId() {
        return visitor_id;
    }

    public Participant getLocal() {
        return local;
    }

    public void setLocal(final Participant local) {
        this.local = local;
    }

    public Participant getVisitor() {
        return visitor;
    }

    public void setVisitor(final Participant visitor) {
        this.visitor = visitor;
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

    public Integer getGroupNumber() {
        return groupNumber;
    }

    public Boolean getIsGroupStage() {
        return isGroupStage;
    }
}
