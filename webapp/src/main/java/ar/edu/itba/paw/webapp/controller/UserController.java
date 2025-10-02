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
import ar.edu.itba.paw.webapp.auth.PawUserDetails;
import ar.edu.itba.paw.webapp.form.FilterForm;
import ar.edu.itba.paw.webapp.form.TournamentForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
    public ModelAndView index(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @ModelAttribute("tournamentForm") TournamentForm tournamentForm, TournamentFilter tournamentFilter, Principal principal) {
        final ModelAndView mav = new ModelAndView("index");
        List<Game> allGames = gs.findAll();

        mav.addObject("user", currentUser.orElse(null));
        mav.addObject("games", allGames);
        mav.addObject("regions", Arrays.stream(Region.values()).toList());
        mav.addObject("elos", Arrays.stream(Elo.values()).toList());
        mav.addObject("structures", Arrays.stream(Structure.values()).toList());
        mav.addObject("tournamentForm", tournamentForm);

        Map<Game, List<Tournament>> tournaments = ts.getUnfilteredTournamentPages(0L);
        mav.addObject("gameIds", tournaments.keySet());
        for (Game game: tournaments.keySet()) {
            mav.addObject("tournaments" + game.getId(), tournaments.get(game));
            mav.addObject("game" + game.getId(), game);
        }
        return mav;
    }

    @RequestMapping("/myTournaments")
    public ModelAndView myTournaments(@ModelAttribute("user") Optional<PawUserDetails> currentUser) {
        final ModelAndView mav = new ModelAndView("myTournaments");

        User user = currentUser.orElse(null).getPawUser();

        List<Tournament> onGoingTournaments = ts.getCreatedAndOngoingTournaments(user.getId());
        List<Tournament> finishedTournaments = ts.getCreatedAndFinishedTournaments(user.getId());
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
    public ModelAndView gamesPage(@ModelAttribute("user") Optional<PawUserDetails> currentUser) {
        final ModelAndView mav = new ModelAndView("gamesPage");
        List<Game> allGames = gs.findAll();

        mav.addObject("user", currentUser.orElse(null));
        mav.addObject("games", allGames);

        return mav;
    }

    @RequestMapping(value = "/tournamentsPage", method = RequestMethod.GET)
    public ModelAndView tournamentsPage(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @ModelAttribute("filterForm") FilterForm filterForm, TournamentFilter tf,  @RequestParam(defaultValue = "0") Long page) {
        final ModelAndView mav = new ModelAndView("tournamentsPage");
        List<Game> allGames = gs.findAll();

        mav.addObject("user", currentUser.orElse(null));
        mav.addObject("games", allGames);
        mav.addObject("structures", Arrays.stream(Structure.values()).toList());
        mav.addObject("regions", Arrays.stream(Region.values()).toList());
        mav.addObject("elos", Arrays.stream(Elo.values()).toList());
        mav.addObject("genres", Arrays.stream(Genre.values()).toList());
        mav.addObject("teamSizes", List.of(1,2,3,4,5));

        tf.setGame_id(filterForm.getGame_id());
        tf.setRegion(filterForm.getRegion());
        tf.setElo(filterForm.getElo());
        tf.setPlayersPerTeam(filterForm.getPlayersPerTeam());
        tf.setGenre(filterForm.getGenre());

        boolean isFiltered = !tf.isEmpty();

        if(isFiltered) {
            mav.addObject("tournaments", ts.findTournaments(tf, page));
            mav.addObject("totalPages", ts.getPageAmount(9, tf));
        }else{
            Map<Game, List<Tournament>> gameTournaments = ts.getUnfilteredTournamentPages(page);
            mav.addObject("gameTournaments", gameTournaments);
            mav.addObject("totalPages", ts.getPageAmount(3, tf));
        }

        mav.addObject("isFiltered", isFiltered);
        return mav;
    }

    @RequestMapping("/search")
    public ModelAndView search(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @RequestParam("q") final String q){
        final ModelAndView mav = new ModelAndView("searchResults");

        mav.addObject("user", currentUser.orElse(null));
        mav.addObject("games", gs.searchByName(q));
        mav.addObject("tournaments", ts.searchByName(q));

        return mav;
    }

    @RequestMapping("/profile/{id}")
    public ModelAndView profile(@ModelAttribute("user") Optional<PawUserDetails> currentUser, Principal principal, @PathVariable Long id){
        final ModelAndView mav = new ModelAndView("profile");

        Optional<User> profileOpt = us.findById(id);
        if (profileOpt.isEmpty()){
            // TODO: REDIRIGIR A 404
            return index(currentUser, new TournamentForm(), new TournamentFilter(), principal);
        }

        mav.addObject("user", currentUser.orElse(null));
        mav.addObject("profile", profileOpt.get());
        mav.addObject("favouriteGames", gs.getFavourites(id));
        mav.addObject("lastTournaments", ts.findUserActiveTournaments(id));

        return mav;
    }

}
