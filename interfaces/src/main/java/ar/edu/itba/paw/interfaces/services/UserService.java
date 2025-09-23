package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findById(long id);

    User create(String username, String email, String password);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void requestPasswordReset(String email);

    void sendVerificationEmail(String email);

    Optional<Token> checkTokenValidity(Long token, Long userId);

    Optional<Token> resetPassword(Long token, Long userId, String newPassword);

    boolean sameAsOldPassword(String newPassword, Long userId);

    Optional<Token> verifyEmail(Long token, Long userId);
}
