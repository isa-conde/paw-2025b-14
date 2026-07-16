package ar.edu.itba.paw.webapp.dto.entities;

import ar.edu.itba.paw.model.User;

import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class UserDTO {

    private long id;
    private String username;
    private boolean verified;
    private String bio;
    private Float rating;

    private List<LinkDTO> links = new ArrayList<>();

    public static Function<User, UserDTO> mapper(final UriInfo uriInfo) {
        return (u) -> fromUser(uriInfo, u);
    }

    public static UserDTO fromUser(final UriInfo uriInfo, final User user) {
        final UserDTO toReturn = new UserDTO();

        toReturn.id = user.getId();
        toReturn.username = user.getUsername();
        toReturn.verified = user.isVerified();
        toReturn.bio = user.getBio();
        toReturn.rating = user.getRating();

        toReturn.addLink("self", uriInfo.getBaseUriBuilder().path("users")
                .path(String.valueOf(user.getId())).build().toString());
        toReturn.addLink("favoriteGames", uriInfo.getBaseUriBuilder().path("games")
                .queryParam("favouritedBy", user.getId()).build().toString());
        toReturn.addLink("commentsReceived", uriInfo.getBaseUriBuilder().path("users")
                .path(String.valueOf(user.getId()))
                .path("comments").build().toString());
        toReturn.addLink("partOfTeam", uriInfo.getBaseUriBuilder().path("teams")
                .queryParam("userId", user.getId()).build().toString());
        if (user.getPfpId() != null) {
            toReturn.addLink("profilePicture", uriInfo.getBaseUriBuilder().path("images")
                    .path(String.valueOf(user.getPfpId())).build().toString());
        }
        if (user.getBannerId() != null) {
            toReturn.addLink("banner", uriInfo.getBaseUriBuilder().path("images")
                    .path(String.valueOf(user.getBannerId())).build().toString());
        }
        toReturn.addLink("userAccounts", uriInfo.getBaseUriBuilder().path("users")
                .path(String.valueOf(user.getId()))
                .path("accounts").build().toString());

        return toReturn;
    }

    private void addLink(String rel, String href) {
        links.add(new LinkDTO(rel, href));
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<LinkDTO> getLinks() {
        return links;
    }

    public void setLinks(List<LinkDTO> links) {
        this.links = links;
    }
}
