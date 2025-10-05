package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exception.TournamentNotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.MatchInfo;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.webapp.auth.PawUserDetails;
import ar.edu.itba.paw.webapp.form.EditTournamentForm;
import ar.edu.itba.paw.webapp.form.GameForm;
import ar.edu.itba.paw.webapp.form.SetWinnerForm;
import ar.edu.itba.paw.webapp.form.TournamentForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

@Controller
@SessionAttributes("tournamentForm")
public class TournamentController {

    private final UserService us;
    private final GameService gs;
    private final TournamentService ts;
    private final MatchService ms;
    private final ParticipantService ps;
    private final TeamService tms;

    public TournamentController(final UserService us, final GameService gs, final TournamentService ts, final MatchService ms, final ParticipantService ps, final TeamService tms) {
        this.us = us;
        this.gs = gs;
        this.ts = ts;
        this.ms = ms;
        this.ps = ps;
        this.tms = tms;
    }

    @ModelAttribute("tournamentForm")
    public TournamentForm getTournamentForm() {
        return new TournamentForm();
    }

    @RequestMapping(value = "/tournament/swap/groups", method = RequestMethod.POST)
    public ModelAndView swapGroups(@RequestParam("tournamentId") Long tournamentId,
                                   @RequestParam(name="selected", required=false) List<String> selected,
                                   RedirectAttributes ra) {

        Long user1 = Long.valueOf(selected.get(0));
        Long user2 = Long.valueOf(selected.get(1));

        ps.swapGroups(tournamentId, user1, user2);

        ra.addAttribute("tournamentId", tournamentId);
        ra.addAttribute("edit", true);
        return new ModelAndView("redirect:/tournament");
    }

    @RequestMapping(value = "/tournament/swap/matches", method = RequestMethod.POST)
    public ModelAndView swapMatchesMembers(
            @RequestParam("tournamentId") Long tournamentId,
            @RequestParam(name="selected", required=false) List<String> selected,
            RedirectAttributes ra) {

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
            final BindingResult result) {
        if (result.hasErrors()) {
            ModelAndView mav = tournamentPage(principal, tournamentId, form);
            mav.addObject("openEditModal", Boolean.TRUE);
            return mav;
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


        if(optionalTournament.isPresent()) {
            Tournament t = optionalTournament.get();
            boolean hasFormErrors = mav.getModel().containsKey(
                    BindingResult.MODEL_KEY_PREFIX + "editTournamentForm"
            );
            if (!hasFormErrors) {
                form.setName(t.getName());
                if(!t.getTournamentStarted()){
                    form.setStart_date(t.getStart_date());
                    form.setMax_participants(t.getMax_participants());
                }
                if(!t.getFinished()) {
                    form.setEnd_date(t.getEnd_date());
                }
            }
            Optional<GameFormat> optionalGameFormat = gs.getFormatById(t.getFormat_id());
            if (optionalGameFormat.isPresent()){
                GameFormat gf = optionalGameFormat.get();
                mav.addObject("format", gf);
                t.setFormat(gf.getName());
            }
            List<ParticipantInfo> participants = ps.getTournamentParticipantInfo(tournamentId, optionalGameFormat.isPresent() ? optionalGameFormat.get().getPlayers_per_team() : 1);
            int participantCount = participants.size();
            Optional<Game> optionalGame = gs.findById(t.getGame_id());
            Optional<User> optionalUser = us.findById(t.getCreator_id());
            Boolean isIndividualTournament = optionalGameFormat.isEmpty() || optionalGameFormat.get().getPlayers_per_team() == 1;
            Boolean isParticipant = user != null && ps.hasJoined(user.getId(), tournamentId);
            if (user != null && !isIndividualTournament && !isParticipant){
                mav.addObject("userTeams", tms.getUserTeams(user.getId()));
            }
            mav.addObject("isIndividualTournament", isIndividualTournament);
            mav.addObject("isParticipant", isParticipant);
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
            mav.addObject("maxStage", maxStage);
        }
        return mav;
    }

    @RequestMapping(value = "/tournament/join", method = { RequestMethod.POST })
    public ModelAndView joinTournament(@ModelAttribute("user") Optional<PawUserDetails> currentUser, HttpServletRequest request, @RequestParam("tournamentId") final long tournamentId) {
        User user = currentUser.get().getPawUser();

        String tournamentLink = request.getRequestURL().toString()
                .replace("/tournament/join", "/tournament?tournamentId=" + tournamentId);
        us.sendTournamentJoinedEmail(user.getUsername(), tournamentId, tournamentLink, user.getEmail());
        ps.joinTournamentUser(user.getId(), tournamentId);
        return new ModelAndView("redirect:/tournament?tournamentId=" + tournamentId);
    }

    @RequestMapping(value = "/tournament/leave", method = { RequestMethod.POST })
    public ModelAndView leaveTournament(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @RequestParam("tournamentId") final long tournamentId) {
        User user = currentUser.get().getPawUser();

        ps.leaveTournamentUser(user.getId(), tournamentId);
        return new ModelAndView("redirect:/tournament?tournamentId=" + tournamentId);
    }

    @RequestMapping(value = "/tournament/closeInscriptions", method = { RequestMethod.POST })
    public ModelAndView closeInscriptions(@RequestParam("tournamentId") final long tournamentId) {
        ts.closeInscriptions(tournamentId);
        return new ModelAndView("redirect:/tournament?tournamentId=" + tournamentId);
    }

    @RequestMapping(value = "/tournament/startTournament", method = { RequestMethod.POST })
    public ModelAndView startTournament(@RequestParam("tournamentId") final long tournamentId) {
        ts.startTournament(tournamentId);
        return new ModelAndView("redirect:/tournament?tournamentId=" + tournamentId);
    }

    @RequestMapping(value = "/tournament/setWinner", method = { RequestMethod.POST })
    public ModelAndView setWinner(@ModelAttribute("setWinnerForm") SetWinnerForm form, @RequestParam(value = "group", required = false) Integer group) {
        ms.setMatchWinner(form.getMatchId(), form.getTournamentId(), form.getWinner());
        String redirect = UriComponentsBuilder.fromPath("/tournament")
                .queryParam("tournamentId", form.getTournamentId())
                .queryParam("section", "matchesTab")
                .queryParamIfPresent("group", java.util.Optional.ofNullable(group))
                .toUriString();

        return new ModelAndView("redirect:" + redirect);
    }

    @RequestMapping("/tournaments/new/step1")
    public ModelAndView newTournamentFormStep1(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @ModelAttribute("tournamentForm") final TournamentForm form){
        final ModelAndView mav = new ModelAndView("tournamentForm");

        User user = currentUser.get().getPawUser();
        mav.addObject("user", user);
        mav.addObject("step", 1);
        mav.addObject("games", gs.findAll());
        mav.addObject("structures", Arrays.stream(Structure.values()).toList());
        mav.addObject("regions", Arrays.stream(Region.values()).toList());

        return mav;
    }

    @RequestMapping(value = "/tournaments/new/step1", method = RequestMethod.POST)
    public ModelAndView validateStep1(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @Validated(TournamentForm.StepOne.class) @ModelAttribute("tournamentForm") TournamentForm form, BindingResult result) {
        if (result.hasErrors()) {
            return newTournamentFormStep1(currentUser, form);
        } else {
            return newTournamentFormStep2(currentUser, form);
        }
    }

    @RequestMapping(value = "/tournaments/new/step2", method = { RequestMethod.GET })
    public ModelAndView newTournamentFormStep2(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @ModelAttribute("tournamentForm") final TournamentForm form){
        final ModelAndView mav = new ModelAndView("tournamentForm");

        User user = currentUser.get().getPawUser();
        mav.addObject("user", user);
        mav.addObject("step", 2);
        mav.addObject("formats", gs.getFormats(form.getGame_id()));
        mav.addObject("elos", Arrays.stream(Elo.values()).toList());
        return mav;
    }


    @RequestMapping(value = "/tournaments/new/step2", method = { RequestMethod.POST })
    public ModelAndView createTournament(@ModelAttribute("user") Optional<PawUserDetails> currentUser, HttpServletRequest request, @Validated(TournamentForm.StepTwo.class) @ModelAttribute("tournamentForm") final TournamentForm form, final BindingResult result, SessionStatus status) {
        if (result.hasErrors()) {
            return newTournamentFormStep2(currentUser, form);
        }
        User user = currentUser.get().getPawUser();

        byte[] imageBytes;
        try {
            if (form.getImage() != null && !form.getImage().isEmpty()) {
                imageBytes = form.getImage().getBytes();
            }else {
                result.rejectValue("image", "error.tournamentForm.emptyImage");
                return newTournamentFormStep2(currentUser, form);
            }
        } catch (IOException e) {
            result.rejectValue("image", "error.tournamentForm.invalidImage", e.getMessage());
            return newTournamentFormStep2(currentUser, form);
        }

        final Tournament t = ts.create(user.getId(), form.getName(), form.getGame_id(),
                form.getRegion(), form.getElo(), form.getStart_date(), form.getEnd_date(),
                null, form.getStructure(), form.getMax_participants(), imageBytes, true, false, form.getFormat_id());
        String tournamentLink = request.getRequestURL().toString()
                .replace("/tournament/create", "/tournament?tournamentId=" + t.getId());
        us.sendTournamentCreatedEmail(user.getUsername(), form.getName(), tournamentLink, user.getEmail());
        status.setComplete();
        return new ModelAndView("redirect:/tournament?tournamentId=" + t.getId());
    }
}