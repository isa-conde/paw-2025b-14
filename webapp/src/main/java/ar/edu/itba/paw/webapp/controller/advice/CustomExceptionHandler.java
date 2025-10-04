package ar.edu.itba.paw.webapp.controller.advice;

import ar.edu.itba.paw.interfaces.exception.GameNotFoundException;
import ar.edu.itba.paw.interfaces.exception.TournamentNotFoundException;
import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFoundException() {
        ModelAndView mav = new ModelAndView("error/exception");
        mav.addObject("message", "errorExceptionPage.userNotFound.description");
        mav.addObject("title", "errorExceptionPage.userNotFound.title");
        return mav;
    }

    @ExceptionHandler(TournamentNotFoundException.class)
    public ModelAndView handleTournamentNotFoundException() {
        ModelAndView mav = new ModelAndView("error/exception");
        mav.addObject("message", "errorExceptionPage.tournamentNotFound.description");
        mav.addObject("title", "errorExceptionPage.tournamentNotFound.title");
        return mav;
    }

    @ExceptionHandler(GameNotFoundException.class)
    public ModelAndView handleGameNotFoundException() {
        ModelAndView mav = new ModelAndView("error/exception");
        mav.addObject("message", "errorExceptionPage.gameNotFound.description");
        mav.addObject("title", "errorExceptionPage.gameNotFound.title");
        return mav;
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class
    })
    public ModelAndView handleBadRequestException() {
        ModelAndView mav = new ModelAndView("error/exception");
        mav.addObject("message", "error400Page.description");
        mav.addObject("title", "error400Page.title");
        return mav;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNotFoundException() {
        ModelAndView mav = new ModelAndView("error/exception");
        mav.addObject("message", "error404Page.description");
        mav.addObject("title", "error404Page.title");
        return mav;
    }

}
