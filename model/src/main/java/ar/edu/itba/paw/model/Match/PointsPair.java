package ar.edu.itba.paw.model.Match;

public class PointsPair {
    private final Integer points;
    private final Integer scoreDifference;

    public PointsPair(Integer points, Integer scoreDifference) {
        this.points = points;
        this.scoreDifference = scoreDifference;
    }

    public Integer getPoints() {
        return points;
    }

    public Integer getScoreDifference() {
        return scoreDifference;
    }

}
