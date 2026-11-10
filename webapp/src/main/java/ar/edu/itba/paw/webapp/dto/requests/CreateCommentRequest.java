package ar.edu.itba.paw.webapp.dto.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateCommentRequest {
    @NotBlank
    @Size(max = 1000)
    private String comment;

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
