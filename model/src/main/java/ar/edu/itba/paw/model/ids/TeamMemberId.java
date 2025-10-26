package ar.edu.itba.paw.model.ids;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class TeamMemberId implements Serializable {
    private Long teamId;
    private Long userId;

    public TeamMemberId(){}

    public TeamMemberId(Long teamId, Long userId){
        this.teamId = teamId;
        this.userId = userId;
    }
}
