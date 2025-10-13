package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.webapp.auth.PawUserDetails;
import ar.edu.itba.paw.webapp.form.CreateTeamForm;
import ar.edu.itba.paw.webapp.form.EditProfileForm;
import ar.edu.itba.paw.webapp.form.EditTeamForm;
import ar.edu.itba.paw.webapp.form.TournamentForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
    public ModelAndView teamForm(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @ModelAttribute("teamForm") CreateTeamForm form){
        ModelAndView mav = new ModelAndView("createTeam");

        if (currentUser.isPresent()) {
            User user = currentUser.get().getPawUser();
            mav.addObject("user", user);
        }

        return mav;
    }

    @RequestMapping(value = "/team/create", method = { RequestMethod.POST })
    public ModelAndView createTeam(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @Valid @ModelAttribute("teamForm") CreateTeamForm form, final BindingResult result){
        if (result.hasErrors()){
            return teamForm(currentUser, form);
        }

        if (currentUser.isPresent()) {
            User user = currentUser.get().getPawUser();
            byte[] pfpBytes = null;
            try {
                if (form.getPfp() != null && !form.getPfp().isEmpty()) {
                    pfpBytes = form.getPfp().getBytes();
                }
            } catch (IOException e) {
                result.rejectValue("pfp", "error.tournamentForm.invalidImage");
            }
            byte[] bannerBytes = null;
            try {
                if (form.getBanner() != null && !form.getBanner().isEmpty()) {
                    bannerBytes = form.getBanner().getBytes();
                }
            } catch (IOException e) {
                result.rejectValue("banner", "error.tournamentForm.invalidImage");
            }
            Team team = ts.create(form.getName(), pfpBytes, bannerBytes, user.getId(), form.getMembers());
            return new ModelAndView("redirect:/team/profile/" + team.getId());
        }
        return null;
    }

    @RequestMapping("/team/profile/{id}")
    public ModelAndView teamProfile(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @PathVariable Long id, @ModelAttribute("teamForm") EditTeamForm editTeamForm){
        final ModelAndView mav = new ModelAndView("teamProfile");

        if (currentUser.isPresent()) {
            User user = currentUser.get().getPawUser();
            mav.addObject("user", user);
        }

        Optional<Team> optionalTeam = ts.getById(id);
        if (optionalTeam.isEmpty()){
            return new ModelAndView("redirect:/");
        }

        Team team = optionalTeam.get();
        mav.addObject("team", team);
        mav.addObject("owner", us.findById(team.getOwner().getId()).get());
        mav.addObject("pastTournaments", ts.getPastTournaments(id));
        mav.addObject("activeTournaments", ts.getActiveTournaments(id));
        mav.addObject("teamForm", editTeamForm);
        mav.addObject("members", ts.getTeamMembers(id));
        editTeamForm.setName(team.getName());
        return mav;
    }

    @RequestMapping(value = "/team/update", method = { RequestMethod.POST })
    public ModelAndView updateProfile(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @RequestParam("teamId") final long teamId, @Valid @ModelAttribute("teamForm") final EditTeamForm form, final BindingResult result){
        if (currentUser.isPresent() && result.hasErrors()) {
            ModelAndView mav = teamProfile(currentUser, teamId, form);
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
