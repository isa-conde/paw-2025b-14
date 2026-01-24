package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;

@Component
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserService us;

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String s) throws UserNotFoundException {
        final User user = us.findByUsername(s).orElseThrow(UserNotFoundException::new);

        Collection<SimpleGrantedAuthority> authorities = new HashSet<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if(user.isVerified()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_VERIFIED"));
        }

        return new UserDetails(user, authorities);
    }
}
