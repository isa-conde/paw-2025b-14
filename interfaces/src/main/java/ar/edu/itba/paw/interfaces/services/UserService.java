package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findById(long id);

    User create(String username, String email, String password);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void requestPasswordReset(String email);

    void resendVerification(User user);

    Optional<Token> checkTokenValidity(Long token, Long userId);

    Optional<Token> resetPassword(Long token, Long userId, String newPassword);

    boolean sameAsOldPassword(String newPassword, Long userId);

    boolean usernameIsTaken(String username);

    boolean emailIsTaken(String email);

    Optional<Token> verifyEmail(Long token, Long userId);

    void authenticateVerifiedUser(Long userId);

    void updateProfileInfo(Long userId, String username, String bio, byte[] pfp, byte[] banner);

    List<User> searchByName(String name);

}
