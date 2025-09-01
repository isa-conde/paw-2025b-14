package ar.edu.itba.paw.model.filters;

import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;

import java.util.Date;

public class TournamentFilter {

    private String name;
    private Long gameid;
    private Region region;
    private Elo elo;
    private Date startdate;
    private Date enddate;
    private String format;
    private Structure structure;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getGameid() { return gameid; }
    public void setGameid(Long gameid) { this.gameid = gameid; }

    public Region getRegion() { return region; }
    public void setRegion(Region region) { this.region = region; }

    public Date getEnddate() { return enddate; }
    public void setEnddate(Date enddate) { this.enddate = enddate; }

    public Elo getElo() { return elo;}
    public void setElo(Elo elo) { this.elo = elo;}

    public Date getStartdate() { return startdate;}
    public void setStartdate(Date startdate) { this.startdate = startdate;}

    public String getFormat() { return format;}
    public void setFormat(String format) { this.format = format;}

    public Structure getStructure() { return structure;}
    public void setStructure(Structure structure) { this.structure = structure;}
}
