package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Team;

import java.util.List;

public interface TeamMemberDao {

    void AddMember(Long team_id, Long user_id);

    Boolean isMember(Long team_id, Long user_id);

    List<Long> getTeamMembers(Long team_id);

}
