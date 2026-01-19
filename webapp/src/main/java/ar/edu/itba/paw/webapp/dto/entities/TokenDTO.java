package ar.edu.itba.paw.webapp.dto.entities;

import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDate;
import java.util.function.Function;

public class TokenDTO {

    private long token;
    private LocalDate expiryDate;

    private URI assignedTo;

    public static Function<Token, TokenDTO> mapper(UriInfo uriInfo) {
        return (t) -> fromToken(uriInfo, t);
    }

    public static TokenDTO fromToken(final UriInfo uriInfo, final Token token) {
        final TokenDTO toReturn = new TokenDTO();

        toReturn.token = token.getToken();
        toReturn.expiryDate = token.getExpiryDate();

        toReturn.assignedTo = uriInfo.getAbsolutePathBuilder().path("users")
                .path("/" + token.getUserId()).build();

        return toReturn;
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
