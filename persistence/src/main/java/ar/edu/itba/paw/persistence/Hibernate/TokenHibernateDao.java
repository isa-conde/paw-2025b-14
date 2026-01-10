package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.TokenDao;
import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class TokenHibernateDao implements TokenDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Token create(long userId, long token, LocalDate expiryDate) {
        Token t = new Token(em.getReference(User.class, userId), token, expiryDate);
        em.persist(t);
        return t;
    }

    @Override
    public Optional<Token> findByToken(long token) {
        TypedQuery<Token> query = em.createQuery("SELECT t FROM Token t WHERE t.token = :token", Token.class);
        query.setParameter("token", token);
        Token result;
        try {
            result = query.getSingleResult();
        } catch(NoResultException e) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    @Override
    public void markAsUsed(long tokenId) {
        Token t = em.find(Token.class, tokenId);
        t.setUsed(true);
        em.persist(t);
    }

    @Override
    public void deleteExpiredTokens() {
        em.createQuery("""
        DELETE FROM Token t
        WHERE t.expiryDate < :today OR t.used = true
        """)
                .setParameter("today", LocalDate.now())
                .executeUpdate();
    }

    @Override
    public List<Token> findAssignedTokens(User user) {
        final TypedQuery<Token> query = em.createQuery("SELECT t FROM Token t WHERE t.user = :user", Token.class);
        query.setParameter("user", user);
        return query.getResultList();
    }
}
