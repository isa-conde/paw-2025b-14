package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserAccount;
import ar.edu.itba.paw.model.enums.Platform;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Repository
public class UserHibernateDao implements UserDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        final TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        User result;
        try {
            result = query.getSingleResult();
        } catch(NoResultException e) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        final TypedQuery<User> query = em.createQuery("SELECT u FROM User u where u.username = :username", User.class);
        query.setParameter("username", username);
        User result;
        try {
            result = query.getSingleResult();
        } catch(NoResultException e) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    @Override
    public User create(String username, String email, String password, String locale) {
        final User user = new User(username, email, password, locale);
        em.persist(user);
        return user;
    }

    @Override
    public void changePassword(long userId, String newPassword) {
        User user = em.find(User.class, userId);
        if (user != null) {
            user.setPassword(newPassword);
            em.merge(user);
        }
    }

    @Override
    public Boolean checkUsernameExists(String username) {
    final TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
    query.setParameter("username", username);
    return !query.getResultList().isEmpty();
    }

    @Override
    public Boolean checkEmailExists(String email) {
        final TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        return !query.getResultList().isEmpty();
    }

    @Override
    public void verifyUser(long userId) {
        User user = em.find(User.class, userId);
        if (user != null){
            user.setVerified(true);
            em.merge(user);
        }
    }

    @Override
    public void updateProfileInfo(Long userId, String username, String bio, Long pfp, Long banner) {
        User user = em.find(User.class, userId);

        if (user != null){
            if (username != null){
                user.setUsername(username);
            }if (bio != null){
                user.setBio(bio);
            }if (pfp != null){
                user.setPfpId(pfp);
            }if (banner != null){
                user.setBannerId(banner);
            }
            em.merge(user);
        }
    }

    @Override
    public List<User> searchByName(String name) {
        final TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE LOWER(u.username) LIKE CONCAT('%', LOWER(:username), '%')", User.class);
        query.setParameter("username", name);
        return query.getResultList();
    }

    @Override
    public void updateUserLocale(String locale, Long userId) {
        User user = em.find(User.class, userId);
        if (user != null) {
            user.setLocale(locale);
            em.merge(user);
        }
    }

    @Override
    public void updateUserRating(Long userId, Float rating) {
        em.createQuery("UPDATE User u SET u.rating = :rating WHERE u.id = :userId")
                .setParameter("rating", rating)
                .setParameter("userId", userId)
                .executeUpdate();
    }

    @Override
    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    public void addUserAccount(long userId, Platform platform, String username) {
        em.createNativeQuery("INSERT INTO user_account (user_id, platform, username) VALUES (:userId, CAST(:platform AS platform), :username)")
                .setParameter("userId", userId)
                .setParameter("platform", platform.name())
                .setParameter("username", username)
                .executeUpdate();
    }

    @Override
    public void deleteUserAccount(long userId, Platform platform) {
        em.createNativeQuery("DELETE FROM user_account WHERE user_id = :userId AND platform = CAST(:platform AS platform)")
                .setParameter("userId", userId)
                .setParameter("platform", platform.name())
                .executeUpdate();
    }
}
