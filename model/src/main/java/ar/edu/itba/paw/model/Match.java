package ar.edu.itba.paw.model;

import java.util.Date;

public class Match {

    private final Long id;
    private final Long tournament_id;
    private final Long user1_id;
    private final Long user2_id;
    private final Date match_date;
    private final Short winner;
    private final Integer points_user1;
    private final Integer point_user2;

    public Match(Long id, Long tournamentId, Long user1Id, Long user2Id, Date matchDate, Short winner, Integer pointsUser1, Integer pointUser2) {
        this.id = id;
        tournament_id = tournamentId;
        user1_id = user1Id;
        user2_id = user2Id;
        match_date = matchDate;
        this.winner = winner;
        points_user1 = pointsUser1;
        point_user2 = pointUser2;
    }

    public Long getId() {
        return id;
    }

    public Long getTournament_id() {
        return tournament_id;
    }

    public Long getUser1_id() {
        return user1_id;
    }

    public Long getUser2_id() {
        return user2_id;
    }

    public Date getMatch_date() {
        return match_date;
    }

    public Short getWinner() {
        return winner;
    }

    public Integer getPoints_user1() {
        return points_user1;
    }

    public Integer getPoint_user2() {
        return point_user2;
    }
}
