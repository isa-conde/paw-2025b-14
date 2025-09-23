package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.enums.Region;

public class FilterForm {
    private Long game_id;
    private Region region;
    private Elo elo;
    private Genre genre;
    private Integer playersPerTeam;


    public Long getGame_id() {
        return game_id;
    }
    public void setGame_id(Long game_id) {
        this.game_id = game_id;
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


}
