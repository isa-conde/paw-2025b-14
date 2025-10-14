package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_id_seq")
    @SequenceGenerator(sequenceName = "team_id_seq", name = "team_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;
    @Column(name = "pfp_id")
    private Long pfp_id;
    @Column(name = "banner_id")
    private Long banner_id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMember> teamMembers = new ArrayList<>();


    Team(){}

    public Team(String name, Long pfp_id, Long banner_id) {
        this.name = name;
        this.pfp_id = pfp_id;
        this.banner_id = banner_id;
    }

    public Team(Long id, String name, Long pfp_id, Long banner_id) {
        this.id = id;
        this.name = name;
        this.pfp_id = pfp_id;
        this.banner_id = banner_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPfp_id() {
        return pfp_id;
    }

    public void setPfp_id(Long pfp_id) {
        this.pfp_id = pfp_id;
    }

    public void setBanner_id(Long banner_id) {
        this.banner_id = banner_id;
    }

    public Long getBanner_id() {
        return banner_id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getMembers() {
        return teamMembers.stream().map(TeamMember::getUser).toList();
    }

    public List<TeamMember> getTeamMembers() {
        return teamMembers;
    }
}
