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
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

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
        if (user.isEmpty()) {
            // TODO: handle unaffiliated email
            return;
        }
        long userId = user.get().getId();
        Token token = generateToken(userId, RESET_PASSWORD_DAYS_DURATION);
        ms.sendResetPasswordEmail(userId, token.getToken(), email, baseUrl);
    }

    @Transactional
    @Override
    public void sendVerificationEmail(String email, String baseUrl) {
        Optional<User> user = findByEmail(email);
        if(user.isEmpty()) {
            // TODO: handle unaffiliated email
            return;
        }
        long userId = user.get().getId();
        String username = user.get().getUsername();
        Token token = generateToken(userId, VERIFICATION_DAYS_DURATION);
        ms.sendVerificationEmail(userId, username, token.getToken(), email, baseUrl);
    }

    @Transactional
    @Override
    public Optional<Token> resetPassword(Long token, Long userId, String newPassword) {
        Optional<Token> optToken = checkTokenValidity(token, userId);
        if(optToken.isPresent()) {
            userDao.changePassword(userId, passwordEncoder.encode(newPassword));
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
            throw new UserNotFoundException();
        }
    }

    @Transactional
    @Override
    public Optional<Token> checkTokenValidity(Long token, Long userId) {
        Optional<Token> optToken = tokenDao.findByToken(token);
        if(findById(userId).isEmpty()) {
            throw new UserNotFoundException();
        }
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

    @Transactional
    @Override
    public void updateProfileInfo(Long userId, String username, String bio, byte[] pfp, byte[] banner){
        Optional<User> user = userDao.findById(userId);
        Long bannerId = null;
        Long pfpId = null;
        if (pfp != null){
            pfpId = imageDao.insertImage(pfp);
        }
        if (banner != null){
            bannerId = imageDao.insertImage(pfp);
        }
        userDao.updateProfileInfo(userId, username, bio, pfpId, bannerId);
    }

    @Override
    public List<User> searchByName(String name) {
        return userDao.searchByName(name);
    }

    @Transactional(readOnly = true)
    @Override
    public void sendTournamentJoinedEmail(String username, Long tournamentId, String tournamentLink, String recipient) {
        Optional<Tournament> tournamentOpt = tournamentDao.findById(tournamentId);
        if(tournamentOpt.isEmpty()) {
            throw new TournamentNotFoundException();
        }
        Tournament tournament = tournamentOpt.get();
        User creator = findById(tournament.getCreator_id()).get();
        ms.sendTournamentJoinedEmail(username, tournament.getName(), tournamentLink, recipient, creator.getEmail());
    }

    @Override
    public void sendTournamentCreatedEmail(String username, String tournamentName, String tournamentLink, String recipient) {
        ms.sendTournamentCreatedEmail(username, tournamentName, tournamentLink, recipient);
    }

}
