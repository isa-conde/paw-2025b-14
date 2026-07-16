package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.model.Comment;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class CommentHibernateDao implements CommentDao {

    @PersistenceContext
    private EntityManager em;

    private static final int COMMENTS_PAGE_SIZE = 6;

    @Override
    public void create(User commenter, User receiver, String comment) {
        Comment toPersist = new Comment(commenter, receiver, comment);
        em.persist(toPersist);
    }

    @Override
    public List<Comment> getCommentsByReceived(User receiver, long page) {
        if (receiver == null) {
            return List.of();
        }
        Query nativeQuery = em.createNativeQuery("SELECT c.id FROM comments c WHERE c.receiver_id = ?1");
        nativeQuery.setParameter(1, receiver.getId());
        nativeQuery.setMaxResults(COMMENTS_PAGE_SIZE);
        nativeQuery.setFirstResult((int) (page * COMMENTS_PAGE_SIZE));

        @SuppressWarnings("unchecked")
        List<Number> rawIds = nativeQuery.getResultList();
        List<Long> ids = rawIds.stream().map(Number::longValue).toList();
        if (ids.isEmpty()) {
            return List.of();
        }

        return em.createQuery("SELECT c FROM Comment c WHERE id in :ids", Comment.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    public int getCommentPagesByReceived(User receiver) {
        if (receiver == null) {
            return 0;
        }
        TypedQuery<Long> q = em.createQuery(
                "SELECT COUNT(c) FROM Comment c WHERE c.receiver = :receiver",
                Long.class
        );
        q.setParameter("receiver", receiver);
        long count = q.getSingleResult();
        return (int) Math.ceil(count / (double) COMMENTS_PAGE_SIZE);
    }
}
