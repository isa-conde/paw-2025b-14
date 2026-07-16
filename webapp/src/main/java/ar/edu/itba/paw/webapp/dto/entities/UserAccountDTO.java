package ar.edu.itba.paw.webapp.dto.entities;

import ar.edu.itba.paw.model.UserAccount;

import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class UserAccountDTO {

    private String platform;
    private String username;

    private List<LinkDTO> links = new ArrayList<>();

    public static Function<UserAccount, UserAccountDTO> mapper(final UriInfo uriInfo) {
        return (account) -> fromUserAccount(uriInfo, account);
    }

    public static UserAccountDTO fromUserAccount(final UriInfo uriInfo, final UserAccount account) {
        UserAccountDTO toReturn = new UserAccountDTO();

        toReturn.platform = account.getPlatform().name();
        toReturn.username = account.getUsername();

        if (account.getUser() != null) {
            toReturn.addLink("user", uriInfo.getBaseUriBuilder().path("users")
                    .path(String.valueOf(account.getUser().getId())).build().toString());
            toReturn.addLink("self", uriInfo.getBaseUriBuilder().path("users")
                    .path(String.valueOf(account.getUser().getId()))
                    .path("accounts")
                    .path(account.getPlatform().name()).build().toString());
        }

        return toReturn;
    }

    private void addLink(String rel, String href) {
        links.add(new LinkDTO(rel, href));
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<LinkDTO> getLinks() {
        return links;
    }

    public void setLinks(List<LinkDTO> links) {
        this.links = links;
    }
}
