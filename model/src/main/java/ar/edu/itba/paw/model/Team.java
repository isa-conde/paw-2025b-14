package ar.edu.itba.paw.model;

public class Team {

    private final Long id;
    private String name;
    private Long pfp_id;
    private Long banner_id;
    private Long owner_id;

    public Team(Long id, String name, Long pfp_id, Long banner_id, Long owner_id) {
        this.id = id;
        this.name = name;
        this.pfp_id = pfp_id;
        this.banner_id = banner_id;
        this.owner_id = owner_id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getPfp_id() {
        return pfp_id;
    }

    public Long getBanner_id() {
        return banner_id;
    }

    public Long getOwner_id() {
        return owner_id;
    }

}
