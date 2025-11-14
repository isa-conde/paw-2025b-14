package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;

@Component
public class PawUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService us;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        final User user = us.findByUsername(s)
                .orElseThrow(() -> new UsernameNotFoundException("No user by name " + s + " found"));

        Collection<SimpleGrantedAuthority> authorities = new HashSet<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if(user.isVerified()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_VERIFIED"));
        }

        return new PawUserDetails(user, authorities);
    }
}
