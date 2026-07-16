package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.User;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
public class CurrentUserProvider {

    public Optional<UserDetails> getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return Optional.empty();
        }

        return Optional.of((UserDetails) principal);
    }

    public User getCurrentUser() {
        return getCurrentUserDetails()
                .map(UserDetails::getUser)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Authentication is required"));
    }

    public long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    public Collection<? extends GrantedAuthority> getCurrentAuthorities() {
        return getCurrentUserDetails()
                .map(UserDetails::getAuthorities)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Authentication is required"));
    }
}
