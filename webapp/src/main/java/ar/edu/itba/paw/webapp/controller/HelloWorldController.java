package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.GameService;
import ar.edu.itba.paw.interfaces.services.GreetingService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Game;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
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
import java.util.List;
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

    public HelloWorldController(final UserService us, final GameService gs, final TournamentService ts) {
        this.us = us;
        this.gs = gs;
        this.ts = ts;
    }

    @RequestMapping("/")
    public ModelAndView index(HttpServletRequest request, @ModelAttribute("loginForm") UserForm loginForm, @ModelAttribute("registerForm") UserForm registerForm) {
        final ModelAndView mav = new ModelAndView("index");
        User user = (User) request.getSession().getAttribute("user");
        mav.addObject("user", user);
        mav.addObject("loginForm", loginForm);
        mav.addObject("registerForm", registerForm);
        List<Game> games = gs.findAll();
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
        User newUser = us.create(username, username + "@email.com");
        return new ModelAndView("redirect:/?userId = " + newUser.getId());
    }

    @RequestMapping(value = "/tournament/create", method = {RequestMethod.GET})
    public ModelAndView createTournamentForm(@ModelAttribute("tournamentForm") final TournamentForm form){
        ModelAndView mav = new ModelAndView("createTournament");
        mav.addObject("regions", Region.values());
        mav.addObject("elos", Elo.values());
        mav.addObject("structures", Structure.values());
        mav.addObject("games", gs.findAll()
);
        return mav;
    }

    @RequestMapping(value = "/tournament/create", method = { RequestMethod.POST })
    public ModelAndView createTournament(@Valid @ModelAttribute("tournamentForm") final TournamentForm form) {

        Optional<Game> optionalGame = gs.findById(form.getGame_id());
        if (!optionalGame.isPresent()){
            return new ModelAndView("index");
        }
        final Tournament t = ts.create(form.getCreator_id(), form.getName(), optionalGame.get().getId(),
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
        return new ModelAndView("redirect:/game?gameId=" + g.getId());
    }

    @RequestMapping("/game")
    public ModelAndView gamePage(@RequestParam("gameId") final long gameId){
        final ModelAndView mav = new ModelAndView("gamePage");
        Optional<Game> optionalGame = gs.findById(gameId);
        if(optionalGame.isPresent()) {
            mav.addObject("game", optionalGame.get());
        } else {
            return new ModelAndView("index");
        }
        return mav;
    }

    @RequestMapping("/tournament")
    public ModelAndView tournamentPage(@RequestParam("tournamentId") final long tournamentId){
        final ModelAndView mav = new ModelAndView("tournamentPage");
        Optional<Tournament> optionalTournament = ts.findById(tournamentId);
        if(optionalTournament.isPresent()) {
            mav.addObject("tournament", optionalTournament.get());
        } else {
            return new ModelAndView("index");
        }
        return mav;
    }

}
