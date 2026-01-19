package ar.edu.itba.paw.webapp.dto.entities;

import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.function.Function;

public class GameFormatDTO {

    private long id;
    private String name;
    private int playersPerTeam;

    private URI self;
    private URI game;

    public static Function<GameFormat, GameFormatDTO> mapper(final UriInfo uriInfo) {
        return (g) -> fromGameFormat(uriInfo, g);
    }

    public static GameFormatDTO fromGameFormat(UriInfo uriInfo, GameFormat gameFormat){
        GameFormatDTO toReturn = new GameFormatDTO();

        toReturn.setId(gameFormat.getId());
        toReturn.setName(gameFormat.getName());
        toReturn.setPlayersPerTeam(gameFormat.getPlayersPerTeam());

        toReturn.setSelf(uriInfo.getAbsolutePathBuilder().path("games")
                .path(gameFormat.getGameId().toString()).path("formats").path(gameFormat.getId().toString()).build());
        toReturn.setGame(uriInfo.getAbsolutePathBuilder().path("games")
                .path(gameFormat.getGameId().toString()).build());

        return toReturn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlayersPerTeam() {
        return playersPerTeam;
    }

    public void setPlayersPerTeam(int playersPerTeam) {
        this.playersPerTeam = playersPerTeam;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getGame() {
        return game;
    }

    public void setGame(URI game) {
        this.game = game;
    }
}
