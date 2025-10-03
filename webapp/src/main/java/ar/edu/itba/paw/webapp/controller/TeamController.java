package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.form.CreateTeamForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
        if (isValid){
            ts.create(form.getName(), pfpBytes, bannerBytes, user.getId(), form.getMembers());
        }
        return new ModelAndView("redirect:/");
    }

}
