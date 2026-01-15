package ar.edu.itba.paw.webapp.dto.entities;

import ar.edu.itba.paw.model.User;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.function.Function;

public class UserDTO {

    private long id;
    private String username;
    private String email;
    private boolean verified;
    private String bio;
    private Float rating;

    // WRITE ONLY
    private String password;

    private URI self;
    private URI createdTournaments;
    private URI favoriteGames;
    private URI tournamentsParticipatedIn;
    private URI commentsReceived;
    private URI matchesParticipatedIn;
    private URI teamsCreated;
    private URI partOfTeam;
    private URI banner;
    private URI profilePicture;
    private URI assignedTokens;

    public static Function<User, UserDTO> mapper(final UriInfo uriInfo) {
        return (u) -> fromUser(uriInfo, u);
    }

    public static UserDTO fromUser(final UriInfo uriInfo, final User user) {
        final UserDTO toReturn = new UserDTO();

        toReturn.id = user.getId();
        toReturn.username = user.getUsername();
        toReturn.email = user.getEmail();
        toReturn.verified = user.isVerified();
        toReturn.bio = user.getBio();
        toReturn.rating = user.getRating();

        toReturn.self = uriInfo.getAbsolutePathBuilder().path("users")
                .path(String.valueOf(user.getId())).build();
        toReturn.createdTournaments = uriInfo.getAbsolutePathBuilder().path("tournaments")
                .queryParam("createdBy", user.getId()).build();
        toReturn.favoriteGames = uriInfo.getAbsolutePathBuilder().path("games")
                .queryParam("favoritedBy", user.getId()).build();
        toReturn.tournamentsParticipatedIn = uriInfo.getAbsolutePathBuilder().path("tournaments")
                .queryParam("hasUser", user.getId()).build();
        toReturn.commentsReceived = uriInfo.getAbsolutePathBuilder().path("comments")
                .queryParam("receivedBy", user.getId()).build();
        toReturn.matchesParticipatedIn = uriInfo.getAbsolutePathBuilder().path("matches")
                .queryParam("hasUser", user.getId()).build();
        toReturn.matchesParticipatedIn = uriInfo.getAbsolutePathBuilder().path("teams")
                .queryParam("createdBy", user.getId()).build();
        toReturn.partOfTeam = uriInfo.getAbsolutePathBuilder().path("teams")
                .queryParam("hasUser", user.getId()).build();
        toReturn.profilePicture = uriInfo.getAbsolutePathBuilder().path("images")
                .path(String.valueOf(user.getPfpId())).build();
        toReturn.banner = uriInfo.getAbsolutePathBuilder().path("images")
                .path(String.valueOf(user.getBannerId())).build();
        toReturn.assignedTokens = uriInfo.getAbsolutePathBuilder().path("tokens")
                .queryParam("assignedTo", user.getId()).build();

        return toReturn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
