package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.GameService;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.MatchWithPlayers;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.webapp.form.GameForm;
import ar.edu.itba.paw.webapp.form.SetWinnerForm;
import ar.edu.itba.paw.webapp.form.TournamentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("tournamentForm")
public class TournamentController {

    private final UserService us;
    private final GameService gs;
    private final TournamentService ts;
    private final MailService ms;

    public TournamentController(final UserService us, final GameService gs, final TournamentService ts, final MailService ms) {
        this.us = us;
        this.gs = gs;
        this.ts = ts;
        this.ms = ms;
    }

//    @RequestMapping(value = "/tournament/create", method = { RequestMethod.POST })
//    public ModelAndView createTournament(Principal principal, HttpServletRequest request, @Valid @ModelAttribute("tournamentForm") final TournamentForm form, final BindingResult result) {
//        if (result.hasErrors()) {
//            ModelAndView mav = index(form, new TournamentFilter(), null);
//            mav.addObject("openModal", "'createTournamentModal'");
//            return mav;
//        }
//        User user = null;
//        if (principal != null) {
//            Optional<User> userOpt = us.findByUsername(principal.getName());
//            user = userOpt.orElse(null);
//        }
//        Optional<Game> optionalGame = gs.findById(form.getGame_id());
//
//        byte[] imageBytes = null;
//        try {
//            if (form.getImage() != null && !form.getImage().isEmpty()) {
//                imageBytes = form.getImage().getBytes();
//            }else {
//                result.rejectValue("image", "error.tournamentForm.emptyImage");
//                ModelAndView mav = index(form, new TournamentFilter(), principal);
//                mav.addObject("openModal", "'createTournamentModal'");
//                return mav;
//            }
//        } catch (IOException e) {
//            result.rejectValue("image", "error.tournamentForm.invalidImage", e.getMessage());
//            ModelAndView mav = index(form, new TournamentFilter(), principal);
//            mav.addObject("openModal", "'createTournamentModal'");
//            return mav;
//        }
//
//        final Tournament t = ts.create(user.getId(), form.getName(), optionalGame.get().getId(),
//            form.getRegion(), form.getElo(), form.getStart_date(), form.getEnd_date(),
//            form.getFormat(), form.getStructure(), form.getMax_participants(), imageBytes, true, false);
//        String tournamentLink = request.getRequestURL().toString()
//            .replace("/tournament/create", "/tournament?tournamentId=" + t.getId());
//        ms.sendTournamentCreatedEmail(user.getUsername(), t.getName(), tournamentLink, user.getEmail());
//        return new ModelAndView("redirect:/tournament?tournamentId=" + t.getId());
//    }


    // TODO: delete this!!!
    @RequestMapping(value = "/game/create", method = {RequestMethod.GET})
    public ModelAndView createGameForm(@ModelAttribute("gameForm") final GameForm form){
        ModelAndView mav = new ModelAndView("addGame");
        mav.addObject("genres", Genre.values()); // 🔹 paso el enum a la vista
        return mav;
    }


    // TODO: this too.......
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

        Optional<Tournament> optionalTournament = ts.findById(tournamentId);

        Map<Integer, Map<Integer, List<MatchWithPlayers>>> matchesByGroup = ts.getTournamentMatchesByGroup(tournamentId);

        List<Integer> groupSections = new ArrayList<>(matchesByGroup.keySet());
        Locale locale = LocaleContextHolder.getLocale();
        List<String> groupLabels = groupSections.stream()
                .map(key -> messageSource.getMessage("tournament.group", new Object[]{key}, locale))
                .collect(Collectors.toList());

        if(optionalTournament.isPresent()) {
            Tournament t = optionalTournament.get();
            Optional<Game> optionalGame = gs.findById(t.getGame_id());
            Optional<User> optionalUser = us.findById(t.getCreator_id());
            mav.addObject("hasJoined", ts.hasJoined(user.getId(), tournamentId));
            mav.addObject("participants", ts.getTournamentParticipantsByGroup(tournamentId));
            mav.addObject("user", user);
            mav.addObject("tournament", t);
            mav.addObject("game", optionalGame.get());
            mav.addObject("creator", optionalUser.get());
            mav.addObject("matchesByGroup", matchesByGroup);
            mav.addObject("groupSections", groupSections);
            mav.addObject("groupLabels", groupLabels);
            mav.addObject("LEAGUE", Structure.LEAGUE);
            mav.addObject("ELIMINATION", Structure.ELIMINATION);
            mav.addObject("HYBRID", Structure.HYBRID);
            mav.addObject("tournamentWinner", t.getTournament_winner());
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

        Optional<Tournament> tournamentOpt = ts.findById(tournamentId);
        if (tournamentOpt.isPresent() && tournamentOpt.get().getCreator_id().equals(user.getId())) {
            ts.closeInscriptions(tournamentId);
            ts.startTournament(tournamentId);
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