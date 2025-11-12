package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.enums.Platform;
import ar.edu.itba.paw.model.ids.UserAccountId;

import javax.persistence.*;

@Entity
@Table(name = "user_account")
public class UserAccount {

    @EmbeddedId
    private UserAccountId userAccountId;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("platform")
    @Enumerated(EnumType.STRING)
    @Column(name = "plarform", nullable = false)
    private Platform platform;

    @Column(name = "username", nullable = false)
    private String username;

    protected UserAccount(){}

    public UserAccount(User user, Platform platform, String username){
        this.user = user;
        this.platform = platform;
        this. username = username;
    }

    public User getUser() {
        return user;
    }

    public Platform getPlatform() {
        return platform;
    }

    public String getUsername() {
        return username;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
