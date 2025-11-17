package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.model.Match.PointsPair;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class ParticipantHibernateDao implements ParticipantDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public void joinTournamentUser(long userId, long tournamentId) {
        Participant participant = new Participant(em.getReference(Tournament.class, tournamentId));
        participant.setUser(em.getReference(User.class, userId));
        participant.setPoints(0);
        participant.setScoreDifference(0);
        participant.setHasRated(false);
        em.persist(participant);
    }

    @Override
    public void joinTournamentUserWithTeam(long userId, long tournamentId, long teamId) {
        Participant participant = new Participant(em.getReference(Tournament.class, tournamentId));
        participant.setUser(em.getReference(User.class, userId));
        participant.setTeam(em.getReference(Team.class, teamId));
        participant.setPoints(0);
        participant.setHasRated(false);
        em.persist(participant);
    }

    @Override
    public void joinTournamentTeam(long tournamentId, long teamId) {
        Participant participant = new Participant(em.getReference(Tournament.class, tournamentId));
        participant.setTeam(em.getReference(Team.class, teamId));
        participant.setPoints(0);
        participant.setScoreDifference(0);
        participant.setHasRated(false);
        em.persist(participant);
    }

    @Override
    public Participant getTournamentParticipantById(long tournamentId, Long participantId, int teamSize) {
        String jpql;

        if (teamSize > 1) {
            jpql = """
            SELECT p
            FROM Participant p
            JOIN FETCH p.team t
            WHERE p.tournament.id = :tournamentId
              AND p.id = :participantId
        """;
        } else {
            jpql = """
            SELECT p
            FROM Participant p
            JOIN FETCH p.user u
            WHERE p.tournament.id = :tournamentId
              AND p.id = :participantId
        """;
        }

        TypedQuery<Participant> query = em.createQuery(jpql, Participant.class);
        query.setParameter("tournamentId", tournamentId);
        query.setParameter("participantId", participantId);

        List<Participant> toReturn = query.getResultList();
        fillParticipantTransientFields(toReturn);
        return toReturn.stream().findFirst().orElse(null);
    }

    @Override
    public boolean hasJoined(long userId, long tournamentId) {
        String jpql = """
        SELECT COUNT(p)
        FROM Participant p
        WHERE p.user.id = :userId
          AND p.tournament.id = :tournamentId
    """;

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("userId", userId)
                .setParameter("tournamentId", tournamentId)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public boolean hasRated(long userId, long tournamentId) {
        String jpql = """
        SELECT COUNT(p)
        FROM Participant p
        WHERE p.user.id = :userId
          AND p.tournament.id = :tournamentId
          AND p.hasRated = true
    """;

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("userId", userId)
                .setParameter("tournamentId", tournamentId)
                .getSingleResult();

        return count != null && count > 0;
    }

    @Override
    public void leaveTournamentUser(long userId, long tournamentId) {
        String jpql = """
        DELETE FROM Participant p
        WHERE p.user.id = :userId
          AND p.tournament.id = :tournamentId
    """;

        em.createQuery(jpql)
                .setParameter("userId", userId)
                .setParameter("tournamentId", tournamentId)
                .executeUpdate();
    }

    @Override
    public void leaveTournamentTeam(long teamId, long tournamentId) {
        String jpql = """
        DELETE FROM Participant p
        WHERE p.team.id = :teamId
          AND p.tournament.id = :tournamentId
    """;

        em.createQuery(jpql)
                .setParameter("teamId", teamId)
                .setParameter("tournamentId", tournamentId)
                .executeUpdate();
    }

    @Override
    public void updateGroupNumberForUsers(long tournamentId, int groupNumber, List<Long> userIds) {
        String jpql = """
            UPDATE Participant p
            SET p.groupNumber = :groupNumber
            WHERE p.tournament.id = :tournamentId
              AND p.id IN :ids
        """;

        em.createQuery(jpql)
                .setParameter("tournamentId", tournamentId)
                .setParameter("groupNumber", groupNumber)
                .setParameter("ids", userIds)
                .executeUpdate();
    }

    @Override
    public void swapGroups(long tournamentId, long user1, long user2, int group1, int group2) {
        String jpql = """
        UPDATE Participant p
        SET p.groupNumber = CASE
            WHEN p.id = :id1 THEN :group2
            WHEN p.id = :id2 THEN :group1
            ELSE p.groupNumber
        END
        WHERE p.tournament.id = :tournamentId
          AND p.id IN (:id1, :id2)
        """;

        em.createQuery(jpql)
                .setParameter("id1", user1)
                .setParameter("id2", user2)
                .setParameter("group1", group1)
                .setParameter("group2", group2)
                .setParameter("tournamentId", tournamentId)
                .executeUpdate();
    }

    @Override
    public List<Participant> getTournamentParticipantsByPointsPair(
            long tournamentId, Integer groupNumber, PointsPair pointsPair, int teamSize) {

        if (teamSize > 1) {
            return getTournamentParticipantsByPointsPairTeam(tournamentId, groupNumber, pointsPair);
        } else {
            return getTournamentParticipantsByPointsPairUser(tournamentId, groupNumber, pointsPair);
        }
    }

    private List<Participant> getTournamentParticipantsByPointsPairUser(
            long tournamentId, Integer groupNumber, PointsPair pointsPair) {

        String jpql = """
        SELECT p FROM Participant p
        JOIN FETCH p.user u
        WHERE p.tournament.id = :tournamentId
          AND p.points = :points
          AND p.scoreDifference = :scoreDifference
    """;

        if (groupNumber != null) {
            jpql += " AND p.groupNumber = :groupNumber";
        }

        TypedQuery<Participant> query = em.createQuery(jpql, Participant.class);
        query.setParameter("tournamentId", tournamentId);
        query.setParameter("points", pointsPair.getPoints());
        query.setParameter("scoreDifference", pointsPair.getScoreDifference());

        if (groupNumber != null) {
            query.setParameter("groupNumber", groupNumber);
        }

        List<Participant> toReturn = query.getResultList();
        fillParticipantTransientFields(toReturn);
        return toReturn;
    }

    private List<Participant> getTournamentParticipantsByPointsPairTeam(
            long tournamentId, Integer groupNumber, PointsPair pointsPair) {

        String jpql = """
        SELECT p FROM Participant p
        JOIN FETCH p.team t
        WHERE p.tournament.id = :tournamentId
          AND p.points = :points
          AND p.scoreDifference = :scoreDifference
    """;

        if (groupNumber != null) {
            jpql += " AND p.groupNumber = :groupNumber";
        }

        TypedQuery<Participant> query = em.createQuery(jpql, Participant.class);
        query.setParameter("tournamentId", tournamentId);
        query.setParameter("points", pointsPair.getPoints());
        query.setParameter("scoreDifference", pointsPair.getScoreDifference());

        if (groupNumber != null) {
            query.setParameter("groupNumber", groupNumber);
        }
        List<Participant> toReturn = query.getResultList();
        fillParticipantTransientFields(toReturn);
        return toReturn;
    }

    @Override
    public PointsPair getTournamentMaxPointsPairGroup(long tournamentId, Integer group) {
        String jpql = "SELECT p.points, p.scoreDifference " +
                "FROM Participant p " +
                "WHERE p.tournament.id = :tournamentId AND p.points IS NOT NULL AND p.scoreDifference IS NOT NULL";

        if (group != null) {
            jpql += " AND p.groupNumber = :groupNumber";
        }

        jpql += " ORDER BY p.points DESC, p.scoreDifference DESC";

        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("tournamentId", tournamentId);
        if (group != null) {
            query.setParameter("groupNumber", group);
        }

        List<Object[]> results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        }

        Object[] row = results.get(0);
        Integer points = row[0] == null ? 0 : ((Number) row[0]).intValue();
        Integer scoreDiff = row[1] == null ? 0 : ((Number) row[1]).intValue();

        return new PointsPair(points, scoreDiff);
    }


    @Override
    public PointsPair getTournamentSecondMaxPointsPairGroup(long tournamentId, Integer group) {
        String jpql = "SELECT p.points, p.scoreDifference " +
                "FROM Participant p " +
                "WHERE p.tournament.id = :tournamentId";

        if (group != null) {
            jpql += " AND p.groupNumber = :groupNumber";
        }

        jpql += " ORDER BY p.points DESC, p.scoreDifference DESC";

        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("tournamentId", tournamentId);
        if (group != null) {
            query.setParameter("groupNumber", group);
        }

        query.setFirstResult(1);
        query.setMaxResults(1);

        List<Object[]> results = query.getResultList();
        if (results.isEmpty()) {
            return new PointsPair(0, 0);
        }

        Object[] row = results.get(0);
        int points = row[0] == null ? 0 : ((Number) row[0]).intValue();
        int scoreDiff = row[1] == null ? 0 : ((Number) row[1]).intValue();

        return new PointsPair(points, scoreDiff);
    }

    @Override
    public int getTournamentGroups(long tournamentId) {
        return em.createQuery("SELECT COUNT(DISTINCT p.groupNumber) FROM Participant p WHERE p.tournament.id = :tournamentId", Long.class)
                .setParameter("tournamentId", tournamentId)
                .getSingleResult()
                .intValue();
    }

    @Override
    public List<Participant> getTournamentParticipantUsers(long tournamentId) {
        TypedQuery<Participant> query = em.createQuery("SELECT p FROM Participant p JOIN FETCH p.user u WHERE p.tournament.id = :tournamentId", Participant.class);
        query.setParameter("tournamentId", tournamentId);
        List<Participant> toReturn = query.getResultList();

        fillParticipantTransientFields(toReturn);
        return toReturn;
    }

    @Override
    public Integer getGroupNumber(long tournamentId, long userId) {
        String jpql = "SELECT p.groupNumber FROM Participant p WHERE p.id = :id AND p.tournament.id = :tournamentId";
        TypedQuery<Integer> query = em.createQuery(jpql, Integer.class);
        query.setParameter("tournamentId", tournamentId);
        query.setParameter("id", userId);
        return query.getSingleResult();
    }

    @Override
    public void sumPoints(long tournamentId, long userId, int points, int scoreDifference, int teamSize) {
        String jpql;

        if (teamSize > 1) {
            jpql = """
            UPDATE Participant p
            SET p.points = p.points + :points , p.scoreDifference = p.scoreDifference + :scoreDifference
            WHERE p.id = :id
              AND p.tournament.id = :tournamentId
              AND p.user IS NULL
        """;
        } else {
            jpql = """
            UPDATE Participant p
            SET p.points = p.points + :points , p.scoreDifference = p.scoreDifference + :scoreDifference
            WHERE p.id = :id
              AND p.tournament.id = :tournamentId
              AND p.team IS NULL
        """;
        }

        em.createQuery(jpql)
                .setParameter("points", points)
                .setParameter("scoreDifference", scoreDifference)
                .setParameter("id", userId)
                .setParameter("tournamentId", tournamentId)
                .executeUpdate();
    }

    @Override
    public List<Participant> getTournamentParticipantTeams(long tournamentId) {
        TypedQuery<Participant> query = em.createQuery("SELECT p FROM Participant p JOIN FETCH p.team t WHERE p.tournament.id = :tournamentId AND p.user IS NULL", Participant.class);
        query.setParameter("tournamentId", tournamentId);
        List<Participant> toReturn = query.getResultList();

        fillParticipantTransientFields(toReturn);
        return toReturn;
    }

    @Override
    public void updateHasRated(long userId, long tournamentId) {
        String jpql = """
        UPDATE Participant p
        SET p.hasRated = true
        WHERE p.user.id = :userId
        AND p.tournament.id = :tournamentId
    """;

        em.createQuery(jpql)
                .setParameter("userId", userId)
                .setParameter("tournamentId", tournamentId)
                .executeUpdate();
    }

    private void fillParticipantTransientFields(List<Participant> participants) {
        if (participants == null) return;

        participants.forEach(p -> {
            if (p.getUser() == null) {
                p.setName(p.getTeam().getName());
                p.setPfpId(p.getTeam().getPfpId());
            } else {
                p.setName(p.getUser().getUsername());
                p.setPfpId(p.getUser().getPfpId());
            }
        });
    }
}
