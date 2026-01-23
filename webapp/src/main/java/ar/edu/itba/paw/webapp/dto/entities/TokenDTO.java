package ar.edu.itba.paw.webapp.dto.entities;

import ar.edu.itba.paw.model.Token;

import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TokenDTO {

    private long token;
    private LocalDate expiryDate;

    private List<LinkDTO> links = new ArrayList<>();

    public static Function<Token, TokenDTO> mapper(final UriInfo uriInfo) {
        return (t) -> fromToken(uriInfo, t);
    }

    public static TokenDTO fromToken(final UriInfo uriInfo, final Token token) {
        final TokenDTO toReturn = new TokenDTO();

        toReturn.token = token.getToken();
        toReturn.expiryDate = token.getExpiryDate();

        toReturn.addLink("self", uriInfo.getAbsolutePathBuilder().path("tokens")
                .path(String.valueOf(token.getToken())).build().toString());
        toReturn.addLink("assignedTo", uriInfo.getAbsolutePathBuilder().path("users")
                .path(String.valueOf(token.getUserId())).build().toString());

        return toReturn;
    }

    private void addLink(String rel, String href) {
        links.add(new LinkDTO(rel, href));
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

    public List<LinkDTO> getLinks() {
        return links;
    }

    public void setLinks(List<LinkDTO> links) {
        this.links = links;
    }
}