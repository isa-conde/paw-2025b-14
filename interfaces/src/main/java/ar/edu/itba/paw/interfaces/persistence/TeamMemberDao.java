package ar.edu.itba.paw.interfaces.persistence;

public interface TeamMemberDao {

    void addMember(Long teamId, Long userId);

    boolean isMember(Long teamId, Long userId);

}
