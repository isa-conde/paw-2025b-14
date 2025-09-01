package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.GameService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Game;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.webapp.form.FilterForm;
import ar.edu.itba.paw.webapp.form.GameForm;
import ar.edu.itba.paw.webapp.form.TournamentForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Controller
public class HelloWorldController {

    private final UserService us;
    private final GameService gs;
    private final TournamentService ts;

    @ModelAttribute("loginForm")
    public UserForm loginForm() { return new UserForm(); }

    @ModelAttribute("registerForm")
    public UserForm registerForm() { return new UserForm(); }

    @ModelAttribute("tournamentForm")
    public TournamentForm tournamentForm() {
        return new TournamentForm();
    }

    @ModelAttribute("filterForm")
    public FilterForm filterForm() {
        return new FilterForm();
    }

    public HelloWorldController(final UserService us, final GameService gs, final TournamentService ts) {
        this.us = us;
        this.gs = gs;
        this.ts = ts;
    }

    @RequestMapping("/")
    public ModelAndView index(HttpServletRequest request, @ModelAttribute("loginForm") UserForm loginForm, @ModelAttribute("registerForm") UserForm registerForm, @ModelAttribute("tournamentForm") TournamentForm tournamentForm) {
        final ModelAndView mav = new ModelAndView("index");
        User user = (User) request.getSession().getAttribute("user");
        List<Game> allGames = gs.findAll();
        mav.addObject("user", user);
        mav.addObject("games", allGames);
        mav.addObject("regions", Arrays.stream(Region.values()).toList());
        mav.addObject("elos", Arrays.stream(Elo.values()).toList());
        mav.addObject("structures", Arrays.stream(Structure.values()).toList());
        mav.addObject("loginForm", loginForm);
        mav.addObject("registerForm", registerForm);
        mav.addObject("tournamentForm", tournamentForm);

        List<Tournament> tournamentsGame1 = ts.findGameTournaments(allGames.get(0).getId());
        List<Tournament> tournamentsGame2 = ts.findGameTournaments(allGames.get(1).getId());

        mav.addObject("tournamentsGame1", tournamentsGame1);
        mav.addObject("tournamentsGame2", tournamentsGame2);
        mav.addObject("game1", allGames.get(0));
        mav.addObject("game2", allGames.get(1));

        return mav;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid @ModelAttribute("registerForm") UserForm form, HttpServletRequest request) {
        User user = us.create(form.getUsername(), form.getEmail());
        request.getSession().setAttribute("user", user);
        return new ModelAndView("redirect:/?userId=" + user.getId());
    }

    @PostMapping("/login")
    public ModelAndView login(@Valid @ModelAttribute("loginForm") UserForm form, HttpServletRequest request) {
        Optional<User> user = us.authenticate(form.getUsername(), form.getEmail());
        if (user.isPresent()) {
            request.getSession().setAttribute("user", user.get());
            return new ModelAndView("redirect:/?userId=" + user.get().getId());
        } else {
            ModelAndView mav = new ModelAndView("index");
            mav.addObject("loginError", "Invalid credentials");
            return mav;
        }
    }

    @RequestMapping("/logout")
    public ModelAndView logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/create")
    public ModelAndView profile(@RequestParam("username") final String username) {
        User newUser = us.create(username, username + "@email.com");        return new ModelAndView("redirect:/?userId = " + newUser.getId());
    }

    @RequestMapping(value = "/tournament/create", method = { RequestMethod.POST })
    public ModelAndView createTournament(HttpServletRequest request, @Valid @ModelAttribute("tournamentForm") final TournamentForm form) {
        User user = (User) request.getSession().getAttribute("user");
        Optional<Game> optionalGame = gs.findById(form.getGame_id());
        if (optionalGame.isEmpty()){
            return new ModelAndView("gamePage");
        }
        final Tournament t = ts.create(user.getId(), form.getName(), optionalGame.get().getId(),
                form.getRegion(), form.getElo(), form.getStart_date(), form.getEnd_date(),
                form.getFormat(), form.getStructure(), form.getMax_participants());
        return new ModelAndView("redirect:/tournament?tournamentId=" + t.getId());
    }

    @RequestMapping(value = "/game/create", method = {RequestMethod.GET})
    public ModelAndView createGameForm(@ModelAttribute("gameForm") final GameForm form){
        ModelAndView mav = new ModelAndView("addGame");
        mav.addObject("genres", Genre.values()); // 🔹 paso el enum a la vista
        return mav;
    }

    @RequestMapping(value = "/game/create", method = { RequestMethod.POST })
    public ModelAndView createGame(@Valid @ModelAttribute("gameForm") final GameForm form) {

        final Game g = gs.create(form.getName(), form.getGenre());
        return new ModelAndView("redirect:/game?game_id=" + g.getId());
    }

    @RequestMapping("/game")
    public ModelAndView gamePage(@RequestParam("game_id") final long game_id){
        final ModelAndView mav = new ModelAndView("gamePage");
        Optional<Game> optionalGame = gs.findById(game_id);
        if(optionalGame.isPresent()) {
            mav.addObject("game", optionalGame.get());
        } else {
            return new ModelAndView("index");
        }
        return mav;
    }

    @RequestMapping("/tournament")
    public ModelAndView tournamentPage(@RequestParam("tournamentId") final long tournamentId) {
        final ModelAndView mav = new ModelAndView("tournament");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM-dd-yyyy", Locale.ENGLISH);
        Optional<Tournament> optionalTournament = ts.findById(tournamentId);
        if(optionalTournament.isPresent()) {
            Tournament tournament = optionalTournament.get();
            Optional<Game> optionalGame = gs.findById(tournament.getGame_id());
            Optional<User> optionalUser = us.findById(tournament.getCreator_id());
            mav.addObject("tournament", tournament);
            mav.addObject("game", optionalGame.get());
            mav.addObject("creator", optionalUser.get());
            mav.addObject("formatter", formatter);
        } else {
            return new ModelAndView("index");
        }
        return mav;
    }

    @RequestMapping(value = "/tournaments-page", method =  { RequestMethod.GET })
    public ModelAndView tournamentsPage(@RequestParam(name = "userId", required = false, defaultValue = "1") final int userId, @ModelAttribute("filterForm") FilterForm filterForm) {
        // Initialize filterForm if it's null
        if (filterForm == null) {
            filterForm = new FilterForm();
        }
        final ModelAndView mav = new ModelAndView("tournaments-page");
        List<Game> allGames = gs.findAll();

        mav.addObject("user", us.findById(userId).isPresent() ? us.findById(userId).get() : null);
        mav.addObject("games", allGames);

        List<Region> regionsWithEmpty = new ArrayList<>();
        regionsWithEmpty.add(null);
        regionsWithEmpty.addAll(Arrays.stream(Region.values()).toList());
        mav.addObject("regions", regionsWithEmpty);

        List<Elo> elosWithEmpty = new ArrayList<>();
        elosWithEmpty.add(null);
        elosWithEmpty.addAll(Arrays.stream(Elo.values()).toList());
        mav.addObject("elos", elosWithEmpty);

        mav.addObject("structures", Arrays.stream(Structure.values()).toList());

        TournamentFilter tf = new TournamentFilter();
        tf.setGame_id(filterForm.getGame_id());
        tf.setRegion(filterForm.getRegion());
        tf.setElo(filterForm.getElo());

        mav.addObject("tournaments", ts.findTournaments(tf));

        List<Tournament> tournamentsGame1 = ts.findGameTournaments(allGames.get(0).getId());
        List<Tournament> tournamentsGame2 = ts.findGameTournaments(allGames.get(1).getId());

        mav.addObject("tournamentsGame1", tournamentsGame1);
        mav.addObject("tournamentsGame2", tournamentsGame2);
        mav.addObject("game1", allGames.get(0));
        mav.addObject("game2", allGames.get(1));

        mav.addObject("isFiltered", !tf.isEmpty());

        return mav;
    }

    @RequestMapping(value = "/tournaments-page", method =  { RequestMethod.POST })
    public ModelAndView filter(@Valid @ModelAttribute("filterForm") final FilterForm form, @RequestParam(name = "userId", required = false, defaultValue = "1") final int userId) {
        final ModelAndView mav = new ModelAndView("tournaments-page");
        List<Game> allGames = gs.findAll();

        mav.addObject("user", us.findById(userId).isPresent() ? us.findById(userId).get() : null);
        mav.addObject("games", allGames);

        List<Region> regionsWithEmpty = new ArrayList<>();
        regionsWithEmpty.add(null);
        regionsWithEmpty.addAll(Arrays.stream(Region.values()).toList());
        mav.addObject("regions", regionsWithEmpty);

        List<Elo> elosWithEmpty = new ArrayList<>();
        elosWithEmpty.add(null);
        elosWithEmpty.addAll(Arrays.stream(Elo.values()).toList());
        mav.addObject("elos", elosWithEmpty);

        mav.addObject("structures", Arrays.stream(Structure.values()).toList());
        mav.addObject("filterForm", form);

        TournamentFilter tf = new TournamentFilter();
        tf.setGame_id(form.getGame_id());
        tf.setRegion(form.getRegion());
        tf.setElo(form.getElo());

        mav.addObject("tournaments", ts.findTournaments(tf));

        List<Tournament> tournamentsGame1 = ts.findGameTournaments(allGames.get(0).getId());
        List<Tournament> tournamentsGame2 = ts.findGameTournaments(allGames.get(1).getId());

        mav.addObject("tournamentsGame1", tournamentsGame1);
        mav.addObject("tournamentsGame2", tournamentsGame2);
        mav.addObject("game1", allGames.get(0));
        mav.addObject("game2", allGames.get(1));

        mav.addObject("isFiltered", !tf.isEmpty());

        return mav;
    }

}