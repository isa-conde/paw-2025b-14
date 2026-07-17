package ar.edu.itba.paw.model.ids;

import ar.edu.itba.paw.model.enums.Platform;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Embeddable
public class UserAccountId implements Serializable {

    @Column(name = "user_id")
    private long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    private Platform platform;

    public UserAccountId(){}

    public UserAccountId(long userId, Platform platform){
        this.platform=platform;
        this.userId=userId;
    }

    public long getUserId() {
        return userId;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }
}
