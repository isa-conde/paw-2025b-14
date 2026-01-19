package ar.edu.itba.paw.webapp.dto.entities;

import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.function.Function;

public class TeamDTO {

    private long id;
    private String name;

    private URI profilePicture;
    private URI banner;
    private URI owner;

    public static Function<Team, TeamDTO> mapper(final UriInfo uriInfo) {
        return (t) -> fromTeam(uriInfo, t);
    }

    public static TeamDTO fromTeam(final UriInfo uriInfo, final Team team) {
        TeamDTO toReturn = new TeamDTO();

        toReturn.setId(team.getId());
        toReturn.setName(team.getName());

        toReturn.setProfilePicture(uriInfo.getAbsolutePathBuilder().path("images")
                .path(team.getPfpId().toString()).build());
        toReturn.setBanner(uriInfo.getAbsolutePathBuilder().path("images")
                .path(team.getBannerId().toString()).build());
        toReturn.setOwner(uriInfo.getAbsolutePathBuilder().path("users")
                .path(team.getOwner().getId().toString()).build());

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

    public URI getBanner() {
        return banner;
    }

    public void setBanner(URI banner) {
        this.banner = banner;
    }

    public URI getOwner() {
        return owner;
    }

    public void setOwner(URI owner) {
        this.owner = owner;
    }

    public URI getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(URI profilePicture) {
        this.profilePicture = profilePicture;
    }
}
