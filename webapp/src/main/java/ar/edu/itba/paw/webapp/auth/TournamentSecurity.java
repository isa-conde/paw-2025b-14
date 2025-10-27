package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;

@Component("tournamentSecurity")
public class TournamentSecurity {

    private final static Logger LOGGER = LoggerFactory.getLogger(TournamentSecurity.class);

    private final UserService userService;
    private final TournamentService tournamentService;

    public TournamentSecurity(UserService userService, TournamentService tournamentService) {
        this.userService = userService;
        this.tournamentService = tournamentService;
    }

    public boolean isCreator(Authentication authentication, HttpServletRequest request) {
        String tidParam = request.getParameter("tournamentId");
        if (tidParam == null) return false;

        long tid;
        try {
            tid = Long.parseLong(tidParam);
        } catch (NumberFormatException e) {
            LOGGER.error("The format of ID {} is invalid", tidParam);
            throw e;
        }

        Optional<User> u = userService.findByUsername(authentication.getName());
        Optional<Tournament> t = tournamentService.findById(tid);

        return u.isPresent() && t.isPresent() && Objects.equals(t.get().getCreator_id(), u.get().getId());
    }
}
