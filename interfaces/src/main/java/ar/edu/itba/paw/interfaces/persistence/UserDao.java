package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.User;

import java.util.Optional;

public interface UserDao {

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    User create(String username, String email, String password);

    void changePassword(long user_id, String newPassword);

    void verifyUser(long user_id);

}
