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

    @Override
    public void create(User commenter, User receiver, String comment) {
        Comment toPersist = new Comment(commenter, receiver, comment);
        em.persist(toPersist);
    }

    @Override
    public List<Comment> getCommentsByReceived(User receiver) {
        if (receiver == null) {
            return List.of();
        }
        TypedQuery<Comment> q = em.createQuery(
                "SELECT c FROM Comment c WHERE c.receiver = :receiver",
                Comment.class
        );
        q.setParameter("receiver", receiver);
        return q.getResultList();
    }
}
