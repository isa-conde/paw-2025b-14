package ar.edu.itba.paw.model;

public class Participant {

    private final Long id;
    private Integer points;
    private final String name;
    private final Integer group_number;
    private final Long pfp_id;

    public Participant(final Long id, final String name, Integer points, Integer group_number, Long pfp_id) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.group_number = group_number;
        this.pfp_id = pfp_id;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
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

    public Long getPfp_id() {
        return pfp_id;
    }
}
