package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.form.EmailForm;
import ar.edu.itba.paw.webapp.form.ResetPasswordForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final UserService us;
    private final PawUserDetailsService userDetailsService;

    public AuthController(final UserService us, final PawUserDetailsService userDetailsService) {
        this.us = us;
        this.userDetailsService = userDetailsService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView register(@Valid @ModelAttribute("registerForm") UserForm form, final BindingResult result) {
        if (result.hasErrors()) {
            return registerPage(form);
        }
        User user = us.create(form.getUsername(), form.getEmail(), form.getPassword());
        return new ModelAndView("redirect:/verify?userId=" + user.getId());
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView registerPage(@ModelAttribute("registerForm") UserForm form) {
        return new ModelAndView("registerPage");
    }


    @RequestMapping(value = "/login")
    public ModelAndView login(@RequestParam(value = "error", required = false) String error) {
        ModelAndView mav = new ModelAndView("loginPage");
        if(error != null) {
            mav.addObject("invalidCredentials", true);
        }
        return mav;
    }

    @RequestMapping("/logout")
    public ModelAndView logout() {
        return new ModelAndView("redirect:/logout");
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public ModelAndView resendVerification(@RequestParam("userId") long userId) {
        User user = us.findById(userId).orElseThrow(UserNotFoundException::new);
        us.resendVerification(user);
        return new ModelAndView("redirect:/verify?userId=" + userId);
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public ModelAndView verifyPage(@RequestParam("userId") long userId) {
        ModelAndView mav = new ModelAndView("verificationPage");
        User user = us.findById(userId).orElseThrow(UserNotFoundException::new);
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping("/verify/confirm")
    public ModelAndView confirmedVerificationPage(@RequestParam("token") long token,
                                                  @RequestParam("userId") long userId,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("confirmedVerificationPage");

        boolean validToken = us.verifyEmail(token, userId);
        mav.addObject("validToken", validToken);
        mav.addObject("userId", userId);

        if (validToken) {
            authenticateUser(userId, request, response);
        }

        return mav;
    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
    public ModelAndView forgotPasswordPage(@ModelAttribute("emailForm") EmailForm emailForm) {
        ModelAndView mav = new ModelAndView("forgotPassword");
        mav.addObject("emailForm", emailForm);
        return mav;
    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public ModelAndView forgotPassword(@Valid @ModelAttribute("emailForm") EmailForm emailForm, BindingResult result) {
        if(result.hasErrors()) {
            return forgotPasswordPage(emailForm);
        }
        us.requestPasswordReset(emailForm.getEmail());
        return new ModelAndView("redirect:/forgotPassword/request");
    }

    @RequestMapping("/forgotPassword/request")
    public ModelAndView requestPasswordResetPage() {
        return new ModelAndView("requestPasswordReset");
    }

    @RequestMapping(value = "/forgotPassword/reset", method = RequestMethod.GET)
    public ModelAndView resetPasswordPage(@RequestParam("token") long token,
                                          @ModelAttribute("resetPasswordForm") ResetPasswordForm resetPasswordForm) {
        ModelAndView mav = new ModelAndView("resetPasswordPage");
        Optional<Token> validToken = us.checkTokenValidity(token);
        if(validToken.isPresent()) {
            User user = us.findUserByToken(token);
            mav.addObject("userId", user.getId());
        }
        mav.addObject("resetPasswordForm", resetPasswordForm);
        mav.addObject("token", token);
        mav.addObject("validToken", validToken.isPresent());
        return mav;
    }

    @RequestMapping(value = "/forgotPassword/reset", method = RequestMethod.POST)
    public ModelAndView resetPassword(@RequestParam("token") long token,
                                      @Valid @ModelAttribute("resetPasswordForm") ResetPasswordForm resetPasswordForm,
                                      BindingResult result,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        if(result.hasErrors()) {
            LOGGER.debug("The password pair {} and {} is not valid", resetPasswordForm.getPassword(), resetPasswordForm.getRepeatPassword());
            return resetPasswordPage(token, resetPasswordForm);
        }
        long userId = us.findUserByToken(token).getId();
        boolean resetPasswordSuccess = us.resetPassword(token, resetPasswordForm.getPassword());
        if(resetPasswordSuccess) {
            authenticateUser(userId, request, response);
        }
        return new ModelAndView("redirect:/forgotPassword/reset/success");
    }

    @RequestMapping("/forgotPassword/reset/success")
    public ModelAndView resetPasswordSuccess() {
        return new ModelAndView("resetPasswordSuccess");
    }

    private void authenticateUser(long userId, HttpServletRequest request, HttpServletResponse response){
        us.findById(userId).ifPresent(user -> {
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            request.changeSessionId();
            new HttpSessionSecurityContextRepository().saveContext(context, request, response);
        });
    }
}
