package ar.edu.itba.paw.model;

import java.util.Date;

public class Match {
    private final Long id;
    private final Long tournamentId;
    private Long localId;
    private Long visitorId;
    // private Date matchDate;
    private Integer localScore;
    private Integer visitorScore;
    
    public Match(Long id, Long tournamentId) {
		this(id, tournamentId, null, null, null);
	}

    public Match(Long id, Long tournamentId, Long localId, Long visitorId, Date matchDate) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.localId = localId;
        this.visitorId = visitorId;
        // this.matchDate = matchDate;
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

    public Short getWinner() {
        if(localScore == null || visitorScore == null) return null;
        return (short) (localScore > visitorScore ? 0 : 1);
    }

    public Integer getLocalScore() {
        return localScore;
    }

    public Integer getVisitorScore() {
        return visitorScore;
    }
}
