package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
        final TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
        final User result = query.getSingleResult();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        final TypedQuery<User> query = em.createQuery("SELECT u FROM User u where u.username = :username", User.class);
        query.setParameter("username", username);
        final User result = query.getSingleResult();
        return Optional.ofNullable(result);
    }

    @Override
    public User create(String username, String email, String password) {
        final User user = new User(username, email, password);
        em.persist(user);
        return user;
    }

    @Override
    public void changePassword(long user_id, String newPassword) {
        User user = em.find(User.class, user_id);
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
    public void verifyUser(long user_id) {
        User user = em.find(User.class, user_id);
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
                user.setPfp_id(pfp);
            }if (banner != null){
                user.setBanner_id(banner);
            }
            em.merge(user);
        }
    }

    @Override
    public List<User> searchByName(String name) {
        final TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username LIKE CONCAT('%', LOWER(:username), '%')", User.class);
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
}
