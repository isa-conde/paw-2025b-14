package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.User;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface UserDao {

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    User create(String username, String email, String password);

    void changePassword(long userId, String newPassword);

    Boolean checkUsernameExists(String username);

    Boolean checkEmailExists(String email);

    void verifyUser(long userId);

    void updateProfileInfo(Long userId, String username, String bio, Long pfp, Long banner);

    List<User> searchByName(String name);

    void updateUserLocale(String locale, Long userId);

    void updateUserRating(Long userId, Float rating);

    List<User> findAll();

}
