package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.auth.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class ErrorController {

    @RequestMapping("/403")
    public ModelAndView forbidden(@ModelAttribute("user") Optional<UserDetails> currentUser) {
        ModelAndView mav = new ModelAndView("error/403");
        if(currentUser.isPresent()) {
            mav.addObject("user", currentUser.get().getUser());
        } else {
            mav.addObject("user", null);
        }
        return mav;
    }
}
