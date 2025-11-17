package ar.edu.itba.paw.interfaces.persistence;

public interface TeamMemberDao {

    void addMember(long teamId, long userId);

    boolean isMember(long teamId, long userId);

}
