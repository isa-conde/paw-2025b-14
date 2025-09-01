package ar.edu.itba.paw.model;

import java.util.Date;

public class Match {
    private final Long id;
    private final Long tournamentId;
    private Long localId;
    private Long visitorId;
    private Date matchDate;
    private Integer pointsUser1;
    private Integer pointUser2;
    
    public Match(Long id, Long tournamentId) {
		this(id, tournamentId, null, null, null);
	}

    public Match(Long id, Long tournamentId, Long localId, Long visitorId, Date matchDate) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.localId = localId;
        this.visitorId = visitorId;
        this.matchDate = matchDate;
    }
    
    public void loadPoints(Integer pointsUser1, Integer pointUser2) {
    	this.pointsUser1 = pointsUser1;
		this.pointUser2 = pointUser2;
    }
    
    public void appointMatch(Date date, Long localId, Long visitorId) {
    	this.localId = localId;
		this.visitorId = visitorId;
		this.matchDate = date;
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

    public Date getMatchDate() {
        return matchDate;
    }

    public Short getWinner() {
        if(pointsUser1 == null || pointUser2 == null) return null;
        return (short) (pointsUser1 > pointUser2 ? 1 : 0);
    }

    public Integer getPointsUser1() {
        return pointsUser1;
    }

    public Integer getPointUser2() {
        return pointUser2;
    }
}
