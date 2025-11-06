package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.TeamMemberDao;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.TeamMember;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.ids.TeamMemberId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class TeamMemberHibernateDao implements TeamMemberDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchHibernateDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public void addMember(Long teamId, Long userId) {
        Team team = em.find(Team.class, teamId);
        User user = em.getReference(User.class, userId);

        if (team == null) {
            LOGGER.warn("Team not found for teamId={}",teamId);
            throw new IllegalArgumentException();
        }
        if (user == null){
            LOGGER.warn("User not found for userId={}", userId);
            throw new IllegalArgumentException();
        }

        TeamMemberId id = new TeamMemberId(teamId, userId);
        TeamMember teamMember = new TeamMember(id, team, user);
        team.getTeamMembers().add(teamMember);
        user.getTeamMembers().add(teamMember);
        em.persist(teamMember);
    }

    @Override
    public Boolean isMember(Long teamId, Long userId) {
        return em.find(TeamMember.class, new TeamMemberId(teamId, userId)) != null;
    }

}
