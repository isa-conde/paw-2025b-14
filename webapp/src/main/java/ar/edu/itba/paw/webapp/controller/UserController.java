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
import ar.edu.itba.paw.webapp.form.EditProfileForm;
import ar.edu.itba.paw.webapp.form.EditTournamentForm;
import ar.edu.itba.paw.webapp.form.FilterForm;
import ar.edu.itba.paw.webapp.form.TournamentForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
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
        List<Game> allGames = gs.findAllPaged(0L);

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

        Map<Long, List<Tournament>> tournaments = ts.getUnfilteredTournamentPages(0L);
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
    public ModelAndView gamesPage(Principal principal, @RequestParam(defaultValue = "0") Long page) {
        final ModelAndView mav = new ModelAndView("gamesPage");
        List<Game> allGames = gs.findAllPaged(page);
        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }
        mav.addObject("user", user);
        mav.addObject("games", allGames);
        mav.addObject("totalPages", gs.getPageAmount());
        mav.addObject("currentPage", page);

        return mav;
    }

    @RequestMapping(value = "/tournamentsPage", method = RequestMethod.GET)
        public ModelAndView tournamentsPage(Principal principal, @ModelAttribute("filterForm") FilterForm filterForm, TournamentFilter tf,  @RequestParam(defaultValue = "0") Long page) {
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
        mav.addObject("currentPage", page);

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
            Map<Long, List<Tournament>> gameTournaments = ts.getUnfilteredTournamentPages(page);
            mav.addObject("gameTournaments", gameTournaments);
            mav.addObject("totalPages", ts.getPageAmount(3, tf));
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

    @RequestMapping("/profile/{id}")
    public ModelAndView profile(Principal principal, @PathVariable Long id, @ModelAttribute("EditProfileForm") EditProfileForm editProfileForm){
        final ModelAndView mav = new ModelAndView("profile");

        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }
        mav.addObject("user", user);

        Optional<User> profileOpt = us.findById(id);
        if (profileOpt.isEmpty()){
            //TODO REDIRIGIR A 404
            return index(new TournamentForm(), new TournamentFilter(), principal);
        }
        User profile = profileOpt.get();
        mav.addObject("isMyProfile", profile.getId() == user.getId());
        mav.addObject("profile", profile);
        mav.addObject("favouriteGames", gs.getFavourites(profile.getId()));
        mav.addObject("lastTournaments", ts.findUserActiveTournaments(profile.getId()));
        mav.addObject("EditProfileForm", editProfileForm);

        editProfileForm.setUsername(profile.getUsername());
        editProfileForm.setBio(profile.getBio());

        return mav;
    }

    @RequestMapping(value = "/profile/update", method = { RequestMethod.POST })
    public ModelAndView updateProfile(Principal principal, @RequestParam("userId") final long userId, @Valid @ModelAttribute("editProfileForm") final EditProfileForm form, final BindingResult result){
        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }
        if (user == null) {
            return new ModelAndView("redirect:/");
        }

        if (result.hasErrors()) {
            ModelAndView mav = profile(principal, userId, form);
            mav.addObject("openModal", "'editProfileModal'");
            return mav;
        }

        Boolean isValid = true;

        byte[] pfpBytes = null;
        try {
            if (form.getProfilePicture() != null && !form.getProfilePicture().isEmpty()) {
                pfpBytes = form.getProfilePicture().getBytes();
            }
        } catch (IOException e) {
            isValid = false;
            result.rejectValue("profilePicture", "error.tournamentForm.invalidImage");
        }
        byte[] bannerBytes = null;
        try {
            if (form.getBannerPicture() != null && !form.getBannerPicture().isEmpty()) {
                bannerBytes = form.getBannerPicture().getBytes();
            }
        } catch (IOException e) {
            isValid = false;
            result.rejectValue("bannerPicture", "error.tournamentForm.invalidImage");
        }
        if (isValid){
            us.updateProfileInfo(userId, form.getUsername(), form.getBio(), pfpBytes, bannerBytes);
        }
        return new ModelAndView("redirect:/profile/" + userId );
    }
}
