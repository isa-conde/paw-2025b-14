package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.auth.PawUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class CurrentUserControllerAdvice {

    @ModelAttribute("user")
    public Optional<PawUserDetails> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
            return Optional.empty();
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof PawUserDetails) {
            return Optional.of((PawUserDetails) principal);
        }

        return Optional.empty();
    }
}
