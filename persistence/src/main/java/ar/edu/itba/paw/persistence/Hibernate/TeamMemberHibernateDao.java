package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.TeamMemberDao;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.TeamMember;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.ids.TeamMemberId;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class TeamMemberHibernateDao implements TeamMemberDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void addMember(long teamId, long userId) {
        Team team = em.getReference(Team.class, teamId);
        User user = em.getReference(User.class, userId);

        TeamMemberId id = new TeamMemberId(teamId, userId);
        TeamMember teamMember = new TeamMember(id, team, user);
        em.persist(teamMember);
    }

    @Override
    public boolean isMember(long teamId, long userId) {
        return em.find(TeamMember.class, new TeamMemberId(teamId, userId)) != null;
    }

}
