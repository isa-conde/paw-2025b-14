package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Comment;
import ar.edu.itba.paw.model.User;

import java.util.List;

public interface CommentDao {

    void create(User commenter, User receiver, String comment);

    List<Comment> getCommentsByReceived(User receiver, long page);

    int getCommentPagesByReceived(User receiver);

}
