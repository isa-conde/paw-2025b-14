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
    private Long pfpId;
    @Column(name = "banner_id")
    private Long bannerId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMember> teamMembers = new ArrayList<>();

    public Team(String name, Long pfpId, Long bannerId) {
        this.name = name;
        this.pfpId = pfpId;
        this.bannerId = bannerId;
    }

    public Team(Long id, String name, Long pfpId, Long bannerId) {
        this.id = id;
        this.name = name;
        this.pfpId = pfpId;
        this.bannerId = bannerId;
    }

    public Team() {}

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

    public Long getPfpId() {
        return pfpId;
    }

    public void setPfpId(Long pfpId) {
        this.pfpId = pfpId;
    }

    public void setBannerId(Long bannerId) {
        this.bannerId = bannerId;
    }

    public Long getBannerId() {
        return bannerId;
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
