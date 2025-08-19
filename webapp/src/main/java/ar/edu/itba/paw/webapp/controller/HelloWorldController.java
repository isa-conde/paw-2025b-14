package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.GreetingService;
import ar.edu.itba.paw.interfaces.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HelloWorldController {

    private final GreetingService greetingService;

    public HelloWorldController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @RequestMapping("/")
    public ModelAndView helloWorld() {
        String greeting = greetingService.greet("bruno");
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("greeting", greeting);
        return mav;
    }
}
