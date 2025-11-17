package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Platform;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    User create(String username, String email, String password, String locale);

    void changePassword(long userId, String newPassword);

    boolean checkUsernameExists(String username);

    boolean checkEmailExists(String email);

    void verifyUser(long userId);

    void updateProfileInfo(long userId, String username, String bio, Long pfp, Long banner);

    List<User> searchByName(String name, long page);

    int countSearchByNameUser(String name);

    List<User> findAllByName(String name);

    void updateUserLocale(String locale, long userId);

    void updateUserRating(long userId, float rating);

    void addUserAccount(long userId, Platform platform, String username);

    void deleteUserAccount(long userId, Platform platform);

}
