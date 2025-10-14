package ar.edu.itba.paw.webapp.controller.advice;

import ar.edu.itba.paw.interfaces.exception.*;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            TypeMismatchException.class
    })
    public ModelAndView handleBadRequestException() {
        ModelAndView mav = new ModelAndView("error/exception");
        mav.addObject("message", "error400Page.description");
        mav.addObject("title", "error400Page.title");
        return mav;
    }

    @ExceptionHandler(UserAlreadyJoinedException.class)
    public ModelAndView handleUserAlreadyJoinedException() {
        ModelAndView mav = new ModelAndView("error/exception");
        mav.addObject("message", "errorExceptionPage.userAlreadyJoined.description");
        mav.addObject("title", "errorExceptionPage.userAlreadyJoined.title");
        return mav;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNotFoundException() {
        ModelAndView mav = new ModelAndView("error/exception");
        mav.addObject("message", "error404Page.description");
        mav.addObject("title", "error404Page.title");
        return mav;
    }

    @ExceptionHandler(TournamentAlreadyClosedException.class)
    public ModelAndView handleTournamentAlreadyJoined() {
        ModelAndView mav = new ModelAndView("error/exception");
        mav.addObject("message", "errorExceptionPage.tournamentAlreadyClosed.description");
        mav.addObject("title", "errorExceptionPage.tournamentAlreadyClosed.title");
        return mav;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView handle405Error() {
        return new ModelAndView("error/405");
    }

    @ExceptionHandler(MatchWinnerAlreadySetException.class)
    public ModelAndView handleMatchWinnerAlreadySet() {
        ModelAndView mav = new ModelAndView("error/exception");
        mav.addObject("message", "errorExceptionPage.matchWinnerAlreadySet.description");
        mav.addObject("title", "errorExceptionPage.matchWinnerAlreadySet.title");
        return mav;
    }

    @ExceptionHandler(TournamentAlreadyStartedException.class)
    public ModelAndView handleTournamentAlreadyStartedException() {
        ModelAndView mav = new ModelAndView("error/exception");
        mav.addObject("message", "errorExceptionPage.tournamentAlreadyStarted.description");
        mav.addObject("title", "errorExceptionPage.tournamentAlreadyStarted.title");
        return mav;
    }

//    @ExceptionHandler(Exception.class)
//    public ModelAndView handleGeneralException() {
//        ModelAndView mav = new ModelAndView("error/exception");
//        mav.addObject("message", "errorExceptionPage.general.description");
//        mav.addObject("title", "errorExceptionPage.general.title");
//        return mav;
//    }

}
