package ar.edu.itba.paw.model.filters;

import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;

import java.time.LocalDate;

public class TournamentFilter {

    private String name;
    private Long gameId;
    private Region region;
    private Elo elo;
    private LocalDate startDate;
    private LocalDate endDate;
    private String format;
    private Structure structure;
    private Genre genre;
    private Integer playersPerTeam;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public Region getRegion() { return region; }
    public void setRegion(Region region) { this.region = region; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Elo getElo() { return elo;}
    public void setElo(Elo elo) { this.elo = elo;}

    public LocalDate getStartDate() { return startDate;}
    public void setStartDate(LocalDate startDate) { this.startDate = startDate;}

    public String getFormat() { return format;}
    public void setFormat(String format) { this.format = format;}

    public Structure getStructure() { return structure;}
    public void setStructure(Structure structure) { this.structure = structure;}

    public Genre getGenre() {return genre;}
    public void setGenre(Genre genre) { this.genre = genre; }

    public Integer getPlayersPerTeam() { return playersPerTeam; }
    public void setPlayersPerTeam(Integer playersPerTeam) { this.playersPerTeam = playersPerTeam; }

    public boolean isEmpty(){
        return name == null && gameId == null && region == null && elo == null && startDate == null && endDate == null && format == null && structure == null && genre == null && playersPerTeam == null;
    }
}
