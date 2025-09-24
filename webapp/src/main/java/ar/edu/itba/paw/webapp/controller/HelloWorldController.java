package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exception.EmailAlreadyUsedException;
import ar.edu.itba.paw.interfaces.exception.UsernameAlreadyUsedException;
import ar.edu.itba.paw.interfaces.services.GameService;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameImg;
import ar.edu.itba.paw.model.MatchWithPlayers;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.Tournament.TournamentImg;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.webapp.form.*;
import ar.edu.itba.paw.webapp.form.FilterForm;
import ar.edu.itba.paw.webapp.form.GameForm;
import ar.edu.itba.paw.webapp.form.SetWinnerForm;
import ar.edu.itba.paw.webapp.form.TournamentForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.naming.Binding;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("tournamentForm")
public class HelloWorldController {

    private final UserService us;
    private final GameService gs;
    private final TournamentService ts;
    private final MailService ms;

    @ModelAttribute("loginForm")
    public UserForm loginForm() { return new UserForm(); }

    @ModelAttribute("registerForm")
    public UserForm registerForm() { return new UserForm();  }

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

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView register(@Valid @ModelAttribute("registerForm") UserForm form, final BindingResult result) {
        if (result.hasErrors()) {
            return registerPage(form);
        }

        User user = null;
        try {
            user = us.create(form.getUsername(), form.getEmail(), form.getPassword());
        } catch (EmailAlreadyUsedException e) {
            result.rejectValue("email", "error.registerForm.emailUsed", e.getMessage());
            // TODO: add mav return?
        } catch (UsernameAlreadyUsedException e) {
            result.rejectValue("username", "error.registerForm.usernameUsed", e.getMessage());
            // TODO: add mav return?
        }
        us.sendVerificationEmail(form.getEmail());
        return new ModelAndView("redirect:/verify?userId=" + user.getId());
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView registerPage(@ModelAttribute("registerForm") UserForm form) {
        return new ModelAndView("registerPage");
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public ModelAndView resendVerification(@RequestParam("userId") long userId) {
        Optional<User> user = us.findById(userId);
        us.sendVerificationEmail(user.get().getEmail());
        return new ModelAndView("redirect:/verify?userId=" + userId);
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public ModelAndView verifyPage(@RequestParam("userId") long userId) {
        ModelAndView mav = new ModelAndView("verificationPage");
        Optional<User> userOpt = us.findById(userId);
        if (userOpt.isPresent()) {
            mav.addObject("user", userOpt.get());
        } else {
            return new ModelAndView("redirect:/register");
        }
        return mav;
    }

    @RequestMapping("/verify/confirm")
    public ModelAndView confirmedVerificationPage(@RequestParam("token") Long token, @RequestParam("userId") long userId) {
        ModelAndView mav = new ModelAndView("confirmedVerificationPage");
        Optional<Token> validToken = us.verifyEmail(token, userId);
        mav.addObject("validToken", validToken.isPresent());
        mav.addObject("userId", userId);
        return mav;
    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
    public ModelAndView forgotPasswordPage(@ModelAttribute("emailForm") EmailForm emailForm) {
        ModelAndView mav = new ModelAndView("forgotPassword");
        mav.addObject("emailForm", emailForm);
        return mav;
    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public ModelAndView forgotPassword(@Valid @ModelAttribute("emailForm") EmailForm emailForm, BindingResult result) {
        if(result.hasErrors()) {
            return forgotPasswordPage(emailForm);
        }
        us.requestPasswordReset(emailForm.getEmail());
        return new ModelAndView("redirect:/requestPasswordReset");
    }

    @RequestMapping("/requestPasswordReset")
    public ModelAndView requestPasswordResetPage() {
        return new ModelAndView("requestPasswordReset");
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
    public ModelAndView resetPasswordPage(@RequestParam("token") Long token, @RequestParam("userId") long userId, @ModelAttribute("resetPasswordForm") ResetPasswordForm resetPasswordForm) {
        ModelAndView mav = new ModelAndView("resetPasswordPage");
        Optional<Token> validToken = us.checkTokenValidity(token, userId);
        mav.addObject("resetPasswordForm", resetPasswordForm);
        mav.addObject("token", token);
        mav.addObject("userId", userId);
        mav.addObject("validToken", validToken.isPresent());
        return mav;
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public ModelAndView resetPassword(@RequestParam("token") Long token, @RequestParam("userId") long userId, @Valid @ModelAttribute("resetPasswordForm") ResetPasswordForm resetPasswordForm, BindingResult result) {
        if(result.hasErrors()) {
            return resetPasswordPage(token, userId, resetPasswordForm);
        }
        resetPasswordForm.setUserId(userId);
        us.resetPassword(token, userId, resetPasswordForm.getNewPassword());
        return new ModelAndView("redirect:/resetPassword/success");
    }

    @RequestMapping("/resetPassword/success")
    public ModelAndView resetPasswordSuccess() {
        return new ModelAndView("resetPasswordSuccess");
    }

    @RequestMapping("/")
    public ModelAndView index(@ModelAttribute("tournamentForm") TournamentForm tournamentForm, TournamentFilter tournamentFilter, Principal principal) {
        final ModelAndView mav = new ModelAndView("index");
        List<GameImg> allGames = gs.findAllWithImg();

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

    @RequestMapping(value = "/login")
    public ModelAndView login(@RequestParam(value = "error", required = false) String error) {
        ModelAndView mav = new ModelAndView("loginPage");
        if(error != null) {
            mav.addObject("invalidCredentials", true);
        }
        return mav;
    }

    @RequestMapping("/logout")
    public ModelAndView logout() {
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(value = "/tournament/create", method = { RequestMethod.POST })
    public ModelAndView createTournament(Principal principal, HttpServletRequest request, @Valid @ModelAttribute("tournamentForm") final TournamentForm form, final BindingResult result) {
        if (result.hasErrors()) {
            ModelAndView mav = index(form, new TournamentFilter(), null);
            mav.addObject("openModal", "'createTournamentModal'");
            return mav;
        }
        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }
        Optional<Game> optionalGame = gs.findById(form.getGame_id());

        byte[] imageBytes = null;
        try {
            if (form.getImage() != null && !form.getImage().isEmpty()) {
                imageBytes = form.getImage().getBytes();
            }else {
                result.rejectValue("image", "error.tournamentForm.emptyImage");
                ModelAndView mav = index(form, new TournamentFilter(), principal);
                mav.addObject("openModal", "'createTournamentModal'");
                return mav;
            }
        } catch (IOException e) {
            result.rejectValue("image", "error.tournamentForm.invalidImage", e.getMessage());
            ModelAndView mav = index(form, new TournamentFilter(), principal);
            mav.addObject("openModal", "'createTournamentModal'");
            return mav;
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
        return new ModelAndView("redirect:/" + g.getId());
    }

    @Autowired
    private MessageSource messageSource;
    @RequestMapping("/tournament")
    public ModelAndView tournamentPage(Principal principal, @RequestParam("tournamentId") final long tournamentId) {
        final ModelAndView mav = new ModelAndView("tournament");
        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }
        mav.addObject("user", user);

        Optional<TournamentImg> optionalTournament = ts.findByIdWithImg(tournamentId);

        Map<Integer, Map<Integer, List<MatchWithPlayers>>> matchesByGroup = ts.getTournamentMatchesByGroup(tournamentId);

        List<Integer> groupSections = new ArrayList<>(matchesByGroup.keySet());
        Locale locale = LocaleContextHolder.getLocale();
        List<String> groupLabels = groupSections.stream()
                .map(key -> messageSource.getMessage("tournament.group", new Object[]{key}, locale))
                .collect(Collectors.toList());

        if(optionalTournament.isPresent()) {
            TournamentImg t = optionalTournament.get();
            Optional<Game> optionalGame = gs.findById(t.getTournament().getGame_id());
            Optional<User> optionalUser = us.findById(t.getTournament().getCreator_id());
            mav.addObject("hasJoined", ts.hasJoined(user.getId(), tournamentId));
            mav.addObject("participants", ts.getTournamentParticipantsByGroup(tournamentId));
            mav.addObject("user", user);
            mav.addObject("tournamentImg", t);
            mav.addObject("game", optionalGame.get());
            mav.addObject("creator", optionalUser.get());
            mav.addObject("matchesByGroup", matchesByGroup);
            mav.addObject("groupSections", groupSections);
            mav.addObject("groupLabels", groupLabels);
            mav.addObject("LEAGUE", Structure.LEAGUE);
            mav.addObject("ELIMINATION", Structure.ELIMINATION);
            mav.addObject("HYBRID", Structure.HYBRID);
            mav.addObject("tournamentWinner", t.getTournament().getTournament_winner());
        } else {
            return new ModelAndView("index");
        }
        return mav;
    }

    @RequestMapping(value = "/tournament/join", method = { RequestMethod.POST })
    public ModelAndView joinTournament(Principal principal, HttpServletRequest request, @RequestParam("tournamentId") final long tournamentId) {
        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        Optional<Tournament> t = ts.findById(tournamentId);
        String tournamentLink = request.getRequestURL().toString()
                .replace("/tournament/join", "/tournament?tournamentId=" + t.get().getId());
        ms.sendTournamentJoinedEmail(user.getUsername(), t.get().getName(), tournamentLink, us.findById(t.get().getCreator_id()).get().getEmail(), user.getEmail());
        ts.joinTournamentUser(user.getId(), tournamentId);
        return new ModelAndView("redirect:/tournament?tournamentId=" + tournamentId);
    }

    @RequestMapping(value = "/tournament/closeInscriptions", method = { RequestMethod.POST })
    public ModelAndView closeInscriptions(Principal principal, @RequestParam("tournamentId") final long tournamentId) {
        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }
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
    public ModelAndView setWinner(Principal principal, @ModelAttribute("setWinnerForm") SetWinnerForm form) {
        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }
        if (user == null) {
            return new ModelAndView("redirect:/");
        }

        Optional<Tournament> tournamentOpt = ts.findById(form.getTournamentId());
        if (tournamentOpt.isPresent() && tournamentOpt.get().getCreator_id().equals(user.getId())) {
            ts.setMatchWinner(form.getMatchId(), form.getTournamentId(), form.getWinner());
        }

        return new ModelAndView("redirect:/tournament?tournamentId=" + form.getTournamentId() + "&section=matches");
    }

    @RequestMapping(value = "/tournamentsPage")
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
    public ModelAndView gamesPage(Principal principal) {
        final ModelAndView mav = new ModelAndView("gamesPage");
        List<GameImg> allGames = gs.findAllWithImg();
        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }
        mav.addObject("user", user);
        mav.addObject("games", allGames);

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

    @RequestMapping("/tournaments/new/step1")
    public ModelAndView newTournamentFormStep1(HttpServletRequest request, @ModelAttribute("tournamentForm") final TournamentForm form, Principal principal){
        final ModelAndView mav = new ModelAndView("tournamentForm");

        User user = us.findByUsername(principal.getName()).orElse(null);
        mav.addObject("user", user);
        mav.addObject("step", 1);
        mav.addObject("games", gs.findAll());
        mav.addObject("structures", Arrays.stream(Structure.values()).toList());
        mav.addObject("regions", Arrays.stream(Region.values()).toList());

        return mav;
    }

    @RequestMapping(value = "/tournaments/new/step1", method = RequestMethod.POST)
    public ModelAndView validateStep1(@Validated(TournamentForm.StepOne.class) @ModelAttribute("tournamentForm") TournamentForm form, BindingResult result, HttpServletRequest request, Principal principal) {

        ModelAndView mav = new ModelAndView("tournamentForm");

        if (result.hasErrors()) {
            return newTournamentFormStep1(request, form, principal);
        } else {
            return newTournamentFormStep2(request, form, principal);
        }
    }

    @RequestMapping(value = "/tournaments/new/step2", method = { RequestMethod.GET })
    public ModelAndView newTournamentFormStep2(HttpServletRequest request, @ModelAttribute("tournamentForm") final TournamentForm form, Principal principal){
        final ModelAndView mav = new ModelAndView("tournamentForm");

        User user = us.findByUsername(principal.getName()).orElse(null);
        mav.addObject("user", user);
        mav.addObject("step", 2);
        mav.addObject("formats", gs.getFormats(form.getGame_id()));
        mav.addObject("elos", Arrays.stream(Elo.values()).toList());
        return mav;
    }


    @RequestMapping(value = "/tournaments/new/step2", method = { RequestMethod.POST })
    public ModelAndView createTournament(Principal principal, HttpServletRequest request, @Validated(TournamentForm.StepTwo.class) @ModelAttribute("tournamentForm") final TournamentForm form, final BindingResult result, SessionStatus status) {
        if (result.hasErrors()) {
            return newTournamentFormStep2(request, form, principal);
        }
        User user = us.findByUsername(principal.getName()).orElse(null);
        Optional<Game> optionalGame = gs.findById(form.getGame_id());

        byte[] imageBytes = null;
        try {
            if (form.getImage() != null && !form.getImage().isEmpty()) {
                imageBytes = form.getImage().getBytes();
            }else {
                result.rejectValue("image", "error.tournamentForm.emptyImage");
                return newTournamentFormStep2(request, form,principal);
            }
        } catch (IOException e) {
            result.rejectValue("image", "error.tournamentForm.invalidImage", e.getMessage());
            return newTournamentFormStep2(request, form, principal);
        }

        final Tournament t = ts.create(user.getId(), form.getName(), optionalGame.get().getId(),
                form.getRegion(), form.getElo(), form.getStart_date(), form.getEnd_date(),
                form.getFormat(), form.getStructure(), form.getMax_participants(), imageBytes, true, false);
        String tournamentLink = request.getRequestURL().toString()
                .replace("/tournament/create", "/tournament?tournamentId=" + t.getId());
        ms.sendTournamentCreatedEmail(user.getUsername(), t.getName(), tournamentLink, user.getEmail());
        status.setComplete();
        return new ModelAndView("redirect:/tournament?tournamentId=" + t.getId());
    }
}