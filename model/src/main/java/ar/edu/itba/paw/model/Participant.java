package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.Match.Match;
import ar.edu.itba.paw.model.Tournament;

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

    @Column(name = "score_difference")
    private Integer scoreDifference;

    @Column(name = "group_number")
    private Integer groupNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(name = "has_rated")
    private Boolean hasRated;

    @Transient
    private String name;
    @Transient
    private Long pfpId;

    @OneToMany(mappedBy = "local")
    private List<Match> localMatch;

    @OneToMany(mappedBy = "visitor")
    private List<Match> visitorMatch;


    public Participant(final Long id, final String name, Integer points, Integer scoreDifference, Integer groupNumber, Long pfpId) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.scoreDifference = scoreDifference;
        this.groupNumber = groupNumber;
        this.pfpId = pfpId;
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

    public Long getPfpId() {
        return pfpId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPfpId(Long pfpId) {
        this.pfpId = pfpId;
    }

    public Integer getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(Integer groupNumber) {
        this.groupNumber = groupNumber;
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

    public Integer getScoreDifference() {
        return scoreDifference;
    }

    public void setScoreDifference(Integer scoreDifference) {
        this.scoreDifference = scoreDifference;
    }

    public Boolean getHasRated() {
        return hasRated;
    }

    public void setHasRated(Boolean hasRated) {
        this.hasRated = hasRated;
    }

    @Override
    public String toString() {
        return name;
    }
}
