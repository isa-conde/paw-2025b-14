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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
public class HelloWorldController {

    private final UserService us;
    private final GameService gs;
    private final TournamentService ts;

    public HelloWorldController(final UserService us, final GameService gs, final TournamentService ts) {
        this.us = us;
        this.gs = gs;
        this.ts = ts;
    }

    @RequestMapping("/")
    public ModelAndView helloWorld(@RequestParam(name = "userId", required = false, defaultValue = "1") final int userId, @ModelAttribute("tournamentForm") final TournamentForm form) {
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("user", us.findById(userId).isPresent() ? us.findById(userId).get() : null);
        mav.addObject("games", gs.findAll());
        mav.addObject("regions", Arrays.stream(Region.values()).toList());
        mav.addObject("elos", Arrays.stream(Elo.values()).toList());
        mav.addObject("structures", Arrays.stream(Structure.values()).toList());
        return mav;
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
        mav.addObject("games", gs.findAll());

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

}
