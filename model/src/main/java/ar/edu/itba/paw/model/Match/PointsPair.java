package ar.edu.itba.paw.model.Match;

public class PointsPair {
    private final Integer points;
    private final Integer score_difference;

    public PointsPair(Integer points, Integer score_difference) {
        this.points = points;
        this.score_difference = score_difference;
    }

    public Integer getPoints() {
        return points;
    }

    public Integer getScoreDifference() {
        return score_difference;
    }

}
