package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;

public interface TeamMemberDao {

    void addMember(Long team_id, Long user_id);

    Boolean isMember(Long team_id, Long user_id);

}
