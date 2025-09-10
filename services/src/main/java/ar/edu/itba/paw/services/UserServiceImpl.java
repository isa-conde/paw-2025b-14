package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.interfaces.exception.BusinessException;
import ar.edu.itba.paw.interfaces.exception.EmailAlreadyUsedException;
import ar.edu.itba.paw.interfaces.exception.UsernameAlreadyUsedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(final UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    @Override
    public User create(String username, String email) throws BusinessException {
        if (userDao.checkUsernameExists(username)){
            throw new UsernameAlreadyUsedException(username);
        }
        if (userDao.checkEmailExists(email)){
            throw new EmailAlreadyUsedException(email);
        }
        return userDao.create(username, email);
    }

    @Override
    public Optional<User> authenticate(String username, String email) {
        return userDao.authenticate(username, email);
    }

}
