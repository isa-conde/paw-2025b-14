package ar.edu.itba.paw.webapp.dto.entities;

import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.User;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.function.Function;

public class GameDTO {

    private long id;
    private String name;
    private String genre;
    private String imageUrl;

    private URI gameFormats;
    private URI self;
    private URI gameTournaments;

    public static Function<Game, GameDTO> mapper(final UriInfo uriInfo) {
        return (g) -> fromGame(uriInfo, g);
    }

    public static GameDTO fromGame (final UriInfo uriInfo, final Game game){
        GameDTO toReturn = new GameDTO();

        toReturn.setId(game.getId());
        toReturn.setGenre(game.getGenre().name());
        toReturn.setName(game.getName());

        toReturn.setImageUrl(uriInfo.getAbsolutePathBuilder().path("images")
                .path(game.getImageId().toString()).toTemplate());
        toReturn.setGameFormats(uriInfo.getAbsolutePathBuilder().path("games")
                .path(game.getId().toString()).path("formats").build());
        toReturn.setSelf(uriInfo.getAbsolutePathBuilder().path("games")
                .path(game.getId().toString()).build());
        toReturn.setGameTournaments(uriInfo.getAbsolutePathBuilder().path("tournaments")
                .queryParam("gameId", game.getId()).build());

        return toReturn;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setGameFormats(URI gameFormats) {
        this.gameFormats = gameFormats;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getGenre() {
        return genre;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public URI getGameFormats() {
        return gameFormats;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getGameTournaments() {
        return gameTournaments;
    }

    public void setGameTournaments(URI gameTournaments) {
        this.gameTournaments = gameTournaments;
    }
}
