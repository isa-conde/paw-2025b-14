package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.User;

import javax.ws.rs.core.UriInfo;
import java.net.URL;

public class UserDTO {

    private String username;
    private String email;
    private boolean verified;
    private String bio;
    private Float rating;
    private String locale;

    public static UserDTO fromUser(final UriInfo uriInfo, final User user) {
        final UserDTO dto = new UserDTO();

        dto.username = user.getUsername();
        dto.email = user.getEmail();
        dto.verified = user.isVerified();
        dto.bio = user.getBio();
        dto.rating = user.getRating();
        dto.locale = user.getLocale();

        return dto;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
