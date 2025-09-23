package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    public ModelAndView forbidden(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/403");
        String username = request.getUserPrincipal().getName();
        Optional<User> user = us.findByUsername(username);
        if(user.isPresent()) {
            mav.addObject("user", user.get());
        } else {
            mav.addObject("user", null);
        }
        return mav;
    }
}
