package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.*;
import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.persistence.TokenDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;
    private final TokenDao tokenDao;
    private final PasswordEncoder passwordEncoder;
    private final MailService ms;
    private final ImageDao imageDao;

    private final static int RESET_PASSWORD_DAYS_DURATION = 1;
    private final static int VERIFICATION_DAYS_DURATION = 2;
    private final static String ID_USER_INEXISTENT = "User with ID {} does not exist";


    public UserServiceImpl(final UserDao userDao, final TokenDao tokenDao, final PasswordEncoder passwordEncoder, final MailService ms, final ImageDao imageDao) {
        this.userDao = userDao;
        this.tokenDao = tokenDao;
        this.passwordEncoder = passwordEncoder;
        this.ms = ms;
        this.imageDao = imageDao;
    }

    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    @Transactional
    @Override
    public User create(String username, String email, String password) throws BusinessException {
        if (userDao.checkUsernameExists(username)){
            throw new UsernameAlreadyUsedException(username);
        }
        if (userDao.checkEmailExists(email)){
            throw new EmailAlreadyUsedException(email);
        }
        LOGGER.info("The user {} has been created with email {}", username, email);
        User toReturn = userDao.create(username, email, passwordEncoder.encode(password));
        Token token = generateToken(toReturn.getId(), VERIFICATION_DAYS_DURATION);
        ms.sendVerificationEmail(toReturn.getId(), toReturn.getUsername(), token.getToken(), toReturn.getEmail());
        LOGGER.info("Verification email correctly sent to the address {}", email);
        return toReturn;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Transactional
    @Override
    public void requestPasswordReset(String email) {
        Optional<User> user = findByEmail(email);
        long userId = user.get().getId();
        Token token = generateToken(userId, RESET_PASSWORD_DAYS_DURATION);
        ms.sendResetPasswordEmail(userId, token.getToken(), email);
        LOGGER.info("Reset Password email correctly sent to the address {}", email);
    }

    @Transactional
    @Override
    public boolean resetPassword(Long token, String newPassword) {
        Optional<Token> optToken = checkTokenValidity(token);
        if(optToken.isPresent()) {
            User user = optToken.get().getUser();
            userDao.changePassword(user.getId(), passwordEncoder.encode(newPassword));
            LOGGER.info("User {} has successfully changed their password", user.getUsername());
            return true;
        } else return false;
    }

    @Override
    public boolean sameAsOldPassword(String newPassword, Long userId) {
        String oldPassword = findById(userId).get().getPassword();
        if(oldPassword == null) return false;
        return passwordEncoder.matches(newPassword, oldPassword);
    }

    @Override
    public boolean usernameIsTaken(String username) {
        return findByUsername(username).isPresent();
    }

    @Override
    public boolean emailIsTaken(String email) {
        return findByEmail(email).isPresent();
    }

    @Transactional
    @Override
    public void resendVerification(User user) {
        Token token = generateToken(user.getId(), VERIFICATION_DAYS_DURATION);
        ms.sendVerificationEmail(user.getId(), user.getUsername(), token.getToken(), user.getEmail());
        LOGGER.info("The verification email has been successfully resent to the address {}", user.getEmail());
    }

    @Transactional
    @Override
    public boolean verifyEmail(Long token, Long userId) {
        Optional<Token> optToken = checkTokenValidity(token);
        Optional<User> user = findById(userId);
        if(user.isPresent()) {
            if(optToken.isPresent()) {
                userDao.verifyUser(userId);
                return true;
            }
        } else {
            LOGGER.error(ID_USER_INEXISTENT, userId);
            throw new UserNotFoundException();
        }
        return false;
    }

    @Override
    public void authenticateVerifiedUser(Long userId) {
        Optional<User> optUser = findById(userId);
        if(optUser.isPresent()) {
            User user = optUser.get();
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            authorities.add(new SimpleGrantedAuthority("ROLE_VERIFIED"));

            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            LOGGER.debug("User has been verified and authenticated");
        } else {
            LOGGER.error(ID_USER_INEXISTENT, userId);
            throw new UserNotFoundException();
        }
    }

    @Transactional
    @Override
    public Optional<Token> checkTokenValidity(Long token) {
        Optional<Token> optToken = tokenDao.findByToken(token);
        if(optToken.isPresent()) {
            Token foundToken = optToken.get();
            if(foundToken.getExpiryDate().isAfter(LocalDate.now())) {
                tokenDao.markAsUsed(foundToken.getId());
                return optToken;
            } else {
                LOGGER.warn("Attempted to use an expired token");
                throw new ExpiredTokenException();
            }
        } else {
            LOGGER.warn("Attempted to use an inexistent token");
            throw new TokenNotFoundException();
        }
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

    @Transactional
    @Override
    public void updateProfileInfo(Long userId, String username, String bio, byte[] pfp, byte[] banner){
        Long bannerId = null;
        Long pfpId = null;
        if (pfp != null){
            pfpId = imageDao.insertImage(pfp);
        }
        if (banner != null){
            bannerId = imageDao.insertImage(banner);
        }
        userDao.updateProfileInfo(userId, username, bio, pfpId, bannerId);
        LOGGER.info("Profile of user {} has been correctly updated", username);
    }

    @Override
    public List<User> searchByName(String name) {
        return userDao.searchByName(name);
    }

    @Transactional
    @Override
    public void updateUserLocale(Locale locale, Long userId) {
        String language = locale.getLanguage();
        userDao.updateUserLocale(language, userId);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Transactional
    @Override
    public void updateUserRating(Long userId, Float rating) {
        User user = findById(userId).get();
        Float currentRating = user.getRating();
        Float newRating = (currentRating == null) ? rating : (currentRating + rating) / 2;
        userDao.updateUserRating(userId, newRating);
        LOGGER.debug("User {} rating updated to {}", user.getUsername(), newRating);
    }

    @Override
    public Float getUserRating(Long userId) {
        User user = findById(userId).get();
        Float userRating = user.getRating();
        if (userRating == null) {
            return null;
        }
        return Math.round(userRating * 10f) / 10f;
    }

    @Override
    public User findUserByToken(Long token) {
        Optional<Token> optToken = tokenDao.findByToken(token);
        if(optToken.isPresent()) {
            return optToken.get().getUser();
        } else throw new TokenNotFoundException();
    }
}
