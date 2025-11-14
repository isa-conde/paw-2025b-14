package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.enums.Platform;
import ar.edu.itba.paw.model.ids.UserAccountId;

import javax.persistence.*;

@Entity
@Table(name = "user_account")
public class UserAccount {

    @EmbeddedId
    private UserAccountId userAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "username", nullable = false)
    private String username;

    protected UserAccount(){}

    public UserAccount(User user, Platform platform, String username){
        this.user = user;
        this.username = username;
        this.userAccountId = new UserAccountId(user.getId(), platform);
    }

    public User getUser() {
        return user;
    }

    public Platform getPlatform() {
        return userAccountId.getPlatform();
    }

    public String getUsername() {
        return username;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPlatform(Platform platform) {
        if (userAccountId == null) {
            userAccountId = new UserAccountId();
        }
        userAccountId.setPlatform(platform);
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
