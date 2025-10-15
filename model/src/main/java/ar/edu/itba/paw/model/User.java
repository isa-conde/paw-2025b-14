package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.Game.Game;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_userid_seq")
    @SequenceGenerator(sequenceName = "users_userid_seq", name = "users_userid_seq", allocationSize = 1)
    @Column(name = "id")
    private long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "verified", nullable = false)
    private boolean verified;

    @Column(name = "bio")
    private String bio;

    @Column(name = "profile_picture_id")
    private Long pfp_id;

    @Column(name = "banner_id")
    private Long banner_id;

    @Column(name = "locale", nullable = false, length = 10)
    private String locale;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_favourites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private List<Game> favouriteGames = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<Team> ownedTeams = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMember> teamMembers = new ArrayList<>();

    public User(){}

    public User(final long id, final String username, final String email, String password, boolean verified, String bio, Long pfp_id, Long banner_id, String locale) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.verified = verified;
        this.bio = bio;
        this.pfp_id = pfp_id;
        this.banner_id = banner_id;
        this.locale = locale;
    }

    public User(final String username, final String email, final String password){
        this.username = username;
        this.email = email;
        this.password = password;
        this.verified = false;
        this.locale = "es";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getBio(){ return bio; }

    public Long getPfp_id() {return pfp_id;}

    public Long getBanner_id() { return banner_id; }

    public boolean isVerified() {
        return verified;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setBio(String bio){
        this.bio = bio;
    }

    public void setPfp_id(Long id){
        this.pfp_id = id;
    }

    public void setBanner_id(Long id){
        this.banner_id=id;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Game> getFavouriteGames() {
        return favouriteGames;
    }

    public void setFavouriteGames(List<Game> favoriteGames) {
        this.favouriteGames = favoriteGames;
    }

    public List<Team> getOwnedTeams() {
        return ownedTeams;
    }

    public void setOwnedTeams(List<Team> ownedTeams) {
        this.ownedTeams = ownedTeams;
    }

    public List<Team> getTeams() {
        return teamMembers.stream().map(TeamMember::getTeam).toList();
    }

    public List<TeamMember> getTeamMembers() {
        return teamMembers;
    }
}
