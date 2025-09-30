package ar.edu.itba.paw.model;

public class Match {
    private final Long id;
    private final Long tournamentId;
    private final Long localId;
    private final Long visitorId;
    private final Integer localScore;
    private final Integer visitorScore;
    private final Integer winner;
    private final Integer stage;

    public Match(Long id, Long tournamentId, Long localId, Long visitorId,
                            Integer localScore, Integer visitorScore, Integer winner, Integer stage) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.localId = localId;
        this.visitorId = visitorId;
        this.localScore = localScore;
        this.visitorScore = visitorScore;
        this.winner = winner;
        this.stage = stage;
    }

    public Long getId() {
        return id;
    }

    public Long getTournamentId() {
        return tournamentId;
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
}

