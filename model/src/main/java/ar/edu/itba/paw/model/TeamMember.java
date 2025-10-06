package ar.edu.itba.paw.model;

public class TeamMember {

    private final Long user_id;
    private final Long team_id;
    private Boolean verified;


    public TeamMember(Long userId, Long teamId, Boolean verified) {
        user_id = userId;
        team_id = teamId;
        this.verified = verified;
    }

    public Long getUser_id() {
        return user_id;
    }

    public Long getTeam_id() {
        return team_id;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
