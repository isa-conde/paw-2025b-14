package ar.edu.itba.paw.webapp.controller.advice;

import ar.edu.itba.paw.interfaces.exception.*;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class CustomExceptionHandler {

    private ModelAndView buildExceptionModelAndView(final String messageKey, final String titleKey) {
        ModelAndView mav = new ModelAndView("error/exception");
        mav.addObject("message", messageKey);
        mav.addObject("title", titleKey);
        return mav;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFoundException() {
        return buildExceptionModelAndView("errorExceptionPage.userNotFound.description", "errorExceptionPage.userNotFound.title");
    }

    @ExceptionHandler(TournamentNotFoundException.class)
    public ModelAndView handleTournamentNotFoundException() {
        return buildExceptionModelAndView("errorExceptionPage.tournamentNotFound.description", "errorExceptionPage.tournamentNotFound.title");
    }

    @ExceptionHandler(GameNotFoundException.class)
    public ModelAndView handleGameNotFoundException() {
        return buildExceptionModelAndView("errorExceptionPage.gameNotFound.description", "errorExceptionPage.gameNotFound.title");
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
        return buildExceptionModelAndView("errorExceptionPage.userAlreadyJoined.description", "errorExceptionPage.userAlreadyJoined.title");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNotFoundException() {
        return buildExceptionModelAndView("errorExceptionPage.tournamentAlreadyClosed.description", "errorExceptionPage.tournamentAlreadyClosed.title");
    }

    @ExceptionHandler(TournamentAlreadyClosedException.class)
    public ModelAndView handleTournamentAlreadyJoined() {
        return buildExceptionModelAndView("errorExceptionPage.tournamentAlreadyClosed.description", "errorExceptionPage.tournamentAlreadyClosed.title");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView handle405Error() {
        return new ModelAndView("error/405");
    }

    @ExceptionHandler(MatchWinnerAlreadySetException.class)
    public ModelAndView handleMatchWinnerAlreadySet() {
        return buildExceptionModelAndView("errorExceptionPage.matchWinnerAlreadySet.description", "errorExceptionPage.matchWinnerAlreadySet.title");
    }

    @ExceptionHandler(TournamentAlreadyStartedException.class)
    public ModelAndView handleTournamentAlreadyStartedException() {
        return buildExceptionModelAndView("errorExceptionPage.tournamentAlreadyStarted.description", "errorExceptionPage.tournamentAlreadyStarted.title");
    }

    @ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusinessException() {
        return buildExceptionModelAndView("errorExceptionPage.business.description", "errorExceptionPage.business.title");
    }

    @ExceptionHandler(DrawInEliminationMatchException.class)
    public ModelAndView handleDrawInEliminationMatchException() {
        return buildExceptionModelAndView("errorExceptionPage.drawInEliminationMatch.description", "errorExceptionPage.drawInEliminationMatch.title");
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ModelAndView handleEmailAlreadyUsedException() {
        return buildExceptionModelAndView("errorExceptionPage.emailAlreadyUsed.description", "errorExceptionPage.emailAlreadyUsed.title");
    }

    @ExceptionHandler(GameFormatNotFoundException.class)
    public ModelAndView handleGameFormatNotFoundException() {
        return buildExceptionModelAndView("errorExceptionPage.gameFormatNotFound.description", "errorExceptionPage.gameFormatNotFound.title");
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ModelAndView handleImageNotFoundException() {
        return buildExceptionModelAndView("errorExceptionPage.imageNotFound.description", "errorExceptionPage.imageNotFound.title");
    }

    @ExceptionHandler(InvalidCommentException.class)
    public ModelAndView handleInvalidCommentException() {
        return buildExceptionModelAndView("errorExceptionPage.invalidComment.description", "errorExceptionPage.invalidComment.title");
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ModelAndView handleInvalidTokenException() {
        return buildExceptionModelAndView("errorExceptionPage.invalidToken.description", "errorExceptionPage.invalidToken.title");
    }

    @ExceptionHandler(MatchNotFoundException.class)
    public ModelAndView handleMatchNotFoundException() {
        return buildExceptionModelAndView("errorExceptionPage.matchNotFound.description", "errorExceptionPage.matchNotFound.title");
    }

    @ExceptionHandler(MissingGroupNumberException.class)
    public ModelAndView handleMissingGroupNumberException() {
        return buildExceptionModelAndView("errorExceptionPage.missingGroupNumber.description", "errorExceptionPage.missingGroupNumber.title");
    }

    @ExceptionHandler(MissingWinnerException.class)
    public ModelAndView handleMissingWinnerException() {
        return buildExceptionModelAndView("errorExceptionPage.missingWinner.description", "errorExceptionPage.missingWinner.title");
    }

    @ExceptionHandler(NameAlreadyUsedException.class)
    public ModelAndView handleNameAlreadyUsedException() {
        return buildExceptionModelAndView("errorExceptionPage.nameAlreadyUsed.description", "errorExceptionPage.nameAlreadyUsed.title");
    }

    @ExceptionHandler(ParticipantAlreadyRatedException.class)
    public ModelAndView handleParticipantAlreadyRatedException() {
        return buildExceptionModelAndView("errorExceptionPage.participantAlreadyRated.description", "errorExceptionPage.participantAlreadyRated.title");
    }

    @ExceptionHandler(ParticipantNotFoundException.class)
    public ModelAndView handleParticipantNotFoundException() {
        return buildExceptionModelAndView("errorExceptionPage.participantNotFound.description", "errorExceptionPage.participantNotFound.title");
    }

    @ExceptionHandler(ParticipantNotInMatchException.class)
    public ModelAndView handleParticipantNotInMatchException() {
        return buildExceptionModelAndView("errorExceptionPage.participantNotInMatch.description", "errorExceptionPage.participantNotInMatch.title");
    }

    @ExceptionHandler(RulesNotFoundException.class)
    public ModelAndView handleRulesNotFoundException() {
        return buildExceptionModelAndView("errorExceptionPage.rulesNotFound.description", "errorExceptionPage.rulesNotFound.title");
    }

    @ExceptionHandler(ScoresInvalidException.class)
    public ModelAndView handleScoresInvalidException() {
        return buildExceptionModelAndView("errorExceptionPage.scoresInvalid.description", "errorExceptionPage.scoresInvalid.title");
    }

    @ExceptionHandler(SettingWinnerForTBDException.class)
    public ModelAndView handleSettingWinnerForTBDException() {
        return buildExceptionModelAndView("errorExceptionPage.settingWinnerForTBD.description", "errorExceptionPage.settingWinnerForTBD.title");
    }

    @ExceptionHandler(StageIsNotSetException.class)
    public ModelAndView handleStageIsNotSetException() {
        return buildExceptionModelAndView("errorExceptionPage.stageIsNotSet.description", "errorExceptionPage.stageIsNotSet.title");
    }

    @ExceptionHandler(TeamNotFoundException.class)
    public ModelAndView handleTeamNotFoundException() {
        return buildExceptionModelAndView("errorExceptionPage.teamNotFound.description", "errorExceptionPage.teamNotFound.title");
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ModelAndView handleTokenNotFoundException() {
        return buildExceptionModelAndView("errorExceptionPage.tokenNotFound.description", "errorExceptionPage.tokenNotFound.title");
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ModelAndView handleUserNotAuthenticatedException() {
        return buildExceptionModelAndView("errorExceptionPage.userNotAuthenticated.description", "errorExceptionPage.userNotAuthenticated.title");
    }

    @ExceptionHandler(UsernameAlreadyUsedException.class)
    public ModelAndView handleUsernameAlreadyUsedException() {
        return buildExceptionModelAndView("errorExceptionPage.usernameAlreadyUsed.description", "errorExceptionPage.usernameAlreadyUsed.title");
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralException() {
        ModelAndView mav = new ModelAndView("error/exception");
        mav.addObject("message", "errorExceptionPage.general.description");
        mav.addObject("title", "errorExceptionPage.general.title");
        return mav;
    }

}
