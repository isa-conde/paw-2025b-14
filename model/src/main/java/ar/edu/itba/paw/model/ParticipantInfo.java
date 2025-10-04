package ar.edu.itba.paw.model;

public class ParticipantInfo {

    private final Long id;
    private Integer points;
    private final String name;
    private final Integer group_number;

    public ParticipantInfo(final Long id, final String name, Integer points, Integer group_number) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.group_number = group_number;
    }

    public Long getId() {
        return id;
    }

    public Integer getPoints() {
        return points;
    }

    public String getName() {
        return name;
    }

    public Integer getGroupNumber() {
        return group_number;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
