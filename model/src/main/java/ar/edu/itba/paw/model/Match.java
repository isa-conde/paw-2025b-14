package ar.edu.itba.paw.model;

public class Match {
    private final Long id;
    private final Long tournamentId;
    private Long localId;
    private Long visitorId;
    // private Date matchDate;
    private Integer localScore;
    private Integer visitorScore;
    private Integer winner;
    private Integer stage;
    
    public Match(Long id, Long tournamentId) {
		this(id, tournamentId, null, null, null, null, null, null);
	}

    public Match(Long id, Long tournamentId, Long localId, Long visitorId/*, Date matchDate*/, Integer localScore, Integer visitorScore, Integer winner, Integer stage) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.localId = localId;
        this.visitorId = visitorId;
        // this.matchDate = matchDate;
        this.localScore = localScore;
        this.visitorScore = visitorScore;
        this.winner = winner;
        this.stage = stage;
    }
    
    public void loadPoints(Integer localScore, Integer visitorScore) {
    	if(localId == null || visitorId == null) throw new IllegalStateException("Cannot load points to a match without teams assigned");
    	this.localScore = localScore;
		this.visitorScore = visitorScore;
    }
    
    public void appointMatch(/* Date date, */ Long localId, Long visitorId) {
    	this.localId = localId;
		this.visitorId = visitorId;
		// this.matchDate = date;
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

//    public Date getMatchDate() {
//        return matchDate;
//    }

    public Integer getWinner() {
        return winner;
    }

    public void setWinner(Integer winner) {
        this.winner = winner;
    }

    public Integer getLocalScore() {
        return localScore;
    }

    public Integer getVisitorScore() {
        return visitorScore;
    }

    public Integer getStage() {
        return stage;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }
}
