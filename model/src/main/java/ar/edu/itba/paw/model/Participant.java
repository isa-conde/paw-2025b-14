package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.Tournament.Tournament;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "participant")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "participant_user_id_seq")
    @SequenceGenerator(sequenceName = "participant_user_id_seq", name = "participant_user_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "points", nullable = false)
    private Integer points;

    @Column(name = "score_difference", nullable = false)
    private Integer score_difference;

    @Column(name = "group_number")
    private Integer group_number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Transient
    private String name;
    @Transient
    private Long pfp_id;

    @OneToMany(mappedBy = "local")
    private List<Match> localMatch;

    @OneToMany(mappedBy = "visitor")
    private List<Match> visitorMatch;


    public Participant(final Long id, final String name, Integer points, Integer score_difference, Integer group_number, Long pfp_id) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.score_difference = score_difference;
        this.group_number = group_number;
        this.pfp_id = pfp_id;
    }

    public Participant(Tournament tournament){
        this.tournament = tournament;
    }

    public Participant() {}

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

    public void setName(String name) {
        this.name = name;
    }

    public void setPfp_id(Long pfp_id) {
        this.pfp_id = pfp_id;
    }

    public void setGroup_number(Integer group_number) {
        this.group_number = group_number;
    }

    public Integer getGroup_number() {
        return group_number;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Match> getLocalMatch() {
        return localMatch;
    }

    public void setLocalMatch(List<Match> localMatch) {
        this.localMatch = localMatch;
    }

    public List<Match> getVisitorMatch() {
        return visitorMatch;
    }

    public void setVisitorMatch(List<Match> visitorMatch) {
        this.visitorMatch = visitorMatch;
    }

    public Integer getScore_difference() {
        return score_difference;
    }

    public void setScore_difference(Integer score_difference) {
        this.score_difference = score_difference;
    }
}
