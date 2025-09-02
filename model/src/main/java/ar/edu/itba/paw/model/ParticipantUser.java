package ar.edu.itba.paw.model;

public class ParticipantUser {

    private final Long user_id;
    private final Long tournament_id;
    private Integer points;

    public ParticipantUser(Long userId, Long tournamentId) {
        user_id = userId;
        tournament_id = tournamentId;
        points = 0;
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
}
