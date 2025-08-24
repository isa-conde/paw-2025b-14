package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.User;

import java.util.Optional;

public interface UserService {

    public Optional<User> findById(long id);

    public User create(String username);

}
