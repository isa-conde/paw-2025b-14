package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exception.GameNotFoundException;
import ar.edu.itba.paw.interfaces.exception.UserNotAuthenticatedException;
import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.Match.Match;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.webapp.auth.PawUserDetails;
import ar.edu.itba.paw.webapp.form.EditTournamentForm;
import ar.edu.itba.paw.webapp.form.SetMatchResultsForm;
import ar.edu.itba.paw.webapp.form.TournamentForm;
import ar.edu.itba.paw.webapp.form.*;
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

import javax.validation.Valid;
import java.io.IOException;
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
    private final TeamService tms;
    private final RulesService rs;

    @Autowired
    MessageSource messageSource;

    public TournamentController(final UserService us, final GameService gs, final TournamentService ts, final MatchService ms, final ParticipantService ps, final TeamService tms, final RulesService rs) {
        this.us = us;
        this.gs = gs;
        this.ts = ts;
        this.ms = ms;
        this.ps = ps;
        this.tms = tms;
        this.rs = rs;
    }

    @ModelAttribute("tournamentForm")
    public TournamentForm getTournamentForm() {
        return new TournamentForm();
    }

    @ModelAttribute("editTournamentForm")
    public EditTournamentForm getEditTournamentForm() {
        return new EditTournamentForm();
    }

    @ModelAttribute("contactOwnerForm")
    public ContactOwnerForm getContactOwnerForm() {
        return new ContactOwnerForm();
    }

    @ModelAttribute("rateTournamentForm")
    public RateTournamentForm getRateTournamentForm() {
        return new RateTournamentForm();
    }

    @ModelAttribute("setMatchResultsForm")
    public SetMatchResultsForm getSetMatchResultsForm() {
        return new SetMatchResultsForm();
    }

    @ModelAttribute("removeParticipantForm")
    public RemoveParticipantForm getRemoveParticipantForm() {
        return new RemoveParticipantForm();
    }

    @ModelAttribute("joinTournamentTeamForm")
    public JoinTournamentTeamForm getJoinTournamentTeamForm() {
        return new JoinTournamentTeamForm();
    }

    @RequestMapping(value = "/tournament/swap/groups", method = RequestMethod.POST)
    public ModelAndView swapGroups(@RequestParam("tournamentId") long tournamentId,
                                   @RequestParam(name="selected", required=false) List<String> selected,
                                   RedirectAttributes ra) {

        long user1 = Long.valueOf(selected.get(0));
        long user2 = Long.valueOf(selected.get(1));

        ps.swapGroups(tournamentId, user1, user2);

        ra.addAttribute("tournamentId", tournamentId);
        ra.addAttribute("edit", true);
        return new ModelAndView("redirect:/tournament");
    }

    @RequestMapping(value = "/tournament/swap/matches", method = RequestMethod.POST)
    public ModelAndView swapMatchesMembers(
            @RequestParam("tournamentId") long tournamentId,
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

    @RequestMapping(value = "/tournament/update/{tournamentId}", method = RequestMethod.POST)
    public ModelAndView updateTournament(
            @ModelAttribute("user") Optional<PawUserDetails> currentUser,
            @PathVariable("tournamentId") final long tournamentId,
            @Valid @ModelAttribute("editTournamentForm") final EditTournamentForm form,
            final BindingResult result) {
        if (result.hasErrors()) {
            ModelAndView mav = tournamentPage(currentUser, tournamentId, form, new JoinTournamentTeamForm(), new SetMatchResultsForm());
            mav.addObject("editTournamentForm", form);
            mav.addObject("joinTeamForm", new JoinTournamentTeamForm());
            mav.addObject("openModal", "'editTournamentModal'");
            return mav;
        }

        byte[] imageBytes = null;
        try {
            if (form.getImage() != null && !form.getImage().isEmpty()) {
                imageBytes = form.getImage().getBytes();
            }
            ts.updateTournamentInfo(tournamentId, form.getName(), form.getStartDate(), form.getEndDate(), form.getMaxParticipants(), imageBytes, form.getServerName(), form.getServerPassword(), form.getDiscordChannel());
        } catch (IOException e) {
            result.rejectValue("image", "error.tournamentForm.invalidImage");
        }

        byte[] rulesBytes;
        try {
            if (form.getRules() != null && !form.getRules().isEmpty()) {
                rulesBytes = form.getRules().getBytes();
                rs.updateRules(tournamentId, rulesBytes);
            }
        } catch (IOException e) {
            result.rejectValue("image", "error.tournamentForm.invalidImage");
        }
        return new ModelAndView("redirect:/tournament/" + tournamentId);
    }

    @RequestMapping(value = "/tournament/{tournamentId}", method = RequestMethod.GET)
    public ModelAndView tournamentPage(@ModelAttribute("user") Optional<PawUserDetails> currentUser,
                                       @PathVariable("tournamentId") final long tournamentId,
                                       @ModelAttribute("editTournamentForm") final EditTournamentForm editTournamentForm,
                                       @ModelAttribute("joinTeamForm") JoinTournamentTeamForm joinTournamentTeamForm,
                                       @ModelAttribute("setMatchResultsForm") SetMatchResultsForm setMatchResultsForm) {

        final ModelAndView mav = new ModelAndView("tournament");
        User user = null;
        if (currentUser.isPresent()) {
            user = currentUser.get().getPawUser();
            mav.addObject("user", user);
        }

        Optional<Tournament> optionalTournament = ts.findById(tournamentId);

        Map<Integer, List<Match>> matches = ms.getTournamentMatchesByStage(tournamentId);
        long maxStage = matches != null ? matches.keySet().stream().max(Integer::compareTo).orElse(0) : 0L;

        int groups = ps.getTournamentGroups(tournamentId);

        if(optionalTournament.isPresent()) {
            Tournament t = optionalTournament.get();
            boolean hasFormErrors = mav.getModel().containsKey(
                    BindingResult.MODEL_KEY_PREFIX + "editTournamentForm"
            );
            if (!hasFormErrors) {
                if(!t.getTournamentStarted()){
                    editTournamentForm.setStartDate(t.getStartDate());
                    editTournamentForm.setMaxParticipants(t.getMaxParticipants());
                }
                if(!t.getFinished()) {
                    editTournamentForm.setName(t.getName());
                    editTournamentForm.setEndDate(t.getEndDate());
                    editTournamentForm.setServerName(t.getServerName());
                    editTournamentForm.setServerPassword(t.getServerPassword());
                    editTournamentForm.setDiscordChannel(t.getDiscordChannel());
                }
            }
            GameFormat gameFormat = gs.findFormatById(t.getFormatId());
            mav.addObject("format", gameFormat);
            t.setFormat(gameFormat.getName());
            List<Participant> participants = ps.getTournamentParticipants(tournamentId, gameFormat.getPlayersPerTeam());
            int participantCount = participants.size();
            Game game = gs.findById(t.getGameId()).orElseThrow(GameNotFoundException::new);
            User creator = us.findById(t.getCreatorId()).orElseThrow(UserNotFoundException::new);
            Boolean isIndividualTournament = gameFormat.getPlayersPerTeam() == 1;
            Boolean isParticipant = user != null && ps.hasJoined(user.getId(), tournamentId);
            Boolean hasRankedTournament = user != null && ps.participantHasRatedTournament(user.getId(), tournamentId);
            Float creatorRating = us.getUserRating(t.getCreatorId());
            if (user != null && !isIndividualTournament && !isParticipant){
                mav.addObject("userTeams", tms.getUserTeamsBySizeNotInTournament(user.getId(), tournamentId));
            }
            mav.addObject("isIndividualTournament", isIndividualTournament);
            mav.addObject("isParticipant", isParticipant);
            mav.addObject("participants", participants);
            mav.addObject("user", user);
            mav.addObject("tournament", t);
            mav.addObject("game", game);
            mav.addObject("creator", creator);
            mav.addObject("matches", matches);
            mav.addObject("groups", groups);
            mav.addObject("LEAGUE", Structure.LEAGUE);
            mav.addObject("ELIMINATION", Structure.ELIMINATION);
            mav.addObject("HYBRID", Structure.HYBRID);
            mav.addObject("tournamentWinner", t.getTournamentWinner());
            mav.addObject("participantCount", participantCount);
            mav.addObject("maxStage", maxStage);
            mav.addObject("editTournamentForm", editTournamentForm);
            mav.addObject("joinTeamForm", joinTournamentTeamForm);
            mav.addObject("creatorRating", creatorRating);
            mav.addObject("hasRankedTournament", hasRankedTournament);
        }
        return mav;
    }

    @RequestMapping(value = "/tournament/join/step1", method = { RequestMethod.POST })
    public ModelAndView handleStep1(
            @Validated(JoinTournamentTeamForm.StepOne.class)
            @ModelAttribute("joinTeamForm") JoinTournamentTeamForm form,
            BindingResult br,
            @RequestParam long tournamentId,
            @ModelAttribute("user") Optional<PawUserDetails> currentUser
        ) {
        ModelAndView mav = tournamentPage(currentUser, tournamentId, new EditTournamentForm(), form, new SetMatchResultsForm());
        if (br.hasErrors()) {
            mav.addObject("openModal", "'chooseTeamModal'");
        }else{
            mav.addObject("openModal", "'chooseTeamMembersModal'");
            mav.addObject("teamMembers", tms.getTeamMembers(form.getTeamId()));
            mav.addObject("selectedTeamId", form.getTeamId());
        }
        return mav;
    }

    @RequestMapping(value = "/tournament/join/step2", method = { RequestMethod.POST })
    public ModelAndView handleStep2(
            @Validated(JoinTournamentTeamForm.StepTwo.class)
            @ModelAttribute("joinTeamForm") JoinTournamentTeamForm form,
            BindingResult br,
            @RequestParam long tournamentId,
            @ModelAttribute("user") Optional<PawUserDetails> currentUser
        ) {
        if (br.hasErrors()) {
            ModelAndView mav = tournamentPage(currentUser, tournamentId, new EditTournamentForm(), form, new SetMatchResultsForm());
            mav.addObject("openModal", "'chooseTeamMembersModal'");
            mav.addObject("teamMembers", tms.getTeamMembers(form.getTeamId()));
            mav.addObject("selectedTeamId", form.getTeamId());
            return mav;
        }
        ps.joinTournamentTeam(form.getTournamentId(), form.getTeamId(), form.getMembers());
        return new ModelAndView("redirect:/tournament/" + tournamentId);
    }

    @RequestMapping(value = "/tournament/join", method = { RequestMethod.POST })
    public ModelAndView joinTournament(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @RequestParam("tournamentId") final long tournamentId) {
        User user = currentUser.orElseThrow(UserNotAuthenticatedException::new).getPawUser(); // aca se manda una excepcion, pero no deberia llegar por Spring Security.
        ps.joinTournamentUser(user.getId(), tournamentId);
        return new ModelAndView("redirect:/tournament/" + tournamentId);
    }

    @RequestMapping(value = "/tournament/leave", method = { RequestMethod.POST })
    public ModelAndView leaveTournament(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @RequestParam("tournamentId") final long tournamentId) {
        User user = currentUser.orElseThrow(UserNotAuthenticatedException::new).getPawUser(); // idem a /tournament/join
        ps.leaveTournament(user.getId(), tournamentId);
        ts.notifyCreatorOfLeavingUser(user, tournamentId);
        return new ModelAndView("redirect:/tournament/" + tournamentId);
    }

    @RequestMapping(value = "/tournament/removeParticipant", method = { RequestMethod.POST })
    public ModelAndView removeTournamentParticipant(@ModelAttribute("removeParticipantForm") RemoveParticipantForm form,
                                        @ModelAttribute("user") Optional<PawUserDetails> currentUser) {

        ps.removeParticipant(form.getTournamentId(), form.getParticipantId());

        String redirect = UriComponentsBuilder
                .fromPath("/tournament/{id}")
                .queryParam("section", "participantsTab")
                .buildAndExpand(form.getTournamentId())
                .toUriString();

        return new ModelAndView("redirect:" + redirect);
    }

    @RequestMapping(value = "/tournament/closeInscriptions", method = { RequestMethod.POST })
    public ModelAndView closeInscriptions(@RequestParam("tournamentId") final long tournamentId) {
        ts.closeInscriptions(tournamentId);
        return new ModelAndView("redirect:/tournament/" + tournamentId);
    }

    @RequestMapping(value = "/tournament/startTournament", method = { RequestMethod.POST })
    public ModelAndView startTournament(@RequestParam("tournamentId") final long tournamentId) {
        ts.startTournament(tournamentId);
        return new ModelAndView("redirect:/tournament/" + tournamentId);
    }

    @RequestMapping(value = "/tournament/setMatchResults", method = { RequestMethod.POST })
    public ModelAndView setMatchResults(@Valid @ModelAttribute("setMatchResultsForm") SetMatchResultsForm form,
                                        BindingResult br,
                                        @RequestParam(value = "group", required = false) Integer group,
                                        @ModelAttribute("user") Optional<PawUserDetails> currentUser) {

        if (br.hasErrors()) {
            EditTournamentForm editForm = new EditTournamentForm();
            JoinTournamentTeamForm joinForm = new JoinTournamentTeamForm();

            ModelAndView mav = tournamentPage(currentUser, form.getTournamentId(), editForm, joinForm, form);
            mav.addObject("editTournamentForm", editForm);
            mav.addObject("joinTeamForm", joinForm);
            mav.addObject("openModal", "openSetMatchModal(" + form.getMatchId() + ")");

            return mav;
        }

        ms.setMatchResults(form.getMatchId(), form.getTournamentId(), form.getLocalScore(), form.getVisitorScore());

        String redirect = UriComponentsBuilder
                .fromPath("/tournament/{id}")
                .queryParam("section", "matchesTab")
                .queryParamIfPresent("group", Optional.ofNullable(group))
                .buildAndExpand(form.getTournamentId())
                .toUriString();

        return new ModelAndView("redirect:" + redirect);
    }

    @RequestMapping("/tournaments/new/step1")
    public ModelAndView newTournamentFormStep1(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @ModelAttribute("tournamentForm") final TournamentForm form){
        final ModelAndView mav = new ModelAndView("tournamentForm");

        User user = currentUser.orElseThrow(UserNotAuthenticatedException::new).getPawUser(); // idem a /tournament/join
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
        List<GameFormat> formats = gs.getFormats(form.getGameId());
        Integer playersPerTeamMax = formats.stream()
        .map(GameFormat::getPlayersPerTeam)
        .filter(java.util.Objects::nonNull)
        .max(Integer::compareTo)
        .orElse(1);

        User user = currentUser.orElseThrow(UserNotAuthenticatedException::new).getPawUser(); // idem a /tournament/join
        mav.addObject("user", user);
        mav.addObject("step", 2);
        mav.addObject("formats", formats);
        mav.addObject("playersPerTeamMax", playersPerTeamMax);
        Map<Elo, String> elosMap = Arrays.stream(Elo.values())
                .collect(Collectors.toMap(
                        elo -> elo,
                        elo -> messageSource.getMessage("elo." + elo.name(), null, LocaleContextHolder.getLocale()),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        mav.addObject("elos", elosMap);
        return mav;
    }

    @RequestMapping(value = "/tournaments/new/step2", method = RequestMethod.POST)
    public ModelAndView validateStep2(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @Validated(TournamentForm.StepTwo.class) @ModelAttribute("tournamentForm") TournamentForm form, BindingResult result) {
        try {
            if (form.getImage() != null && !form.getImage().isEmpty()) {
                form.setImageBytes(form.getImage().getBytes());
            }else {
                return newTournamentFormStep2(currentUser, form);
            }
        } catch (IOException e) {
            result.rejectValue("image", "error.tournamentForm.invalidImage", e.getMessage());
            return newTournamentFormStep2(currentUser, form);
        }
        if (result.hasErrors()) {
            return newTournamentFormStep2(currentUser, form);
        } else {
            return newTournamentFormStep3(currentUser, form);
        }
    }

    @RequestMapping(value = "/tournaments/new/step3", method = { RequestMethod.GET })
    public ModelAndView newTournamentFormStep3(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @ModelAttribute("tournamentForm") final TournamentForm form){
        final ModelAndView mav = new ModelAndView("tournamentForm");

        User user = currentUser.orElseThrow(UserNotAuthenticatedException::new).getPawUser();
        mav.addObject("user", user);
        mav.addObject("step", 3);

        return mav;
    }

    @RequestMapping(value = "/tournaments/new/step3", method = { RequestMethod.POST })
    public ModelAndView createTournament(@ModelAttribute("user") Optional<PawUserDetails> currentUser,
                                         @Validated({TournamentForm.StepOne.class, TournamentForm.StepTwo.class, TournamentForm.StepThree.class}) @ModelAttribute("tournamentForm") final TournamentForm form,
                                         final BindingResult result,
                                         SessionStatus status) {
        if (result.hasErrors()) {
            return newTournamentFormStep3(currentUser, form);
        }
        User user = currentUser.orElseThrow(UserNotAuthenticatedException::new).getPawUser();

        byte[] rulesBytes = null;
        try {
            if (form.getRules() != null && !form.getRules().isEmpty()) {
                rulesBytes = form.getRules().getBytes();
            }
        } catch (IOException e) {
            result.rejectValue("rules", "error.tournamentForm.invalidRules", e.getMessage());
            return newTournamentFormStep3(currentUser, form);
        }

        System.out.println("ELO del form: '" + form.getElo() + "'");

        final Tournament t = ts.create(user.getId(), form.getName(), form.getGameId(),
                form.getRegion(), form.getElo(), form.getStartDate(), form.getEndDate(),
                null, form.getStructure(), form.getMaxParticipants(), form.getImageBytes(), true, false, form.getFormatId(),
                rulesBytes, form.getServerName(), form.getServerPassword(), form.getDiscordChannel());
        status.setComplete();
        return new ModelAndView("redirect:/tournament/" + t.getId());
    }

    @RequestMapping(value = "/tournament/contactOwner", method = { RequestMethod.POST })
    public ModelAndView contactTournamentOwner(@Valid @ModelAttribute("contactOwnerForm") final ContactOwnerForm contactOwnerForm,
                                               BindingResult result,
                                               @ModelAttribute("user") Optional<PawUserDetails> currentUser) {
        ModelAndView mav = tournamentPage(currentUser, contactOwnerForm.getTournamentId(), getEditTournamentForm(), getJoinTournamentTeamForm(), getSetMatchResultsForm());
        if(result.hasErrors()) {
            mav.addObject("openModal", "'contactOwnerModal'");
            return mav;
        }
        User user = currentUser.orElseThrow(UserNotAuthenticatedException::new).getPawUser(); // idem a /tournament/join
        ts.contactOwner(contactOwnerForm.getTournamentId(), user, contactOwnerForm.getSubject(), contactOwnerForm.getBody(), contactOwnerForm.getCreatorId());
        return mav;
    }

    @RequestMapping(value = "/tournament/rate", method = { RequestMethod.POST })
    public ModelAndView rateTournamentOwner(@Valid @ModelAttribute("rateTournamentForm") final RateTournamentForm rateTournamentForm,
                                            BindingResult result,
                                            @ModelAttribute("user") Optional<PawUserDetails> currentUser) {
        ModelAndView mav = tournamentPage(currentUser, rateTournamentForm.getTournamentId(), getEditTournamentForm(), getJoinTournamentTeamForm(), getSetMatchResultsForm());
        if(result.hasErrors()) {
            mav.addObject("openModal", "'rateTournamentModal'");
            return mav;
        }
        ts.updateTournamentRating(rateTournamentForm.getTournamentId(), rateTournamentForm.getRating());
        ps.updateCreatorRating(rateTournamentForm.getTournamentId(), rateTournamentForm.getCreatorId(), currentUser.orElseThrow(UserNotAuthenticatedException::new).getPawUser().getId(), rateTournamentForm.getRating());
        return new ModelAndView("redirect:/tournament/" + rateTournamentForm.getTournamentId());
    }
}