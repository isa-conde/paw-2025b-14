package ar.edu.itba.paw.webapp.dto.entities;

import ar.edu.itba.paw.model.Comment;

import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CommentDTO {

    private long id;
    private String comment;
    private LocalDateTime createdAt;

    private List<LinkDTO> links = new ArrayList<>();

    public static Function<Comment, CommentDTO> mapper(final UriInfo uriInfo) {
        return (comment) -> fromComment(uriInfo, comment);
    }

    public static CommentDTO fromComment(final UriInfo uriInfo, final Comment comment) {
        CommentDTO toReturn = new CommentDTO();

        toReturn.id = comment.getId();
        toReturn.comment = comment.getComment();
        toReturn.createdAt = comment.getCreatedAt();

        if (comment.getCommenter() != null) {
            toReturn.addLink("commenter", uriInfo.getBaseUriBuilder().path("users")
                    .path(String.valueOf(comment.getCommenter().getId())).build().toString());
        }

        if (comment.getReceiver() != null) {
            toReturn.addLink("receiver", uriInfo.getBaseUriBuilder().path("users")
                    .path(String.valueOf(comment.getReceiver().getId())).build().toString());
        }

        return toReturn;
    }

    private void addLink(String rel, String href) {
        links.add(new LinkDTO(rel, href));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<LinkDTO> getLinks() {
        return links;
    }

    public void setLinks(List<LinkDTO> links) {
        this.links = links;
    }
}
