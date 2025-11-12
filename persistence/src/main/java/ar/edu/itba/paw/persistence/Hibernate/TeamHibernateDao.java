package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.TeamDao;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class TeamHibernateDao implements TeamDao {
    private static final int GRID_PAGE_SIZE = 9;

    @PersistenceContext
    private EntityManager em;

    @Override
    public Team create(String name, Long pfpId, Long bannerId, Long ownerId) {
        User owner = em.find(User.class, ownerId);
        if (owner == null) {
            throw new IllegalArgumentException();
        }

        Team team = new Team(name, pfpId, bannerId);
        team.setOwner(owner);
        em.persist(team);
        return team;
    }

    @Override
    public Optional<Team> getById(Long id) {
        return Optional.ofNullable(em.find(Team.class, id));
    }

    @Override
    public List<Long> getPastTournaments(Long teamId, Integer page) {
        return findTeamTournamentIds(teamId, true, page);
    }

    @Override
    public List<Long> getActiveTournaments(Long teamId, Integer page) {
        return findTeamTournamentIds(teamId, false, page);
    }

    private List<Long> findTeamTournamentIds(Long teamId, Boolean isFinished, Integer page) {
        String jpql = """
            SELECT DISTINCT p.tournament.id
            FROM Participant p
            WHERE p.team.id = :teamId
            AND p.tournament.isFinished = :isFinished
            AND p.user IS NULL
        """;

        return em.createQuery(jpql, Long.class)
                .setParameter("teamId", teamId)
                .setParameter("isFinished", isFinished)
                .setMaxResults(GRID_PAGE_SIZE)
                .setFirstResult(page*GRID_PAGE_SIZE)
                .getResultList();
    }

    @Override
    public Long getActivePages(Long teamId) {
        return getTeamTournamentsPages(teamId, false);
    }

    @Override
    public Long getPastPages(Long teamId) {
        return getTeamTournamentsPages(teamId, true);
    }

    private Long getTeamTournamentsPages(Long teamId, Boolean isFinished){
        String jpql = """
            SELECT DISTINCT COUNT (DISTINCT (p.tournament.id))
            FROM Participant p
            WHERE p.team.id = :teamId
            AND p.tournament.isFinished = :isFinished
            AND p.user IS NULL
        """;

        Long total = em.createQuery(jpql, Long.class)
                .setParameter("teamId", teamId)
                .setParameter("isFinished", isFinished)
                .getSingleResult();
        return (long) Math.ceil((double) total / GRID_PAGE_SIZE);
    }

    @Override
    public List<Team> getUserTeams(Long userId) {
        return em.find(User.class, userId).getTeams();
    }

    @Override
    public void updateTeam(Long teamId, String name, Long pfpId, Long bannerId) {
        Team team = em.find(Team.class, teamId);
        if (name != null){
            team.setName(name);
        }if (pfpId != null){
            team.setPfpId(pfpId);
        }if (bannerId != null){
            team.setBannerId(bannerId);
        }
        em.persist(team);
    }

    @Override
    public Boolean teamNameTaken(String name) {
        String jpql = "SELECT COUNT(t) FROM Team t WHERE t.name = :name";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public List<Team> searchByName(String name) {
        TypedQuery<Team> query = em.createQuery(
                "SELECT t FROM Team t WHERE LOWER(t.name) LIKE CONCAT('%', LOWER(:name), '%')",
                Team.class
        );
        query.setParameter("name", name);
        return query.getResultList();
    }

    @Override
    public List<Team> getUserTeamsBySizeNotInTournament(Long userId, Long tournamentId, Long minSize) {
        String jpql = """
        SELECT DISTINCT t
        FROM Team t
        WHERE EXISTS (
            SELECT 1
            FROM TeamMember tm
            WHERE tm.team = t
              AND tm.user.id = :userId
        )
        AND (
            SELECT COUNT(tm2)
            FROM TeamMember tm2
            WHERE tm2.team = t
        ) >= :minSize
        AND NOT EXISTS (
            SELECT 1
            FROM Participant p
            WHERE p.tournament.id = :tournamentId
              AND p.team = t
        )
        ORDER BY t.id
    """;

        return em.createQuery(jpql, Team.class)
                .setParameter("userId", userId)
                .setParameter("minSize", minSize)
                .setParameter("tournamentId", tournamentId)
                .getResultList();
    }
}
