package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.GameService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.*;
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
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    public ModelAndView index(HttpServletRequest request, @ModelAttribute("loginForm") UserForm loginForm, @ModelAttribute("registerForm") UserForm registerForm, @ModelAttribute("tournamentForm") TournamentForm tournamentForm, TournamentFilter tournamentFilter) {
        final ModelAndView mav = new ModelAndView("index");
        User user = (User) request.getSession().getAttribute("user");
        List<GameImg> allGames = gs.findAllWithImg();
        mav.addObject("user", user);
        mav.addObject("games", allGames);
        mav.addObject("regions", Arrays.stream(Region.values()).toList());
        mav.addObject("elos", Arrays.stream(Elo.values()).toList());
        mav.addObject("structures", Arrays.stream(Structure.values()).toList());
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
        User user = (User) request.getSession().getAttribute("user");

        final ModelAndView mav = new ModelAndView("game");
        Optional<GameImg> optionalGame = gs.findByIdWithImage(game_id);
        if(optionalGame.isPresent()) {
            mav.addObject("game", optionalGame.get());
        } else {
            return new ModelAndView("index");
        }
        mav.addObject("user", user);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
        Optional<TournamentImg> optionalTournament = ts.findByIdWithImg(tournamentId);
        List <Match> matches = ts.getTournamentMatches(tournamentId);

        Map<Integer, List<Match>> matchesByDate = new LinkedHashMap<>();
        if (!matches.isEmpty() && optionalTournament.isPresent()) {
            int maxParticipants = optionalTournament.get().getTournament().getMax_participants();

            for (int i = 0; i < matches.size(); i++) {
                int dateNumber = (i / maxParticipants) + 1;
                matchesByDate.computeIfAbsent(dateNumber, k -> new ArrayList<>()).add(matches.get(i));
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
            mav.addObject("formatter", formatter);
            mav.addObject("genericMatches", ts.getGenericMatches(tournamentId));;
            mav.addObject("matchesByDate", matchesByDate);
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
        return tournamentPage(request, tournamentId);
    }

    @RequestMapping(value = "/tournamentsPage")
    public ModelAndView tournamentsPage(HttpServletRequest request, @ModelAttribute("filterForm") FilterForm filterForm, TournamentFilter tf) {
        final ModelAndView mav = new ModelAndView("tournamentsPage");
        List<Game> allGames = gs.findAll();
        User user = (User) request.getSession().getAttribute("user");
        mav.addObject("user", user);
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
        User user = (User) request.getSession().getAttribute("user");
        final ModelAndView mav = new ModelAndView("gamesPage");
        List<GameImg> allGames = gs.findAllWithImg();
        
        mav.addObject("user", user);
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
//        mav.addObject("createdTournaments", createdTournaments);

        mav.addObject("onGoingTournaments", onGoingTournaments);
        mav.addObject("finishedTournaments", finishedTournaments);
        mav.addObject("joinedTournaments", joinedTournaments);

        return mav;
    }
}