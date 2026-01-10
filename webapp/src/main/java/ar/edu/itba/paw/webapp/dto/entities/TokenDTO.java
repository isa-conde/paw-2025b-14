package ar.edu.itba.paw.webapp.dto.entities;

import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;

import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.util.function.Function;

public class TokenDTO {

    private User user;
    private long token;
    private LocalDate expiryDate;

    public static Function<Token, TokenDTO> mapper(UriInfo uriInfo) {
        return (t) -> fromToken(uriInfo, t);
    }

    public static TokenDTO fromToken(final UriInfo uriInfo, final Token token) {
        final TokenDTO toReturn = new TokenDTO();

        toReturn.user = token.getUser();
        toReturn.token = token.getToken();
        toReturn.expiryDate = token.getExpiryDate();

        return toReturn;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getToken() {
        return token;
    }

    public void setToken(long token) {
        this.token = token;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}
