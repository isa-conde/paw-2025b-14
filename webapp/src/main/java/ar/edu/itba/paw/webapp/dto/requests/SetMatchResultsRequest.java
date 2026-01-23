package ar.edu.itba.paw.webapp.dto.requests;

import javax.validation.constraints.NotNull;

public class SetMatchResultsRequest {

    @NotNull
    private Integer localScore;

    @NotNull
    private Integer visitorScore;

    public Integer getLocalScore() {
        return localScore;
    }

    public void setLocalScore(Integer localScore) {
        this.localScore = localScore;
    }

    public Integer getVisitorScore() {
        return visitorScore;
    }

    public void setVisitorScore(Integer visitorScore) {
        this.visitorScore = visitorScore;
    }
}