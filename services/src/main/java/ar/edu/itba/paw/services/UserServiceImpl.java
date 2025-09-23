package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.TokenDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import ar.edu.itba.paw.interfaces.exception.BusinessException;
import ar.edu.itba.paw.interfaces.exception.EmailAlreadyUsedException;
import ar.edu.itba.paw.interfaces.exception.UsernameAlreadyUsedException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final TokenDao tokenDao;
    private final PasswordEncoder passwordEncoder;
    private final MailService ms;

    private final static int RESET_PASSWORD_DAYS_DURATION = 1;
    private final static int VERIFICATION_DAYS_DURATION = 2;


    public UserServiceImpl(final UserDao userDao, final TokenDao tokenDao, final PasswordEncoder passwordEncoder, final MailService ms) {
        this.userDao = userDao;
        this.tokenDao = tokenDao;
        this.passwordEncoder = passwordEncoder;
        this.ms = ms;
    }

    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }


    @Override
    public User create(String username, String email, String password) throws BusinessException {
        if (userDao.checkUsernameExists(username)){
            throw new UsernameAlreadyUsedException(username);
        }
        if (userDao.checkEmailExists(email)){
            throw new EmailAlreadyUsedException(email);
        }
        return userDao.create(username, email, passwordEncoder.encode(password));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public void requestPasswordReset(String email) {
        Optional<User> user = findByEmail(email);
        if (user.isEmpty()) {
            // TODO: handle unaffiliated email
            return;
        }
        long userId = user.get().getId();
        String username = user.get().getUsername();
        Token token = generateToken(userId, RESET_PASSWORD_DAYS_DURATION);
        ms.sendResetPasswordEmail(userId, token.getToken(), email);
    }

    @Override
    public void sendVerificationEmail(String email) {
        Optional<User> user = findByEmail(email);
        if(user.isEmpty()) {
            // TODO: handle unaffiliated email
            return;
        }
        long userId = user.get().getId();
        String username = user.get().getUsername();
        Token token = generateToken(userId, VERIFICATION_DAYS_DURATION);
        ms.sendVerificationEmail(userId, username, token.getToken(), email);
    }

    @Override
    public Optional<Token> resetPassword(Long token, Long userId, String newPassword) {
        Optional<Token> optToken = checkTokenValidity(token, userId);
        if(optToken.isPresent()) {
            userDao.changePassword(userId, passwordEncoder.encode(newPassword));
        }
        return optToken;
    }

    @Override
    public boolean sameAsOldPassword(String newPassword, Long userId) {
        String oldPassword = userDao.findById(userId).get().getPassword();
        if(oldPassword == null) return true;
        return passwordEncoder.matches(newPassword, oldPassword);
    }

    @Override
    public Optional<Token> verifyEmail(Long token, Long userId) {
        Optional<Token> optToken = checkTokenValidity(token, userId);
        if(optToken.isPresent()) {
            userDao.verifyUser(userId);
        }
        return optToken;
    }

    @Override
    public Optional<Token> checkTokenValidity(Long token, Long userId) {
        Optional<Token> optToken = tokenDao.findByToken(token);
        if(optToken.isPresent()) {
            Token foundToken = optToken.get();
            if(foundToken.getUser_id().equals(userId)) {
                if(foundToken.getExpiry_date().isAfter(LocalDate.now())) {
                    tokenDao.markAsUsed(foundToken.getId());
                    return optToken;
                }
            }
        }
        return Optional.empty();
    }

    private Token generateToken(Long userId, int validityDays) {
        SecureRandom secureRandom = new SecureRandom();
        long tokenValue = secureRandom.nextLong();

        if (tokenValue < 0) {
            tokenValue = Math.abs(tokenValue);
        }

        Optional<Token> token = tokenDao.findByToken(tokenValue);
        if(token.isPresent()) {
            // TODO: handle existing token
        }

        LocalDate expiryDate = LocalDate.now().plusDays(validityDays);

        return tokenDao.create(userId, tokenValue, expiryDate);
    }
}
