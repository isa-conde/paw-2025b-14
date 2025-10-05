package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.filters.TournamentFilter;
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
import java.security.Principal;
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
    public ModelAndView teamForm(Principal principal, @ModelAttribute("teamForm") CreateTeamForm form){
        ModelAndView mav = new ModelAndView("createTeam");

        User user = us.findByUsername(principal.getName()).orElse(null);

        mav.addObject("user", user);

        return mav;
    }

    @RequestMapping(value = "/team/create", method = { RequestMethod.POST })
    public ModelAndView createTeam(Principal principal, @Valid @ModelAttribute("teamForm") CreateTeamForm form, final BindingResult result){
        if (result.hasErrors()){
            return teamForm(principal, form);
        }

        User user = us.findByUsername(principal.getName()).orElse(null);

        Boolean isValid = true;

        byte[] pfpBytes = null;
        try {
            if (form.getPfp() != null && !form.getPfp().isEmpty()) {
                pfpBytes = form.getPfp().getBytes();
            }
        } catch (IOException e) {
            isValid = false;
            result.rejectValue("pfp", "error.tournamentForm.invalidImage");
        }
        byte[] bannerBytes = null;
        try {
            if (form.getBanner() != null && !form.getBanner().isEmpty()) {
                bannerBytes = form.getBanner().getBytes();
            }
        } catch (IOException e) {
            isValid = false;
            result.rejectValue("banner", "error.tournamentForm.invalidImage");
        }
        Team team = ts.create(form.getName(), pfpBytes, bannerBytes, user.getId(), form.getMembers());
        return new ModelAndView("redirect:/profile/team/" + team.getId());
    }

    @RequestMapping("/team/profile/{id}")
    public ModelAndView teamProfile(Principal principal, @PathVariable Long id, @ModelAttribute("teamForm") EditTeamForm editTeamForm){
        final ModelAndView mav = new ModelAndView("teamProfile");

        User user = us.findByUsername(principal.getName()).orElse(null);

        mav.addObject("user", user);

        Optional<Team> optionalTeam = ts.getById(id);
        if (optionalTeam.isEmpty()){
            return new ModelAndView("redirect:/");
        }

        Team team = optionalTeam.get();
        mav.addObject("team", team);
        mav.addObject("owner", us.findById(team.getOwner_id()).get());
        mav.addObject("pastTournaments", ts.getPastTournaments(team.getId()));
        mav.addObject("activeTournaments", ts.getActiveTournaments(team.getId()));
        editTeamForm.setName(team.getName());
        return mav;
    }

    @RequestMapping(value = "/team/update", method = { RequestMethod.POST })
    public ModelAndView updateProfile(Principal principal, @RequestParam("teamId") final long teamId, @Valid @ModelAttribute("EditTeamForm") final EditTeamForm form, final BindingResult result){
        User user = null;
        if (principal != null) {
            Optional<User> userOpt = us.findByUsername(principal.getName());
            user = userOpt.orElse(null);
        }
        if (user == null) {
            return new ModelAndView("redirect:/");
        }

        if (result.hasErrors()) {
            return new ModelAndView("redirect:/team/profile/" + teamId   );
        }

        Boolean isValid = true;

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
