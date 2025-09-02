package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;

import java.time.LocalDate;
import java.util.List;

public class Tournament {

    private final Long id;
    private final Long creatorid;
    private final String name;
    private final Long gameid;
    private final Region region;
    private final Elo elo;
    private final LocalDate startdate;
    private final LocalDate enddate;
    private final String format;
    private final Structure structure;
    private final Integer max_participants;



    public Tournament(Long id, Long creatorid, String name, Long gameid, Region region, Elo elo, LocalDate startdate, LocalDate enddate, String format, Structure structure, Integer max_participants) {
        this.id = id;
        this.creatorid = creatorid;
        this.name = name;
        this.gameid = gameid;
        this.region = region;
        this.elo = elo;
        this.startdate = startdate;
        this.enddate = enddate;
        this.format = format;
        this.structure = structure;
        this.max_participants = max_participants;
    }

    public Long getId() {
        return id;
    }

    public Long getCreatorid() {
        return creatorid;
    }

    public String getName() {
        return name;
    }

    public Long getGameid() {
        return gameid;
    }

    public Region getRegion() {
        return region;
    }

    public Elo getElo() {
        return elo;
    }

    public LocalDate getStartdate() {
        return startdate;
    }

    public LocalDate getEnddate() {
        return enddate;
    }

    public String getFormat() {
        return format;
    }

    public Structure getStructure() {
        return structure;
    }

    public Integer getMax_participants() {
        return max_participants;
    }
    
    public boolean isFull(int currentParticipants) {
		return currentParticipants >= max_participants;
	}
    
    public int amountOfMatches() {
    	return structure.amountOfMatches(max_participants);
    }
    
    public List<Pair<Integer,Integer>> firstMatches(int participant) {
    	return structure.firstMatches(participant, max_participants);
	}
}
