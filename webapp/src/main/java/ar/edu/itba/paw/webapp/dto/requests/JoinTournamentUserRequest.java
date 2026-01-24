package ar.edu.itba.paw.webapp.dto.requests;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class JoinTournamentUserRequest {

    @NotNull
    @Positive
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}