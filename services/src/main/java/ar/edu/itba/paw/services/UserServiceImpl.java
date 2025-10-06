package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.*;
import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.persistence.TokenDao;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.Tournament.Tournament;
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

import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;
    private final TokenDao tokenDao;
    private final PasswordEncoder passwordEncoder;
    private final MailService ms;
    private final ImageDao imageDao;
    private final TournamentDao tournamentDao;

    private final static int RESET_PASSWORD_DAYS_DURATION = 1;
    private final static int VERIFICATION_DAYS_DURATION = 2;


    public UserServiceImpl(final UserDao userDao, final TokenDao tokenDao, final PasswordEncoder passwordEncoder, final TournamentDao tournamentDao, final MailService ms, final ImageDao imageDao) {
        this.userDao = userDao;
        this.tokenDao = tokenDao;
        this.passwordEncoder = passwordEncoder;
        this.tournamentDao = tournamentDao;
        this.ms = ms;
        this.imageDao = imageDao;
    }

    @Transactional(readOnly = true)
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
        return userDao.create(username, email, passwordEncoder.encode(password));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Transactional
    @Override
    public void requestPasswordReset(String email, String baseUrl) {
        Optional<User> user = findByEmail(email);
        long userId = user.get().getId();
        Token token = generateToken(userId, RESET_PASSWORD_DAYS_DURATION);
        ms.sendResetPasswordEmail(userId, token.getToken(), email, baseUrl);
        LOGGER.info("Reset Password email correctly sent to the address {}", email);
    }

    @Transactional
    @Override
    public void sendVerificationEmail(String email, String baseUrl) {
        Optional<User> user = findByEmail(email);
        if(user.isEmpty()) {
            throw new UserNotFoundException();
        }
        long userId = user.get().getId();
        String username = user.get().getUsername();
        Token token = generateToken(userId, VERIFICATION_DAYS_DURATION);
        ms.sendVerificationEmail(userId, username, token.getToken(), email, baseUrl);
        LOGGER.info("Verification email correctly sent to the address {}", email);
    }

    @Transactional
    @Override
    public Optional<Token> resetPassword(Long token, Long userId, String newPassword) {
        Optional<Token> optToken = checkTokenValidity(token, userId);
        if(optToken.isPresent()) {
            userDao.changePassword(userId, passwordEncoder.encode(newPassword));
            LOGGER.info("User {} has successfully changed their password", findById(userId).get().getUsername());
        }
        return optToken;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean sameAsOldPassword(String newPassword, Long userId) {
        String oldPassword = findById(userId).get().getPassword();
        if(oldPassword == null) return false;
        return passwordEncoder.matches(newPassword, oldPassword);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean usernameIsTaken(String username) {
        return findByUsername(username).isPresent();
    }

    @Transactional(readOnly = true)
    @Override
    public boolean emailIsTaken(String email) {
        return findByEmail(email).isPresent();
    }

    @Transactional
    @Override
    public Optional<Token> verifyEmail(Long token, Long userId) {
        Optional<Token> optToken = checkTokenValidity(token, userId);
        Optional<User> user = findById(userId);
        if(user.isPresent()) {
            if(optToken.isPresent()) {
                userDao.verifyUser(userId);
            }
        } else {
            LOGGER.error("User with ID {} does not exist", userId);
            throw new UserNotFoundException();
        }

        return optToken;
    }

    @Transactional(readOnly = true)
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
        } else {
            LOGGER.error("User with ID {} does not exist", userId);
            throw new UserNotFoundException();
        }
    }

    @Transactional
    @Override
    public Optional<Token> checkTokenValidity(Long token, Long userId) {
        Optional<Token> optToken = tokenDao.findByToken(token);
        if(findById(userId).isEmpty()) {
            LOGGER.error("User with ID {} does not exist", userId);
            throw new UserNotFoundException();
        }
        if(optToken.isPresent()) {
            Token foundToken = optToken.get();
            if(foundToken.getUser_id().equals(userId)) {
                if(foundToken.getExpiry_date().isAfter(LocalDate.now())) {
                    tokenDao.markAsUsed(foundToken.getId());
                    return optToken;
                } else {
                    LOGGER.warn("User with ID {} attempted to use an expired token", userId);
                    // TODO: make new exception for expired token
                }
            } else {
                LOGGER.warn("User with ID {} attempted to use another user's token", userId);
                // TODO: make new exception for token not belonging to user
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

    @Transactional
    @Override
    public void updateProfileInfo(Long userId, String username, String bio, byte[] pfp, byte[] banner){
        Optional<User> user = userDao.findById(userId);
        Long bannerId = null;
        Long pfpId = null;
        if (pfp != null){
            pfpId = imageDao.insertImage(pfp);
        }else {
            pfpId = user.get().getProfile_picture_id();
        }
        if (banner != null){
            bannerId = imageDao.insertImage(pfp);
        }else {
            bannerId = user.get().getBanner_id();
        }
        userDao.updateProfileInfo(userId, username, bio, pfpId, bannerId);
        LOGGER.info("Profile of user {} has been correctly updated", username);
    }

    @Transactional(readOnly = true)
    @Override
    public void sendTournamentJoinedEmail(String username, Long tournamentId, String tournamentLink, String recipient) {
        Optional<Tournament> tournamentOpt = tournamentDao.findById(tournamentId);
        if(tournamentOpt.isEmpty()) {
            LOGGER.error("Tournament of ID {} does not exist", tournamentId);
            throw new TournamentNotFoundException();
        }
        Tournament tournament = tournamentOpt.get();
        User creator = findById(tournament.getCreator_id()).get();
        ms.sendTournamentJoinedEmail(username, tournament.getName(), tournamentLink, recipient, creator.getEmail());
        LOGGER.info("Tournament joined email correctly sent to the address {}", recipient);
    }

    @Override
    public void sendTournamentCreatedEmail(String username, String tournamentName, String tournamentLink, String recipient) {
        ms.sendTournamentCreatedEmail(username, tournamentName, tournamentLink, recipient);
        LOGGER.info("Tournament creation email correctly sent to the address {}", recipient);
    }

}
