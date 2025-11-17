package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Comment;
import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserAccount;
import ar.edu.itba.paw.model.enums.Platform;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface UserService {

    Optional<User> findById(long id);

    User create(String username, String email, String password, Locale locale);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void requestPasswordReset(String email);

    void resendVerification(User user);

    Optional<Token> checkTokenValidity(Long token);

    boolean resetPassword(Long token, String newPassword);

    boolean sameAsOldPassword(String newPassword, Long userId);

    boolean usernameIsTaken(String username);

    boolean emailIsTaken(String email);

    boolean verifyEmail(Long token, Long userId);

    void updateProfileInfo(Long userId, String username, String bio, byte[] pfp, byte[] banner);

    List<User> searchByName(String name, Long page);

    List<User> findAllByName(String name);

    int countSearchByNameUser(String name);

    Float getUserRating(Long userId);

    User findUserByToken(Long token);

    void commentOnProfile(User commenter, long receiverId, String comment);

    List<Comment> getCommentsReceived(long receiverId, long page);

    int getCommentPages(long receiverId);

    List<UserAccount> getUserAccounts(long userId);

    void addUserAccount(long userId, Platform platform, String username);

    void deleteUserAccount(long userId, Platform platform);

    List<Platform> getAvailablePlatforms(List<UserAccount> accounts);
}
