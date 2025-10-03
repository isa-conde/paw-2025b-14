package ar.edu.itba.paw.model;

public class ParticipantUser {

    private final Long user_id;
    private final Long tournament_id;
    private Integer points;
    private final Integer group_number;

    public ParticipantUser(Long userId, Long tournamentId, Integer points, Integer groupNumber) {
        this.user_id = userId;
        this.tournament_id = tournamentId;
        this.points = points;
        this.group_number = groupNumber;
    }

    public Long getTournament_id() {
        return tournament_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getGroupNumber() {
        return group_number;
    }
}
