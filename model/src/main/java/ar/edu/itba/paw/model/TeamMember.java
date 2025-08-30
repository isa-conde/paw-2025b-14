package ar.edu.itba.paw.model;

public class TeamMember {

    private final Long user_id;
    private final Long team_id;


    public TeamMember(Long userId, Long teamId) {
        user_id = userId;
        team_id = teamId;
    }

    public Long getUser_id() {
        return user_id;
    }

    public Long getTeam_id() {
        return team_id;
    }
}
