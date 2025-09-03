package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.GameService;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.MatchWithPlayers;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.webapp.form.FilterForm;
import ar.edu.itba.paw.webapp.form.GameForm;
import ar.edu.itba.paw.webapp.form.SetWinnerForm;
import ar.edu.itba.paw.webapp.form.TournamentForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class HelloWorldController {

    private final UserService us;
    private final GameService gs;
    private final TournamentService ts;
    private final MailService ms;

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

    public HelloWorldController(final UserService us, final GameService gs, final TournamentService ts, final MailService ms) {
        this.us = us;
        this.gs = gs;
        this.ts = ts;
        this.ms = ms;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid @ModelAttribute("registerForm") UserForm form, HttpServletRequest request, final BindingResult result) {
        if (result.hasErrors()) {
            ModelAndView mav = index(request, loginForm(), form, tournamentForm(), new TournamentFilter());
            mav.addObject("openModal", "'createTournamentModal'");
            return mav;
        }


        User user = us.create(form.getUsername(), form.getEmail());
        request.getSession().setAttribute("user", user);
        return new ModelAndView("redirect:/?userId=" + user.getId());
    }

    @RequestMapping("/")
    public ModelAndView index(HttpServletRequest request, @ModelAttribute("loginForm") UserForm loginForm, @ModelAttribute("registerForm") UserForm registerForm,@ModelAttribute("tournamentForm") TournamentForm tournamentForm, TournamentFilter tournamentFilter) {
        final ModelAndView mav = new ModelAndView("index");
        User user = (User) request.getSession().getAttribute("user");
        List<GameImg> allGames = gs.findAllWithImg();

        mav.addObject("user", user);
        mav.addObject("games", allGames);
        mav.addObject("regions", Arrays.stream(Region.values()).toList());
        mav.addObject("elos", Arrays.stream(Elo.values()).toList());
        mav.addObject("structures", Arrays.stream(Structure.values()).filter(s -> s == Structure.LEAGUE).toList());
        mav.addObject("loginForm", loginForm);
        mav.addObject("registerForm", registerForm);
        mav.addObject("tournamentForm", tournamentForm);

        List<GameImg> topGamesWithTournaments = allGames.stream()
                .map(game -> {
                    List<Tournament> gameTours = ts.findGameTournaments(game.getGame().getId());
                    return new Object[]{game, gameTours.size()};
                })
                .filter(gameData -> (Integer) gameData[1] > 0)
                .sorted((a, b) -> Integer.compare((Integer) b[1], (Integer) a[1]))
                .limit(5)
                .map(gameData -> (GameImg) gameData[0])
                .toList();

        for (GameImg game : topGamesWithTournaments) {
            tournamentFilter.setGame_id(game.getGame().getId());
            List<TournamentImg> gameTours = ts.findWithImg(tournamentFilter);
            mav.addObject("tournaments" + game.getGame().getId(), gameTours);
            mav.addObject("game" + game.getGame().getId(), game);
        }
        return mav;
    }

    @PostMapping("/login")
    public ModelAndView login(@Valid @ModelAttribute("loginForm") UserForm form, HttpServletRequest request, BindingResult result ) {
        if (result.hasErrors()) {
            ModelAndView mav = index(request, form, registerForm(), tournamentForm(), new TournamentFilter());
            mav.addObject("openModal", "'createTournamentModal'");
            return mav;
        }

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
    public ModelAndView createTournament(HttpServletRequest request, @Valid @ModelAttribute("tournamentForm") final TournamentForm form, final BindingResult result) {

        if (result.hasErrors()) {
            ModelAndView mav = index(request, loginForm(), registerForm(), form, new TournamentFilter());
            mav.addObject("openModal", "'createTournamentModal'");
            return mav;
        }
        User user = (User) request.getSession().getAttribute("user");
        Optional<Game> optionalGame = gs.findById(form.getGame_id());
        if (optionalGame.isEmpty()){
            return new ModelAndView("game");
        }

        byte[] imageBytes = null;
        try {
            if (form.getImage() != null && !form.getImage().isEmpty()) {
                imageBytes = form.getImage().getBytes();
            }
        } catch (IOException e) {
            //TBD
            return new ModelAndView("index");
        }

        final Tournament t = ts.create(user.getId(), form.getName(), optionalGame.get().getId(),
                form.getRegion(), form.getElo(), form.getStart_date(), form.getEnd_date(),
                form.getFormat(), form.getStructure(), form.getMax_participants(), imageBytes, true, false);
        String tournamentLink = request.getRequestURL().toString()
                .replace("/tournament/create", "/tournament?tournamentId=" + t.getId());
        ms.sendTournamentCreatedEmail(user.getUsername(), t.getName(), tournamentLink, user.getEmail());
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

        byte[] imageBytes = null;
        try {
            if (form.getImage() != null && !form.getImage().isEmpty()) {
                imageBytes = form.getImage().getBytes();
            }
        } catch (IOException e) {
            //TBD
            return new ModelAndView("index");
        }

        final Game g = gs.createWithFormats(form.getName(), form.getGenre(), form.getFormats(), imageBytes);
        return new ModelAndView("redirect:/game?game_id=" + g.getId());
    }

    @RequestMapping("/game")
    public ModelAndView game(HttpServletRequest request, @RequestParam("game_id") final long game_id, @ModelAttribute("filterForm") FilterForm filterForm, TournamentFilter tf){
        final ModelAndView mav = new ModelAndView("game");
        Optional<GameImg> optionalGame = gs.findByIdWithImage(game_id);
        if(optionalGame.isPresent()) {
            mav.addObject("game", optionalGame.get());
        } else {
            return new ModelAndView("index");
        }
        mav.addObject("user", request.getSession().getAttribute("user"));
        mav.addObject("structures", Arrays.stream(Structure.values()).toList());
        mav.addObject("regions", Arrays.stream(Region.values()).toList());
        mav.addObject("elos", Arrays.stream(Elo.values()).toList());

        tf.setGame_id(game_id);
        tf.setRegion(filterForm.getRegion());
        tf.setElo(filterForm.getElo());

        mav.addObject("tournaments", ts.findWithImg(tf));
        mav.addObject("isFiltered", !tf.isEmpty());

        return mav;
    }

    @RequestMapping("/tournament")
    public ModelAndView tournamentPage(HttpServletRequest request, @RequestParam("tournamentId") final long tournamentId) {
        final ModelAndView mav = new ModelAndView("tournament");
        User user = (User) request.getSession().getAttribute("user");
        mav.addObject("user", user);
        if (user == null){
            return new ModelAndView("redirect:/");
        }
        Optional<TournamentImg> optionalTournament = ts.findByIdWithImg(tournamentId);
        List <MatchWithPlayers> matches = ts.getTournamentMatchesWithPlayers(tournamentId);

        Map<Integer, List<MatchWithPlayers>> matchesByStage = new LinkedHashMap<>();
        if (!matches.isEmpty() && optionalTournament.isPresent()) {
            for (MatchWithPlayers match : matches) {
                int stage = match.getStage();
                matchesByStage.computeIfAbsent(stage, k -> new ArrayList<>()).add(match);
            }
        }
        if(optionalTournament.isPresent()) {
            TournamentImg t = optionalTournament.get();
            Optional<Game> optionalGame = gs.findById(t.getTournament().getGame_id());
            Optional<User> optionalUser = us.findById(t.getTournament().getCreator_id());
            mav.addObject("hasJoined", ts.hasJoined(user.getId(), tournamentId));
            mav.addObject("participants", ts.getTournamentParticipants(tournamentId));
            mav.addObject("user", user);
            mav.addObject("tournamentImg", t);
            mav.addObject("game", optionalGame.get());
            mav.addObject("creator", optionalUser.get());
            mav.addObject("genericMatches", ts.getMatches(tournamentId));
            mav.addObject("matchesByStage", matchesByStage);
        } else {
            return new ModelAndView("index");
        }
        return mav;
    }

    @RequestMapping(value = "/tournament/join", method = { RequestMethod.POST })
    public ModelAndView joinTournament(HttpServletRequest request, @RequestParam("tournamentId") final long tournamentId) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        ts.joinTournamentUser(user.getId(), tournamentId);
        return new ModelAndView("redirect:/tournament?tournamentId=" + tournamentId);
    }

    @RequestMapping(value = "/tournament/closeInscriptions", method = { RequestMethod.POST })
    public ModelAndView closeInscriptions(HttpServletRequest request, @RequestParam("tournamentId") final long tournamentId) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }

        Optional<TournamentImg> tournamentOpt = ts.findByIdWithImg(tournamentId);
        if (tournamentOpt.isPresent() && tournamentOpt.get().getTournament().getCreator_id().equals(user.getId())) {
            ts.closeInscriptions(tournamentId);
        }

        return new ModelAndView("redirect:/tournament?tournamentId=" + tournamentId);
    }

    @RequestMapping(value = "/tournament/setWinner", method = { RequestMethod.POST })
    public ModelAndView setWinner(HttpServletRequest request, @ModelAttribute("setWinnerForm") SetWinnerForm form) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }

        Optional<Tournament> tournamentOpt = ts.findById(form.getTournamentId());
        if (tournamentOpt.isPresent() && tournamentOpt.get().getCreator_id().equals(user.getId())) {
            ts.setMatchWinner(form.getMatchId(), form.getTournamentId(), form.getWinner());
        }

        return new ModelAndView("redirect:/tournament?tournamentId=" + form.getTournamentId() + "&section=Matches");
    }

    @RequestMapping(value = "/tournamentsPage")
    public ModelAndView tournamentsPage(HttpServletRequest request, @ModelAttribute("filterForm") FilterForm filterForm, TournamentFilter tf) {
        final ModelAndView mav = new ModelAndView("tournamentsPage");
        List<Game> allGames = gs.findAll();

        mav.addObject("user", request.getSession().getAttribute("user"));
        mav.addObject("games", allGames);
        mav.addObject("structures", Arrays.stream(Structure.values()).toList());
        mav.addObject("regions", Arrays.stream(Region.values()).toList());
        mav.addObject("elos", Arrays.stream(Elo.values()).toList());

        tf.setGame_id(filterForm.getGame_id());
        tf.setRegion(filterForm.getRegion());
        tf.setElo(filterForm.getElo());

        boolean isFiltered = !tf.isEmpty();

        if(isFiltered) {
            mav.addObject("tournaments", ts.findWithImg(tf));
        }else{
            Map<Long, List<TournamentImg>> gameTournaments = new HashMap<>();
            for (Game game : allGames) {
                tf.setGame_id(game.getId());
                List<TournamentImg> gameTours = ts.findWithImg(tf);
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

    @RequestMapping("/gamesPage")
    public ModelAndView gamesPage(HttpServletRequest request) {
        final ModelAndView mav = new ModelAndView("gamesPage");
        List<GameImg> allGames = gs.findAllWithImg();

        mav.addObject("user", request.getSession().getAttribute("user"));
        mav.addObject("games", allGames);

        return mav;
    }

    @RequestMapping("/myTournaments")
    public ModelAndView myTournaments(HttpServletRequest request) {
        final ModelAndView mav = new ModelAndView("myTournaments");
        User user = (User) request.getSession().getAttribute("user");
        List<TournamentImg> allCreatedTournaments = ts.findByCreatorImg(user.getId());

        List<TournamentImg> onGoingTournaments = allCreatedTournaments.stream()
            .filter(t -> !t.getTournament().getFinished())
            .toList();

        List<TournamentImg> finishedTournaments = allCreatedTournaments.stream()
            .filter(t -> t.getTournament().getFinished())
            .toList();
        List<TournamentImg> joinedTournaments = ts.findUserActiveTournaments(user.getId());
        List<TournamentImg> pastTournaments = ts.findUserPastTournaments(user.getId());

        mav.addObject("user", user);
        mav.addObject("pastTournaments", pastTournaments);
        mav.addObject("onGoingTournaments", onGoingTournaments);
        mav.addObject("finishedTournaments", finishedTournaments);
        mav.addObject("joinedTournaments", joinedTournaments);

        return mav;
    }

    @RequestMapping("/send")
    public ModelAndView sendTestEmail() {
        ms.sendSimpleMessage("brunitaccone@gmail.com", "Test Subject", "hola mundo  ");
        return new ModelAndView("index");
    }
}