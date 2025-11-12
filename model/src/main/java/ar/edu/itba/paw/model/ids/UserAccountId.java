package ar.edu.itba.paw.model.ids;

import ar.edu.itba.paw.model.enums.Platform;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserAccountId implements Serializable {

    private long userId;
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

}
