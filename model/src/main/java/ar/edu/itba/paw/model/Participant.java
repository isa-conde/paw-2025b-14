package ar.edu.itba.paw.model;

public class Participant {

    private final Long id;
    private Integer points;
    private final String name;
    private final Integer group_number;

    public Participant(final Long id, final String name, Integer points, Integer group_number) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.group_number = group_number;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public Integer getGroup_number() {
        return group_number;
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
