package ar.edu.itba.paw.webapp.dto.entities;

import ar.edu.itba.paw.model.Team;

import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TeamDTO {

    private long id;
    private String name;

    private List<LinkDTO> links = new ArrayList<>();

    public static Function<Team, TeamDTO> mapper(final UriInfo uriInfo) {
        return (t) -> fromTeam(uriInfo, t);
    }

    public static TeamDTO fromTeam(final UriInfo uriInfo, final Team team) {
        TeamDTO toReturn = new TeamDTO();

        toReturn.id = team.getId();
        toReturn.name = team.getName();

        toReturn.addLink("self", uriInfo.getAbsolutePathBuilder().path("teams")
                .path(String.valueOf(team.getId()))
                .build().toString());

        toReturn.addLink("profilePicture", uriInfo.getAbsolutePathBuilder().path("images")
                .path(team.getPfpId().toString())
                .build().toString());

        toReturn.addLink("banner", uriInfo.getAbsolutePathBuilder().path("images")
                .path(team.getBannerId().toString())
                .build().toString());

        toReturn.addLink("owner", uriInfo.getAbsolutePathBuilder().path("users")
                .path(String.valueOf(team.getOwner().getId()))
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

    public List<LinkDTO> getLinks() {
        return links;
    }

    public void setLinks(List<LinkDTO> links) {
        this.links = links;
    }
}