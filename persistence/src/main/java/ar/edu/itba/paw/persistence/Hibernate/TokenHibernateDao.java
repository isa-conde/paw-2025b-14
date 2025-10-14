package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.TokenDao;
import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public class TokenHibernateDao implements TokenDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchHibernateDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Token create(Long user_id, Long token, LocalDate expiry_date) {
        Token t = new Token(em.getReference(User.class, user_id), token, expiry_date);
        em.persist(t);
        return t;
    }

    @Override
    public Optional<Token> findByToken(Long token) {
        TypedQuery<Token> query = em.createQuery("SELECT t FROM Token t WHERE t.token = :token", Token.class);
        query.setParameter("token", token);
        return Optional.ofNullable(query.getSingleResult());
    }

    @Override
    public void markAsUsed(Long tokenId) {
        Token t = em.find(Token.class, tokenId);
        t.setUsed(true);
        em.persist(t);
    }

    @Override
    public void deleteExpiredTokens() {
        em.createQuery("""
        DELETE FROM Token t
        WHERE t.expiry_date < :today OR t.used = true
        """)
                .setParameter("today", LocalDate.now())
                .executeUpdate();
    }
}
