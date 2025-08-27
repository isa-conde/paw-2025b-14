package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.GreetingService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.tags.Param;

@Controller
public class HelloWorldController {

    private final UserService us;

    public HelloWorldController(final UserService us) {
        this.us = us;
    }

    @RequestMapping("/")
    public ModelAndView helloWorld(@RequestParam(name = "userId", required = false, defaultValue = "1") final int userId) {
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("user", us.findById(userId).get());
        return mav;
    }

    @RequestMapping("/create")
    public ModelAndView profile(@RequestParam("username") final String username) {
        User newUser = us.create(username);
        final ModelAndView mav = new ModelAndView("redirect:/?userId = " + newUser.getId());
        return mav;
    }
}
