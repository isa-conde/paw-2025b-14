package ar.edu.itba.paw.interfaces.persistence;

public interface TeamMemberDao {

    void addMember(Long team_id, Long user_id);

}
