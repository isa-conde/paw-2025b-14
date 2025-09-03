package ar.edu.itba.paw.model;

public class MatchWithPlayers {
    private final Long id;
    private final Long tournamentId;
    private final Long localId;
    private final Long visitorId;
    private final String localPlayerName;
    private final String visitorPlayerName;
    private final Integer localScore;
    private final Integer visitorScore;
    private final Integer winner;
    private final Integer stage;

    public MatchWithPlayers(Long id, Long tournamentId, Long localId, Long visitorId, 
                           String localPlayerName, String visitorPlayerName,
                           Integer localScore, Integer visitorScore, Integer winner, Integer stage) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.localId = localId;
        this.visitorId = visitorId;
        this.localPlayerName = localPlayerName;
        this.visitorPlayerName = visitorPlayerName;
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

    public String getLocalPlayerName() {
        return localPlayerName;
    }

    public String getVisitorPlayerName() {
        return visitorPlayerName;
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
