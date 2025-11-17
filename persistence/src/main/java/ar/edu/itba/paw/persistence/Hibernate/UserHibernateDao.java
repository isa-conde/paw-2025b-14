package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Platform;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class UserHibernateDao implements UserDao {

    private static final int PAGE_SIZE = 9;

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
    public boolean checkUsernameExists(String username) {
        final TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        return !query.getResultList().isEmpty();
    }

    @Override
    public boolean checkEmailExists(String email) {
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
    public void updateProfileInfo(long userId, String username, String bio, Long pfp, Long banner) {
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
    public List<User> searchByName(String name, long page) {

        Query idQuery = em.createNativeQuery(
                "SELECT u.id " +
                        "FROM users u " +
                        "WHERE LOWER(u.username) LIKE CONCAT('%', LOWER(?1), '%') " +
                        "ORDER BY u.username ASC"
        );

        idQuery.setParameter(1, name);
        idQuery.setFirstResult((int) (page * PAGE_SIZE));
        idQuery.setMaxResults(PAGE_SIZE);

        @SuppressWarnings("unchecked")
        List<Number> ids = idQuery.getResultList();
        if (ids.isEmpty()) return Collections.emptyList();

        TypedQuery<User> fullQuery = em.createQuery(
                "SELECT DISTINCT u FROM User u " +
                        "WHERE u.id IN :ids " +
                        "ORDER BY u.username ASC",
                User.class
        );

        fullQuery.setParameter("ids",
                ids.stream().map(Number::longValue).toList()
        );

        return fullQuery.getResultList();
    }

    public int countSearchByNameUser(String name) {

        TypedQuery<Long> countQuery = em.createQuery(
                "SELECT COUNT(u) FROM User u " +
                        "WHERE LOWER(u.username) LIKE CONCAT('%', LOWER(:username), '%')",
                Long.class
        );

        countQuery.setParameter("username", name);

        long total = countQuery.getSingleResult();

        return (int) Math.ceil((double) total / PAGE_SIZE);
    }

    @Override
    public List<User> findAllByName(String name) {
        final TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE LOWER(u.username) LIKE CONCAT('%', LOWER(:username), '%')", User.class);
        query.setParameter("username", name);
        return query.getResultList();
    }


    @Override
    public void updateUserLocale(String locale, long userId) {
        User user = em.find(User.class, userId);
        if (user != null) {
            user.setLocale(locale);
            em.merge(user);
        }
    }

    @Override
    public void updateUserRating(long userId, float rating) {
        em.createQuery("UPDATE User u SET u.rating = :rating WHERE u.id = :userId")
                .setParameter("rating", rating)
                .setParameter("userId", userId)
                .executeUpdate();
    }

    @Override
    public void addUserAccount(long userId, Platform platform, String username) {
        em.createNativeQuery("INSERT INTO user_account (user_id, platform, username) VALUES (?1, CAST(?2 AS platform), ?3)")
                .setParameter(1, userId)
                .setParameter(2, platform.name())
                .setParameter(3, username)
                .executeUpdate();
    }

    @Override
    public void deleteUserAccount(long userId, Platform platform) {
        em.createQuery("DELETE FROM UserAccount u WHERE u.id.userId = :userId AND u.id.platform = :platform")
                .setParameter("userId", userId)
                .setParameter("platform", platform)
                .executeUpdate();
    }
}
