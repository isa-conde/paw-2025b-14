package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.GameService;
import ar.edu.itba.paw.interfaces.services.GreetingService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Game;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.webapp.form.GameForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.tags.Param;

import javax.validation.Valid;

@Controller
public class HelloWorldController {

    private final UserService us;
    private final GameService gs;

    public HelloWorldController(final UserService us, final GameService gs) {
        this.us = us;
        this.gs = gs;
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

    @RequestMapping(value = "/game/create", method = { RequestMethod.POST })
    public ModelAndView createGame(@Valid @ModelAttribute("gameForm") final GameForm form, final BindingResult errors) {
        if (errors.hasErrors()) {
            return createGameForm(form);
        }
        final Game g = gs.create(form.getName(), form.getGenre());
        return new ModelAndView("redirect:/user?userId=" + g.getId());
    }

    @RequestMapping(value = "/game/create", method = {RequestMethod.GET})
    public ModelAndView createGameForm(@ModelAttribute("gameForm") final GameForm form){
        ModelAndView mav = new ModelAndView("addGame");
        mav.addObject("genres", Genre.values()); // 🔹 paso el enum a la vista
        return mav;
    }


}
