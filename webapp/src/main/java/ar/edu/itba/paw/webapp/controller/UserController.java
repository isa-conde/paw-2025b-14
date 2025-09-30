package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.GameService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.webapp.form.FilterForm;
import ar.edu.itba.paw.webapp.form.TournamentForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.*;

@Controller
public class UserController {

    private final GameService gs;
    private final UserService us;
    private final TournamentService ts;

    public UserController(GameService gs, UserService us, TournamentService ts) {
        this.gs = gs;
        this.us = us;
        this.ts = ts;
    }

    @RequestMapping("/")
    public ModelAndView index(@ModelAttribute("tournamentForm") TournamentForm tournamentForm, TournamentFilter tournamentFilter, Principal principal) {
        final ModelAndView mav = new ModelAndView("index");
        List<Game> allGames = gs.findAll();

        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }

        mav.addObject("user", user);
        mav.addObject("games", allGames);
        mav.addObject("regions", Arrays.stream(Region.values()).toList());
        mav.addObject("elos", Arrays.stream(Elo.values()).toList());
        mav.addObject("structures", Arrays.stream(Structure.values()).toList());
        mav.addObject("tournamentForm", tournamentForm);

        Map<Long, List<Tournament>> tournaments = ts.getHomeTournaments();
        mav.addObject("gameIds", tournaments.keySet());
        for (Long game_id : tournaments.keySet()) {
            mav.addObject("tournaments" + game_id, tournaments.get(game_id));
            mav.addObject("game" + game_id, gs.findById(game_id).get());
        }
        return mav;
    }

    @RequestMapping("/myTournaments")
    public ModelAndView myTournaments(Principal principal) {
        final ModelAndView mav = new ModelAndView("myTournaments");

        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }
        List<Tournament> allCreatedTournaments = ts.findByCreator(user.getId());

        List<Tournament> onGoingTournaments = allCreatedTournaments.stream()
                .filter(t -> !t.getFinished())
                .toList();

        List<Tournament> finishedTournaments = allCreatedTournaments.stream()
                .filter(t -> t.getFinished())
                .toList();
        List<Tournament> joinedTournaments = ts.findUserActiveTournaments(user.getId());
        List<Tournament> pastTournaments = ts.findUserPastTournaments(user.getId());

        mav.addObject("user", user);
        mav.addObject("pastTournaments", pastTournaments);
        mav.addObject("onGoingTournaments", onGoingTournaments);
        mav.addObject("finishedTournaments", finishedTournaments);
        mav.addObject("joinedTournaments", joinedTournaments);

        return mav;
    }

    @RequestMapping("/gamesPage")
    public ModelAndView gamesPage(Principal principal) {
        final ModelAndView mav = new ModelAndView("gamesPage");
        List<Game> allGames = gs.findAll();
        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }
        mav.addObject("user", user);
        mav.addObject("games", allGames);

        return mav;
    }

    @RequestMapping(value = "/tournamentsPage", method = RequestMethod.GET)
    public ModelAndView tournamentsPage(Principal principal, @ModelAttribute("filterForm") FilterForm filterForm, TournamentFilter tf) {
        final ModelAndView mav = new ModelAndView("tournamentsPage");
        List<Game> allGames = gs.findAll();
        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }

        mav.addObject("user", user);
        mav.addObject("games", allGames);
        mav.addObject("structures", Arrays.stream(Structure.values()).toList());
        mav.addObject("regions", Arrays.stream(Region.values()).toList());
        mav.addObject("elos", Arrays.stream(Elo.values()).toList());
        mav.addObject("genres", Arrays.stream(Genre.values()).toList());
        mav.addObject("teamSizes", List.of(1,2,3,4,5));

        tf.setGame_id(filterForm.getGame_id());
        tf.setRegion(filterForm.getRegion());
        tf.setElo(filterForm.getElo());

        boolean isFiltered = !tf.isEmpty();

        if(isFiltered) {
            mav.addObject("tournaments", ts.findTournaments(tf));
        }else{
            Map<Long, List<Tournament>> gameTournaments = new HashMap<>();
            for (Game game : allGames) {
                tf.setGame_id(game.getId());
                List<Tournament> gameTours = ts.findTournaments(tf);
                if (!gameTours.isEmpty()) {
                    gameTournaments.put(game.getId(), gameTours);
                }
            }
            mav.addObject("gameTournaments", gameTournaments);
            tf.setGame_id(null);
        }

        mav.addObject("isFiltered", isFiltered);
        return mav;
    }

    @RequestMapping("/search")
    public ModelAndView search(@RequestParam("q") final String q, Principal principal){
        final ModelAndView mav = new ModelAndView("searchResults");
        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }

        mav.addObject("user", user);
        mav.addObject("games", gs.searchByName(q));
        mav.addObject("tournaments", ts.searchByName(q));

        return mav;
    }

}
