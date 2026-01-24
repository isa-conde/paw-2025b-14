package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exception.TeamNotFoundException;
import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.auth.UserDetails;
import ar.edu.itba.paw.webapp.form.CreateTeamForm;
import ar.edu.itba.paw.webapp.form.EditTeamForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@Controller
public class TeamController {

    UserService us;
    TeamService ts;

    public TeamController(final UserService us, final TeamService ts){
        this.us = us;
        this.ts = ts;
    }

    @RequestMapping("/team/create")
    public ModelAndView teamForm(@ModelAttribute("user") Optional<UserDetails> currentUser,
                                 @ModelAttribute("teamForm") CreateTeamForm form,
                                 @RequestParam(value = "returnUrl", required = false) String returnUrl) {
        ModelAndView mav = new ModelAndView("createTeam");

        currentUser.ifPresent(u -> mav.addObject("user", u.getUser()));
        if (returnUrl != null) {
            mav.addObject("returnUrl", returnUrl);
        }
        return mav;
    }

    @RequestMapping(value = "/team/create", method = { RequestMethod.POST })
    public ModelAndView createTeam(@ModelAttribute("user") Optional<UserDetails> currentUser,
                                   @Valid @ModelAttribute("teamForm") CreateTeamForm form,
                                   final BindingResult result,
                                   HttpServletRequest request) {
        if (result.hasErrors()) {
            return teamForm(currentUser, form, form.getReturnUrl());
        }

        if (currentUser.isPresent()) {
            User user = currentUser.get().getUser();

            byte[] pfpBytes = null;
            try {
                if (form.getPfp() != null && !form.getPfp().isEmpty()) {
                    pfpBytes = form.getPfp().getBytes();
                }
            } catch (IOException e) {
                result.rejectValue("pfp", "error.tournamentForm.invalidImage");
                return teamForm(currentUser, form, form.getReturnUrl());
            }

            byte[] bannerBytes = null;
            try {
                if (form.getBanner() != null && !form.getBanner().isEmpty()) {
                    bannerBytes = form.getBanner().getBytes();
                }
            } catch (IOException e) {
                result.rejectValue("banner", "error.tournamentForm.invalidImage");
                return teamForm(currentUser, form, form.getReturnUrl());
            }

            Team team = ts.create(form.getName(), pfpBytes, bannerBytes, user.getId(), form.getMembers());

            String returnUrl = form.getReturnUrl();
            if (returnUrl == null) {
                returnUrl = request.getParameter("returnUrl");
            }

            if (isSafeInternalRedirect(request, returnUrl)) {
                return new ModelAndView("redirect:" + returnUrl);
            }

            return new ModelAndView("redirect:/team/profile/" + team.getId());
        }
        return teamForm(currentUser, form, form.getReturnUrl());
    }

    private boolean isSafeInternalRedirect(HttpServletRequest request, String returnUrl) {
        if (returnUrl == null || returnUrl.isBlank()) return false;

        String lower = returnUrl.toLowerCase();
        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            return false;
        }

        String ctx = request.getContextPath();
        if (ctx != null && !ctx.isEmpty() && returnUrl.startsWith(ctx + "/")) {
            return true;
        }
        return returnUrl.startsWith("/");
    }

    @RequestMapping("/team/profile/{id}")
    public ModelAndView teamProfile(@ModelAttribute("user") Optional<UserDetails> currentUser,
                                    @PathVariable long id,
                                    @ModelAttribute("teamForm") EditTeamForm editTeamForm,
                                    @RequestParam(defaultValue = "0") int page1,
                                    @RequestParam(defaultValue = "0") int page2){
        final ModelAndView mav = new ModelAndView("teamProfile");

        if (currentUser.isPresent()) {
            User user = currentUser.get().getUser();
            mav.addObject("user", user);
        }

        Team team = ts.findById(id).orElseThrow(TeamNotFoundException::new);

        mav.addObject("team", team);
        mav.addObject("owner", us.findById(team.getOwner().getId()).orElseThrow(UserNotFoundException::new));
        mav.addObject("pastTournaments", ts.getPastTournaments(id, page2));
        mav.addObject("activeTournaments", ts.getActiveTournaments(id, page1));
        mav.addObject("teamForm", editTeamForm);
        mav.addObject("members", ts.getTeamMembers(id));
        mav.addObject("currentPage1", page1);
        mav.addObject("currentPage2", page2);
        mav.addObject("totalPages1", ts.getActivePages(id));
        mav.addObject("totalPages2", ts.getPastPages(id));
        editTeamForm.setName(team.getName());
        return mav;
    }

    @RequestMapping(value = "/team/update", method = { RequestMethod.POST })
    public ModelAndView updateProfile(@ModelAttribute("user") Optional<UserDetails> currentUser,
                                      @RequestParam("teamId") final long teamId,
                                      @Valid @ModelAttribute("teamForm") final EditTeamForm form,
                                      final BindingResult result){
        if (currentUser.isPresent() && result.hasErrors()) {
            ModelAndView mav = teamProfile(currentUser, teamId, form, 0, 0);
            mav.addObject("openModal", "'editProfileModal'");
            return mav;
        }

        boolean isValid = true;

        byte[] pfpBytes = null;
        try {
            if (form.getProfilePicture() != null && !form.getProfilePicture().isEmpty()) {
                pfpBytes = form.getProfilePicture().getBytes();
            }
        } catch (IOException e) {
            isValid = false;
            result.rejectValue("profilePicture", "error.tournamentForm.invalidImage");
        }
        byte[] bannerBytes = null;
        try {
            if (form.getBannerPicture() != null && !form.getBannerPicture().isEmpty()) {
                bannerBytes = form.getBannerPicture().getBytes();
            }
        } catch (IOException e) {
            isValid = false;
            result.rejectValue("bannerPicture", "error.tournamentForm.invalidImage");
        }
        if (isValid){
            ts.updateTeam(form.getTeamId(), form.getName(), pfpBytes, bannerBytes, form.getMembers());
        }
        return new ModelAndView("redirect:/team/profile/" + form.getTeamId() );
    }

}
