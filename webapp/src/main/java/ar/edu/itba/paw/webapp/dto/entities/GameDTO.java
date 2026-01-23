package ar.edu.itba.paw.webapp.dto.entities;

import ar.edu.itba.paw.model.Game.Game;

import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GameDTO {

    private long id;
    private String name;
    private String genre;
    private String imageUrl;

    private List<LinkDTO> links = new ArrayList<>();

    public static Function<Game, GameDTO> mapper(final UriInfo uriInfo) {
        return (g) -> fromGame(uriInfo, g);
    }

    public static GameDTO fromGame(final UriInfo uriInfo, final Game game) {
        GameDTO toReturn = new GameDTO();

        toReturn.id = game.getId();
        toReturn.genre = game.getGenre().name();
        toReturn.name = game.getName();

        toReturn.imageUrl = uriInfo.getAbsolutePathBuilder().path("images")
                .path(game.getImageId().toString()).toTemplate();

        toReturn.addLink("self", uriInfo.getAbsolutePathBuilder().path("games")
                .path(String.valueOf(game.getId()))
                .build().toString());

        toReturn.addLink("formats", uriInfo.getAbsolutePathBuilder().path("games")
                .path(String.valueOf(game.getId()))
                .path("formats")
                .build().toString());

        toReturn.addLink("tournaments", uriInfo.getAbsolutePathBuilder().path("tournaments")
                .queryParam("gameId", game.getId())
                .build().toString());

        toReturn.addLink("image", uriInfo.getAbsolutePathBuilder().path("images")
                .path(game.getImageId().toString())
                .build().toString());

        return toReturn;
    }

    private void addLink(String rel, String href) {
        links.add(new LinkDTO(rel, href));
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<LinkDTO> getLinks() {
        return links;
    }

    public void setLinks(List<LinkDTO> links) {
        this.links = links;
    }
}