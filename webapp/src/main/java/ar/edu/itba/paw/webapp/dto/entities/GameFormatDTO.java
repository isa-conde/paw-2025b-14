package ar.edu.itba.paw.webapp.dto.entities;

import ar.edu.itba.paw.model.Game.GameFormat;

import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GameFormatDTO {

    private long id;
    private String name;
    private int playersPerTeam;

    private List<LinkDTO> links = new ArrayList<>();

    public static Function<GameFormat, GameFormatDTO> mapper(final UriInfo uriInfo) {
        return (g) -> fromGameFormat(uriInfo, g);
    }

    public static GameFormatDTO fromGameFormat(final UriInfo uriInfo, final GameFormat gameFormat) {
        GameFormatDTO toReturn = new GameFormatDTO();

        toReturn.id = gameFormat.getId();
        toReturn.name = gameFormat.getName();
        toReturn.playersPerTeam = gameFormat.getPlayersPerTeam();

        toReturn.addLink("self", uriInfo.getAbsolutePathBuilder().path("games")
                .path(String.valueOf(gameFormat.getGameId()))
                .path("formats")
                .path(String.valueOf(gameFormat.getId()))
                .build().toString());

        toReturn.addLink("game", uriInfo.getAbsolutePathBuilder().path("games")
                .path(String.valueOf(gameFormat.getGameId()))
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

    public int getPlayersPerTeam() {
        return playersPerTeam;
    }

    public void setPlayersPerTeam(int playersPerTeam) {
        this.playersPerTeam = playersPerTeam;
    }

    public List<LinkDTO> getLinks() {
        return links;
    }

    public void setLinks(List<LinkDTO> links) {
        this.links = links;
    }
}