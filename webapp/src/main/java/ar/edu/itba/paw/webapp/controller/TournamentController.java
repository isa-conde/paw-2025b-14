package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.MatchInfo;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.webapp.form.EditTournamentForm;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

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
    private final MatchService ms;
    private final ParticipantService ps;

    public TournamentController(final UserService us, final GameService gs, final TournamentService ts, final MatchService ms, final ParticipantService ps) {
        this.us = us;
        this.gs = gs;
        this.ts = ts;
        this.ms = ms;
        this.ps = ps;
    }

    @ModelAttribute("tournamentForm")
    public TournamentForm getTournamentForm() {
        return new TournamentForm();
    }

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

    @RequestMapping(value = "/tournament/swap/groups", method = RequestMethod.POST)
    public ModelAndView swapGroups(
            Principal principal,
            @RequestParam("tournamentId") Long tournamentId,
            @RequestParam(name="selected", required=false) List<String> selected,
            RedirectAttributes ra) {

        User user = (principal != null) ? us.findByUsername(principal.getName()).orElse(null) : null;
        if (user == null) {
            return new ModelAndView("redirect:/");
        }

        Long user1 = Long.valueOf(selected.get(0));
        Long user2 = Long.valueOf(selected.get(1));

        ps.swapGroups(tournamentId, user1, user2);

        ra.addAttribute("tournamentId", tournamentId);
        ra.addAttribute("edit", true);
        return new ModelAndView("redirect:/tournament");
    }

    @RequestMapping(value = "/tournament/swap/matches", method = RequestMethod.POST)
    public ModelAndView swapMatchesMembers(
            Principal principal,
            @RequestParam("tournamentId") Long tournamentId,
            @RequestParam(name="selected", required=false) List<String> selected,
            RedirectAttributes ra) {

        User user = (principal != null) ? us.findByUsername(principal.getName()).orElse(null) : null;
        if (user == null) {
            return new ModelAndView("redirect:/");
        }

        String[] a = selected.get(0).split(":");
        String[] b = selected.get(1).split(":");
        Long match1 = Long.valueOf(a[0]), user1 = Long.valueOf(a[1]);
        Long match2 = Long.valueOf(b[0]), user2 = Long.valueOf(b[1]);

        ms.swapMatchesMembers(tournamentId, match1, match2, user1, user2);

        ra.addAttribute("tournamentId", tournamentId);
        ra.addAttribute("edit", true);
        return new ModelAndView("redirect:/tournament");
    }

    @RequestMapping(value = "/tournament/update", method = { RequestMethod.POST })
    public ModelAndView updateTournament(
            Principal principal,
            @RequestParam("tournamentId") final long tournamentId,
            @Valid @ModelAttribute("editTournamentForm") final EditTournamentForm form,
            final BindingResult result)
    {
        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }
        if (user == null) {
            return new ModelAndView("redirect:/");
        }

        if (result.hasErrors()) {
            return new ModelAndView("redirect:/tournament?tournamentId=" + tournamentId);
        }
        byte[] imageBytes = null;
        try {
            if (form.getImage() != null && !form.getImage().isEmpty()) {
                imageBytes = form.getImage().getBytes();
            }
            ts.updateTournamentInfo(tournamentId, form.getName(), form.getStart_date(), form.getEnd_date(), form.getMax_participants(), imageBytes);
        } catch (IOException e) {
            result.rejectValue("image", "error.tournamentForm.invalidImage");
        }
        return new ModelAndView("redirect:/tournament?tournamentId=" + tournamentId);
    }

    @RequestMapping(value = "/tournament", method = RequestMethod.GET)
    public ModelAndView tournamentPage(Principal principal, @RequestParam("tournamentId") final long tournamentId, @ModelAttribute("editTournamentForm") final EditTournamentForm form) {
        final ModelAndView mav = new ModelAndView("tournament");
        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }
        mav.addObject("user", user);

        Optional<Tournament> optionalTournament = ts.findById(tournamentId);

        Map<Integer, List<MatchInfo>> matches = ms.getTournamentMatchesByStage(tournamentId);
        long maxStage = matches != null ? matches.keySet().stream().max(Integer::compareTo).orElse(0) : 0L;

        Integer groups = ps.getTournamentGroups(tournamentId);
        List<ParticipantUserInfo> participants = ps.getTournamentParticipantUsersInfo(tournamentId);
        int participantCount = participants.size();
        Long userId = (user != null ? user.getId() : null);
        boolean isParticipant = (userId != null) && participants.stream().anyMatch(p -> Objects.equals(p.getUser_id(), userId));

        if(optionalTournament.isPresent()) {
            Tournament t = optionalTournament.get();
            form.setName(t.getName());
            form.setStart_date(t.getStart_date());
            form.setEnd_date(t.getEnd_date());
            form.setMax_participants(t.getMax_participants());
            Optional<Game> optionalGame = gs.findById(t.getGame_id());
            Optional<User> optionalUser = us.findById(t.getCreator_id());
            mav.addObject("hasJoined", ps.hasJoined(user.getId(), tournamentId));
            mav.addObject("participants", participants);
            mav.addObject("user", user);
            mav.addObject("tournament", t);
            mav.addObject("game", optionalGame.get());
            mav.addObject("creator", optionalUser.get());
            mav.addObject("matches", matches);
            mav.addObject("groups", groups);
            mav.addObject("LEAGUE", Structure.LEAGUE);
            mav.addObject("ELIMINATION", Structure.ELIMINATION);
            mav.addObject("HYBRID", Structure.HYBRID);
            mav.addObject("tournamentWinner", t.getTournament_winner());
            mav.addObject("participantCount", participantCount);
            mav.addObject("isParticipant", isParticipant);
            mav.addObject("maxStage", maxStage);
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
        ps.joinTournamentUser(user.getId(), tournamentId);
        return new ModelAndView("redirect:/tournament?tournamentId=" + tournamentId);
    }

    @RequestMapping(value = "/tournament/leave", method = { RequestMethod.POST })
    public ModelAndView leaveTournament(Principal principal, HttpServletRequest request, @RequestParam("tournamentId") final long tournamentId) {
        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        ps.leaveTournamentUser(user.getId(), tournamentId);
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
        }

        return new ModelAndView("redirect:/tournament?tournamentId=" + tournamentId);
    }

    @RequestMapping(value = "/tournament/startTournament", method = { RequestMethod.POST })
    public ModelAndView startTournament(Principal principal, @RequestParam("tournamentId") final long tournamentId) {
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
            ts.startTournament(tournamentId);
        }

        return new ModelAndView("redirect:/tournament?tournamentId=" + tournamentId);
    }

    @RequestMapping(value = "/tournament/setWinner", method = { RequestMethod.POST })
    public ModelAndView setWinner(Principal principal, @ModelAttribute("setWinnerForm") SetWinnerForm form, @RequestParam(value = "group", required = false) Integer group) {
        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }
        if (user == null) {
            return new ModelAndView("redirect:/");
        }

        Optional<Tournament> tournamentOpt = ts.findById(form.getTournamentId());
        if (tournamentOpt.isPresent()) {
            Tournament t = tournamentOpt.get();
            if(t.getCreator_id().equals(user.getId())){
                ms.setMatchWinner(form.getMatchId(), form.getTournamentId(), form.getWinner());
            }
            if(t.getFinished()){
                return new ModelAndView("redirect:/tournament?tournamentId=" + t.getId());
            }
        }
        String redirect = UriComponentsBuilder.fromPath("/tournament")
                .queryParam("tournamentId", form.getTournamentId())
                .queryParam("section", "matchesTab")
                .queryParamIfPresent("group", java.util.Optional.ofNullable(group))
                .toUriString();

        return new ModelAndView("redirect:" + redirect);
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

        byte[] imageBytes;
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
        status.setComplete();
        return new ModelAndView("redirect:/tournament?tournamentId=" + t.getId());
    }
}