package ar.edu.itba.paw.model.filters;

import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;

import java.util.Date;

public class TournamentFilter {

    private String name;
    private Long game_id;
    private Region region;
    private Elo elo;
    private Date start_date;
    private Date end_date;
    private String format;
    private Structure structure;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getGame_id() { return game_id; }
    public void setGame_id(Long game_id) { this.game_id = game_id; }

    public Region getRegion() { return region; }
    public void setRegion(Region region) { this.region = region; }

    public Date getEnd_date() { return end_date; }
    public void setEnd_date(Date end_date) { this.end_date = end_date; }

    public Elo getElo() { return elo;}
    public void setElo(Elo elo) { this.elo = elo;}

    public Date getStart_date() { return start_date;}
    public void setStart_date(Date start_date) { this.start_date = start_date;}

    public String getFormat() { return format;}
    public void setFormat(String format) { this.format = format;}

    public Structure getStructure() { return structure;}
    public void setStructure(Structure structure) { this.structure = structure;}
}
