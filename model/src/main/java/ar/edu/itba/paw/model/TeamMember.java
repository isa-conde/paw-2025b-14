package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.ids.TeamMemberId;

import javax.persistence.*;


@Entity
@Table(name = "team_member")
public class TeamMember {

    @EmbeddedId
    private TeamMemberId id;

    @ManyToOne
    @MapsId("teamId")
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    private User user;

    private Boolean verified;

    public TeamMember() {}

    public TeamMember(TeamMemberId id, Team team, User user){
        this.id = id;
        this.team = team;
        this.user = user;
    }

    public Long getUserId() {
        return user.getId();
    }

    public Long getTeamId() {
        return team.getId();
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Team getTeam() {
        return team;
    }

    public User getUser() {
        return user;
    }
}
