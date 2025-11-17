package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.Constants;
import ar.edu.itba.paw.interfaces.exception.UserNotAuthenticatedException;
import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import ar.edu.itba.paw.interfaces.services.GameService;
import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Comment;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserAccount;
import ar.edu.itba.paw.model.enums.*;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.webapp.auth.PawUserDetails;
import ar.edu.itba.paw.webapp.form.CommentForm;
import ar.edu.itba.paw.webapp.form.AddAccountForm;
import ar.edu.itba.paw.webapp.form.EditProfileForm;
import ar.edu.itba.paw.webapp.form.FilterForm;
import ar.edu.itba.paw.webapp.form.TournamentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class UserController {

    private final GameService gs;
    private final UserService us;
    private final TournamentService ts;
    private final TeamService tms;

    private final static long DEFAULT_PAGE = 0L;

    @Autowired
    MessageSource messageSource;

    @ModelAttribute("commentForm")
    public CommentForm commentForm() {
        return new CommentForm();
    }

    public UserController(GameService gs, UserService us, TournamentService ts, TeamService tms) {
        this.gs = gs;
        this.us = us;
        this.ts = ts;
        this.tms = tms;
    }

    @RequestMapping("/")
    public ModelAndView index(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @ModelAttribute("tournamentForm") TournamentForm tournamentForm) {
        final ModelAndView mav = new ModelAndView("index");
        List<Game> games = gs.findAllPaged(0L);

        User loggedUser = null;
        if(currentUser.isPresent()) {
            loggedUser = currentUser.get().getPawUser();
        }
        mav.addObject("user", loggedUser);
        mav.addObject("games", games);


        Map<Game, List<Tournament>> tournaments = ts.getUnfilteredTournamentPages(0L);
        List<Long> gameIds = new ArrayList<>();
        for (Game game: tournaments.keySet()) {
            mav.addObject("tournaments" + game.getId(), tournaments.get(game));
            mav.addObject("game" + game.getId(), game);
            gameIds.add(game.getId());
        }
        mav.addObject("gameIds", gameIds);
        return mav;
    }


    @RequestMapping("/gamesPage")
    public ModelAndView gamesPage(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @RequestParam(defaultValue = "0") Long page) {
        final ModelAndView mav = new ModelAndView("gamesPage");
        List<Game> allGames = gs.findAllPaged(page);

        mav.addObject("user", currentUser.orElse(null));
        mav.addObject("games", allGames);
        mav.addObject("totalPages", gs.getPageAmount());
        mav.addObject("currentPage", page);

        return mav;
    }

    @RequestMapping(value = "/tournamentsPage", method = RequestMethod.GET)
    public ModelAndView tournamentsPage(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @ModelAttribute("filterForm") FilterForm filterForm, TournamentFilter tf,  @RequestParam(defaultValue = "0") Long page) {
        final ModelAndView mav = new ModelAndView("tournamentsPage");
        List<Game> allGames = gs.findAll();

        User loggedUser = null;
        if(currentUser.isPresent()) {
            loggedUser = currentUser.get().getPawUser();
        }
        mav.addObject("user", loggedUser);
        mav.addObject("games", allGames);
        mav.addObject("structures", Arrays.stream(Structure.values()).toList());
        mav.addObject("regions", Arrays.stream(Region.values()).toList());
        Map<Elo, String> elosMap = Arrays.stream(Elo.values())
                .collect(Collectors.toMap(
                        elo -> elo,
                        elo -> messageSource.getMessage("elo." + elo.name(), null, LocaleContextHolder.getLocale()),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        mav.addObject("elos", elosMap);
        mav.addObject("genres", Arrays.stream(Genre.values()).toList());
        mav.addObject("teamSizes", Constants.TEAM_SIZES);
        mav.addObject("currentPage", page);

        tf.setGameId(filterForm.getGameId());
        tf.setRegion(filterForm.getRegion());
        tf.setElo(filterForm.getElo());
        tf.setPlayersPerTeam(filterForm.getPlayersPerTeam());
        tf.setGenre(filterForm.getGenre());

        boolean isFiltered = !tf.isEmpty();

        if(isFiltered) {
            mav.addObject("tournaments", ts.findTournaments(tf, page));
            mav.addObject("totalPages", ts.getPageAmount(9, tf));
        }else{
            Map<Game, List<Tournament>> gameTournaments = ts.getUnfilteredTournamentPages(page);
            mav.addObject("gameTournaments", gameTournaments);
            mav.addObject("totalPages", ts.getPageAmount(3, tf));
        }

        mav.addObject("isFiltered", isFiltered);

        return mav;
    }

    @RequestMapping("/search")
    public ModelAndView search(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @RequestParam("q") final String q){
        final ModelAndView mav = new ModelAndView("searchResults");

        mav.addObject("user", currentUser.orElse(null));
        mav.addObject("games", gs.searchByName(q));
        mav.addObject("tournaments", ts.searchByName(q));
        mav.addObject("users", us.searchByName(q));
        mav.addObject("teams", tms.searchByName(q));

        return mav;
    }

    @RequestMapping("/profile/{id}")
    public ModelAndView profile(@ModelAttribute("user") Optional<PawUserDetails> currentUser,
                                @PathVariable Long id,
                                @ModelAttribute("editProfileForm") EditProfileForm editProfileForm,
                                @RequestParam(value = "page", defaultValue = "0") Long commentsPage,
                                @ModelAttribute("addAccountForm") AddAccountForm addAccountForm){
        final ModelAndView mav = new ModelAndView("profile");
        User profile = us.findById(id).orElseThrow(UserNotFoundException::new);
        Float userRating = us.getUserRating(id);
        int totalCommentPages = us.getCommentPages(id);
        commentsPage = adjustPage(commentsPage, totalCommentPages);
        List<Comment> comments = us.getCommentsReceived(id, commentsPage);
        List<UserAccount> userAccounts =  us.getUserAccounts(profile.getId());
        User loggedUser = null;
        if(currentUser.isPresent()) {
            loggedUser = currentUser.get().getPawUser();
        }
        mav.addObject("user", loggedUser);
        mav.addObject("isMyProfile", loggedUser != null && loggedUser.getId() == id);
        mav.addObject("profile", profile);
        mav.addObject("favouriteGames", gs.getFavourites(id));
        mav.addObject("activeTournaments", ts.findUserTournaments(id, false , false , false ,0L));
        mav.addObject("lastTournaments", ts.findUserTournaments(id, true , false , false ,0L));
        mav.addObject("editProfileForm", editProfileForm);
        mav.addObject("userRating", userRating);
        mav.addObject("comments", comments);
        mav.addObject("commentsTotalPages", totalCommentPages);
        mav.addObject("commentsCurrentPage", Math.toIntExact(commentsPage));
        mav.addObject("userAccounts", userAccounts);
        mav.addObject("addAccountForm", addAccountForm);
        mav.addObject("availablePlatforms", us.getAvailablePlatforms(userAccounts));

        editProfileForm.setUsername(profile.getUsername());
        editProfileForm.setBio(profile.getBio());

        boolean hasFormErrors = mav.getModel().containsKey(
                BindingResult.MODEL_KEY_PREFIX + "editProfileForm"
        );
        if (!hasFormErrors) {
            editProfileForm.setUsername(profile.getUsername());
            editProfileForm.setBio(profile.getBio());
        }

        return mav;
    }


    @RequestMapping("/profile/{id}/tournaments")
    public ModelAndView profileTournaments(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @PathVariable Long id, @RequestParam(value = "section", required = false, defaultValue = "active") String section, @RequestParam(defaultValue = "0") Long page1, @RequestParam(defaultValue = "0") Long page2) {
        final ModelAndView mav = new ModelAndView("myTournaments");

        User profile = us.findById(id).orElseThrow(UserNotFoundException::new);
        mav.addObject("profile", profile);
        mav.addObject("user", currentUser.map(PawUserDetails::getPawUser).orElse(null));

        int totalPages1 = 0;
        int totalPages2 = 0;

        switch (section) {
            case Constants.TOURNAMENTS_OWNED -> {
                totalPages1 = ts.countUserTournaments(profile.getId(), Constants.ONGOING, Constants.CREATOR, Constants.ALL_TOURNEYS);
                totalPages2 = ts.countUserTournaments(profile.getId(), Constants.FINISHED, Constants.CREATOR, Constants.ALL_TOURNEYS);

                page1 = adjustPage(page1, totalPages1);
                page2 = adjustPage(page2, totalPages2);

                List<Tournament> onGoingTournaments = ts.findUserTournaments(profile.getId(), Constants.ONGOING, Constants.CREATOR, Constants.ALL_TOURNEYS, page1);
                List<Tournament> finishedTournaments = ts.findUserTournaments(profile.getId(), Constants.FINISHED, Constants.CREATOR, Constants.ALL_TOURNEYS, page2);

                mav.addObject("onGoingTournaments", onGoingTournaments);
                mav.addObject("finishedTournaments", finishedTournaments);
            }

            case Constants.TOURNAMENTS_FINISHED -> {
                totalPages1 = ts.countUserTournaments(profile.getId(), Constants.FINISHED, Constants.PARTICIPANT, Constants.ALL_TOURNEYS);
                totalPages2 = ts.countUserTournaments(profile.getId(), Constants.FINISHED, Constants.PARTICIPANT, Constants.WON);

                page1 = adjustPage(page1, totalPages1);
                page2 = adjustPage(page2, totalPages2);

                List<Tournament> pastTournaments = ts.findUserTournaments(profile.getId(), Constants.FINISHED, Constants.PARTICIPANT, Constants.ALL_TOURNEYS, page1);
                List<Tournament> wonTournaments = ts.findUserTournaments(profile.getId(), Constants.FINISHED, Constants.PARTICIPANT, Constants.WON, page2);

                mav.addObject("pastTournaments", pastTournaments);
                mav.addObject("wonTournaments", wonTournaments);
            }

            case Constants.TOURNAMENTS_ACTIVE -> {
                totalPages1 = ts.countUserTournaments(profile.getId(), Constants.ONGOING, Constants.PARTICIPANT, Constants.ALL_TOURNEYS);
                page1 = adjustPage(page1, totalPages1);

                List<Tournament> joinedTournaments = ts.findUserTournaments(profile.getId(),  Constants.ONGOING, Constants.PARTICIPANT, Constants.ALL_TOURNEYS, page1);
                mav.addObject("joinedTournaments", joinedTournaments);
            }
        }

        mav.addObject("totalPages1", totalPages1);
        mav.addObject("totalPages2", totalPages2);
        mav.addObject("currentPage1", page1);
        mav.addObject("currentPage2", page2);

        return mav;
    }

    @RequestMapping("/profile/{id}/teams")
    public ModelAndView profileTeams(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @PathVariable Long id, @ModelAttribute("editProfileForm") EditProfileForm editProfileForm){
        final ModelAndView mav = new ModelAndView("profileTeams");

        User profile = us.findById(id).orElseThrow(UserNotFoundException::new);
        Float userRating = us.getUserRating(profile.getId());
        User loggedUser = null;
        if(currentUser.isPresent()) {
            loggedUser = currentUser.get().getPawUser();
        }
        mav.addObject("user", loggedUser);
        mav.addObject("isMyProfile", loggedUser != null && loggedUser.getId() == id);
        mav.addObject("profile", profile);
        mav.addObject("teams", tms.getUserTeams(id));
        mav.addObject("EditProfileForm", editProfileForm);
        mav.addObject("userRating", userRating);

        editProfileForm.setUsername(profile.getUsername());
        editProfileForm.setBio(profile.getBio());
        boolean hasFormErrors = mav.getModel().containsKey(
                BindingResult.MODEL_KEY_PREFIX + "editProfileForm"
        );
        if (!hasFormErrors) {
            editProfileForm.setUsername(profile.getUsername());
            editProfileForm.setBio(profile.getBio());
        }

        return mav;
    }

    @RequestMapping(path = "/profile/{id}/comment", method = RequestMethod.POST)
    public ModelAndView comment(@ModelAttribute("user") Optional<PawUserDetails> currentUser,
                                @PathVariable long id,
                                @ModelAttribute("commentForm") CommentForm commentForm,
                                BindingResult result) {
        if(result.hasErrors()) {
            return profile(currentUser, id, new EditProfileForm(), DEFAULT_PAGE, new AddAccountForm());
        }
        us.commentOnProfile(currentUser.orElseThrow(UserNotAuthenticatedException::new).getPawUser(), id, commentForm.getComment()); // aca se manda una excepcion, pero no deberia llegar por Spring Security.
        return new ModelAndView("redirect:/profile/{id}");
    }

    private long adjustPage(long page, int totalPages) {
        if (totalPages <= 0) return 0;
        if (page < 0) return 0;
        if (page >= totalPages) return totalPages - 1;
        return page;
    }

    @RequestMapping(value = "/profile/update", method = { RequestMethod.POST })
    public ModelAndView updateProfile(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @RequestParam("userId") final long userId, @Valid @ModelAttribute("editProfileForm") final EditProfileForm form, final BindingResult result,  @ModelAttribute("addAccountForm") AddAccountForm addAccountForm){
        if (currentUser.isPresent() && result.hasErrors()) {
            ModelAndView mav = profile(currentUser, userId, form, DEFAULT_PAGE, addAccountForm);
            mav.addObject("openModal", "'editProfileModal'");
            return mav;
        }

        ModelAndView mav = new ModelAndView("redirect:/profile/" + userId);
        byte[] pfpBytes = null;
        try {
            if (form.getProfilePicture() != null && !form.getProfilePicture().isEmpty()) {
                pfpBytes = form.getProfilePicture().getBytes();
            }
        } catch (IOException e) {
            result.rejectValue("profilePicture", "error.tournamentForm.invalidImage");
            return mav;
        }
        byte[] bannerBytes = null;
        try {
            if (form.getBannerPicture() != null && !form.getBannerPicture().isEmpty()) {
                bannerBytes = form.getBannerPicture().getBytes();
            }
        } catch (IOException e) {
            result.rejectValue("bannerPicture", "error.tournamentForm.invalidImage");
            return mav;
        }
        us.updateProfileInfo(userId, form.getUsername(), form.getBio(), pfpBytes, bannerBytes);

        return mav;
    }

    @RequestMapping(value = "/account/delete", method = { RequestMethod.POST })
    public ModelAndView deleteUserAccount(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @RequestParam("userId") final long userId, @RequestParam("platform") final Platform platform) {

        User user = currentUser.map(PawUserDetails::getPawUser).orElse(null);
        List<Platform> platforms = Arrays.stream(Platform.values()).toList();

        ModelAndView mav = new ModelAndView("redirect:/profile/" + userId);
        if (user == null || userId != user.getId() || !platforms.contains(platform)){
            return mav;
        }

        us.deleteUserAccount(userId, platform);

        return mav;
    }

    @RequestMapping(value = "/account/add", method = { RequestMethod.POST })
    public ModelAndView addUserAccount(@ModelAttribute("user") Optional<PawUserDetails> currentUser, @Valid @ModelAttribute("addAccountForm") final AddAccountForm form, final BindingResult result) {

        User user = currentUser.map(PawUserDetails::getPawUser).orElse(null);

        if (currentUser.isPresent() && result.hasErrors()) {
            ModelAndView mav = profile(currentUser, form.getUserId(), new EditProfileForm(),DEFAULT_PAGE ,form);
            mav.addObject("openModal", "'addAccountModal'");
            return mav;
        }

        ModelAndView mav = new ModelAndView("redirect:/profile/" + form.getUserId());
        if (user == null || form.getUserId() != user.getId()){
            return mav;
        }

        us.addUserAccount(form.getUserId(), form.getPlatform(), form.getUsername());

        return mav;
    }

    @GetMapping(value = "/users/search", produces = "application/json")
    @ResponseBody
    public List<String> searchUsers(@RequestParam String name) {
        return us.searchByName(name)
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }
}
