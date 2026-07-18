package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.model.Comment;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

        return em.createQuery("""
                SELECT c
                FROM Comment c
                JOIN FETCH c.commenter
                JOIN FETCH c.receiver
                WHERE c.receiver.id = :receiverId
                ORDER BY c.createdAt DESC, c.id DESC
                """, Comment.class)
                .setParameter("receiverId", receiver.getId())
                .setMaxResults(COMMENTS_PAGE_SIZE)
                .setFirstResult((int) (page * COMMENTS_PAGE_SIZE))
                .getResultList();
    }

    @Override
    public int getCommentPagesByReceived(User receiver) {
        if (receiver == null) {
            return 0;
        }
        TypedQuery<Long> q = em.createQuery(
                "SELECT COUNT(c) FROM Comment c WHERE c.receiver.id = :receiverId",
                Long.class
        );
        q.setParameter("receiverId", receiver.getId());
        long count = q.getSingleResult();
        return (int) Math.ceil(count / (double) COMMENTS_PAGE_SIZE);
    }
}
