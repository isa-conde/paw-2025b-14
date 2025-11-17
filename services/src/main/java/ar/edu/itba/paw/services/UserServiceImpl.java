package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.*;
import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.persistence.TokenDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Comment;
import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserAccount;
import ar.edu.itba.paw.model.enums.Platform;
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
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;
    private final TokenDao tokenDao;
    private final PasswordEncoder passwordEncoder;
    private final MailService ms;
    private final ImageDao imageDao;
    private final CommentDao commentDao;

    private final static int RESET_PASSWORD_DAYS_DURATION = 1;
    private final static int VERIFICATION_DAYS_DURATION = 2;

    public UserServiceImpl(final UserDao userDao, final TokenDao tokenDao, final PasswordEncoder passwordEncoder, final MailService ms, final ImageDao imageDao, final CommentDao commentDao) {
        this.userDao = userDao;
        this.tokenDao = tokenDao;
        this.passwordEncoder = passwordEncoder;
        this.ms = ms;
        this.imageDao = imageDao;
        this.commentDao = commentDao;
    }

    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    @Transactional
    @Override
    public User create(String username, String email, String password, Locale locale) throws BusinessException {
        if (userDao.checkUsernameExists(username)){
            throw new UsernameAlreadyUsedException(username);
        }
        if (userDao.checkEmailExists(email)){
            throw new EmailAlreadyUsedException(email);
        }
        String finalLocale = locale.getLanguage();
        LOGGER.info("The user {} has been created with email {}", username, email);
        if (!Objects.equals(finalLocale, "es")){
            finalLocale = "en";
        }
        User toReturn = userDao.create(username, email, passwordEncoder.encode(password), finalLocale);
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
        User user = findByEmail(email).orElseThrow(UserNotFoundException::new);
        long userId = user.getId();
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
        String oldPassword = findById(userId).orElseThrow(UserNotFoundException::new).getPassword();
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
        findById(userId).orElseThrow(UserNotFoundException::new);
        if(optToken.isPresent()) {
            userDao.verifyUser(userId);
            return true;
        }
        return false;
    }

    @Override
    public void authenticateVerifiedUser(Long userId) {
        User user = findById(userId).orElseThrow(UserNotFoundException::new);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_VERIFIED"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        LOGGER.debug("User has been verified and authenticated");
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
        long tokenValue;
        Optional<Token> existingToken;

        do {
            tokenValue = Math.abs(secureRandom.nextLong());
            existingToken = tokenDao.findByToken(tokenValue);
        } while (existingToken.isPresent());

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
    public List<User> searchByName(String name, Long page) {
        return userDao.searchByName(name, page);
    }

    @Override
    public List<User> findAllByName(String name) {
        return userDao.findAllByName(name);
    }

    @Override
    public int countSearchByNameUser(String name) {
        return userDao.countSearchByNameUser(name);
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
        User user = findById(userId).orElseThrow(UserNotFoundException::new);
        Float currentRating = user.getRating();
        Float newRating = (currentRating == null) ? rating : (currentRating + rating) / 2;
        userDao.updateUserRating(userId, newRating);
        LOGGER.debug("User {} rating updated to {}", user.getUsername(), newRating);
    }

    @Override
    public Float getUserRating(Long userId) {
        User user = findById(userId).orElseThrow(UserNotFoundException::new);
        Float userRating = user.getRating();
        if (userRating == null) {
            return null;
        }
        return Math.round(userRating * 10f) / 10f;
    }

    @Override
    public User findUserByToken(Long token) {
        Token tokenToReturn = tokenDao.findByToken(token).orElseThrow(TokenNotFoundException::new);
        return tokenToReturn.getUser();
    }

    @Transactional
    @Override
    public void commentOnProfile(User commenter, long receiverId, String comment) {
        if(receiverId == commenter.getId()) {
            throw new InvalidCommentException();
        }
        User receiver = findById(receiverId).orElseThrow(UserNotFoundException::new);
        commentDao.create(commenter, receiver, comment);
    }

    @Override
    public List<Comment> getCommentsReceived(long receiverId, long page) {
        User receiver = findById(receiverId).orElseThrow(UserNotFoundException::new);
        return commentDao.getCommentsByReceived(receiver, page);
    }

    @Override
    public int getCommentPages(long receiverId) {
        User receiver = findById(receiverId).orElseThrow(UserNotFoundException::new);
        return commentDao.getCommentPagesByReceived(receiver);
    }

    @Transactional
    @Override
    public List<UserAccount> getUserAccounts(long userId) {
        User user = findById(userId).orElseThrow(UserNotFoundException::new);
        List<UserAccount> accounts = user.getAccounts();
        accounts.size();
        return accounts;
    }

    @Transactional
    @Override
    public void addUserAccount(long userId, Platform platform, String username) {
        userDao.addUserAccount(userId, platform, username);
    }

    @Transactional
    @Override
    public void deleteUserAccount(long userId, Platform platform) {
        userDao.deleteUserAccount(userId, platform);
    }

    @Override
    public List<Platform> getAvailablePlatforms(List<UserAccount> accounts) {
        Set<Platform> usedPlatforms = accounts.stream()
                .map(UserAccount::getPlatform)
                .collect(Collectors.toSet());

        return Arrays.stream(Platform.values())
                .filter(p -> !usedPlatforms.contains(p))
                .collect(Collectors.toList());

    }
}
