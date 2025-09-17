package ar.edu.itba.paw.model.filters;

import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;

import java.time.LocalDate;

public class TournamentFilter {

    private String name;
    private Long game_id;
    private Region region;
    private Elo elo;
    private LocalDate start_date;
    private LocalDate end_date;
    private String format;
    private Structure structure;
    private Genre genre;
    private Integer playersPerTeam;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getGame_id() { return game_id; }
    public void setGame_id(Long game_id) { this.game_id = game_id; }

    public Region getRegion() { return region; }
    public void setRegion(Region region) { this.region = region; }

    public LocalDate getEnd_date() { return end_date; }
    public void setEnd_date(LocalDate end_date) { this.end_date = end_date; }

    public Elo getElo() { return elo;}
    public void setElo(Elo elo) { this.elo = elo;}

    public LocalDate getStart_date() { return start_date;}
    public void setStart_date(LocalDate start_date) { this.start_date = start_date;}

    public String getFormat() { return format;}
    public void setFormat(String format) { this.format = format;}

    public Structure getStructure() { return structure;}
    public void setStructure(Structure structure) { this.structure = structure;}

    public Genre getGenre() {return genre;}
    public void setGenre(Genre genre) { this.genre = genre; }

    public Integer getPlayersPerTeam() { return playersPerTeam; }
    public void setPlayersPerTeam(Integer playersPerTeam) { this.playersPerTeam = playersPerTeam; }

    public boolean isEmpty(){
        return name == null && game_id == null && region == null && elo == null && start_date == null && end_date == null && format == null && structure == null;
    }
}
