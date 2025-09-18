package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class PawUserDetails extends org.springframework.security.core.userdetails.User {



    private User pawUser;

    public PawUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getPassword(), authorities);
        this.pawUser = user;
    }

    public User getPawUser() {
        return pawUser;
    }

}
