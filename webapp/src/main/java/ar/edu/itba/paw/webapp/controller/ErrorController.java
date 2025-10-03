package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.auth.PawUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class ErrorController {

    @Autowired
    private UserService us;

    @RequestMapping("/403")
    public ModelAndView forbidden(@ModelAttribute("user") Optional<PawUserDetails> currentUser) {
        ModelAndView mav = new ModelAndView("error/403");
        if(currentUser.isPresent()) {
            mav.addObject("user", currentUser.get().getPawUser());
        } else {
            mav.addObject("user", null);
        }
        return mav;
    }
}
