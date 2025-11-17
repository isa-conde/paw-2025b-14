package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.enums.Region;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class FilterForm {
    private Long gameId;
    private Region region;
    private Elo elo;
    private Genre genre;
    private Integer playersPerTeam;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;


    public Long getGameId() {
        return gameId;
    }
    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Region getRegion() {
        return region;
    }
    public void setRegion(Region region) {
        this.region = region;
    }

    public Elo getElo() {
        return elo;
    }
    public void setElo(Elo elo) {
        this.elo = elo;
    }

    public Genre getGenre() {return genre;}
    public void setGenre(Genre genre) { this.genre = genre; }

    public Integer getPlayersPerTeam() { return playersPerTeam; }
    public void setPlayersPerTeam(Integer playersPerTeam) { this.playersPerTeam = playersPerTeam; }

    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
