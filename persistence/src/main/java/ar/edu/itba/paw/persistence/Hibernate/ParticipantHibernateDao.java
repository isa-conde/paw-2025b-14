package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.model.Match.PointsPair;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class ParticipantHibernateDao implements ParticipantDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public void joinTournamentUser(Long user_id, Long tournament_id) {
        Participant participant = new Participant(em.getReference(Tournament.class, tournament_id));
        participant.setUser(em.getReference(User.class, user_id));
        participant.setPoints(0);
        participant.setScore_difference(0);
        em.persist(participant);
    }

    @Override
    public void joinTournamentUserWithTeam(Long user_id, Long tournament_id, Long team_id) {
        Participant participant = new Participant(em.getReference(Tournament.class, tournament_id));
        participant.setUser(em.getReference(User.class, user_id));
        participant.setTeam(em.getReference(Team.class, team_id));
        em.persist(participant);
    }

    @Override
    public void joinTournamentTeam(Long tournamentId, Long teamId) {
        Participant participant = new Participant(em.getReference(Tournament.class, tournamentId));
        participant.setTeam(em.getReference(Team.class, teamId));
        participant.setPoints(0);
        participant.setScore_difference(0);
        em.persist(participant);
    }

    @Override
    @Transactional(readOnly = true)
    public Participant getTournamentParticipantById(Long tournament_id, Long participant_id, Integer teamSize) {
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
        query.setParameter("tournamentId", tournament_id);
        query.setParameter("participantId", participant_id);

        List<Participant> toReturn = query.getResultList();
        fillParticipantTransientFields(toReturn);
        return toReturn.stream().findFirst().orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public Boolean hasJoined(Long userId, Long tournamentId) {
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
    public Boolean hasRated(Long userId, Long tournamentId) {
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
    public void leaveTournamentUser(Long user_id, Long tournament_id) {
        String jpql = """
        DELETE FROM Participant p
        WHERE p.user.id = :userId
          AND p.tournament.id = :tournamentId
    """;

        em.createQuery(jpql)
                .setParameter("userId", user_id)
                .setParameter("tournamentId", tournament_id)
                .executeUpdate();
    }

    @Override
    public void leaveTournamentTeam(Long team_id, Long tournament_id) {
        String jpql = """
        DELETE FROM Participant p
        WHERE p.team.id = :teamId
          AND p.tournament.id = :tournamentId
    """;

        em.createQuery(jpql)
                .setParameter("teamId", team_id)
                .setParameter("tournamentId", tournament_id)
                .executeUpdate();
    }

    @Override
    public void updateGroupNumberForUsers(long tournamentId, int groupNumber, List<Long> userIds, Integer teamSize) {
        String jpql;

        if (teamSize != null && teamSize > 1) {
            jpql = """
            UPDATE Participant p
            SET p.group_number = :groupNumber
            WHERE p.tournament.id = :tournamentId
              AND p.team.id IN :ids
        """;
        } else {
            jpql = """
            UPDATE Participant p
            SET p.group_number = :groupNumber
            WHERE p.tournament.id = :tournamentId
              AND p.user.id IN :ids
        """;
        }

        em.createQuery(jpql)
                .setParameter("tournamentId", tournamentId)
                .setParameter("groupNumber", groupNumber)
                .setParameter("ids", userIds)
                .executeUpdate();
    }

    @Override
    public void swapGroups(Long tournament_id, Long user1, Long user2, Integer group1, Integer group2, Integer teamSize) {
        String idField = (teamSize != null && teamSize > 1) ? "team.id" : "user.id";

        String jpql = String.format("""
        UPDATE Participant p
        SET p.group_number = CASE
            WHEN p.%s = :id1 THEN :group2
            WHEN p.%s = :id2 THEN :group1
            ELSE p.group_number
        END
        WHERE p.tournament.id = :tournamentId
          AND p.%s IN (:id1, :id2)
        """, idField, idField, idField);

        em.createQuery(jpql)
                .setParameter("id1", user1)
                .setParameter("id2", user2)
                .setParameter("group1", group1)
                .setParameter("group2", group2)
                .setParameter("tournamentId", tournament_id)
                .executeUpdate();
    }

    @Override
    public List<Participant> getTournamentParticipantsByPointsPair(
            Long tournamentId, Integer groupNumber, PointsPair pointsPair, Integer teamSize) {

        if (teamSize != null && teamSize > 1) {
            return getTournamentParticipantsByPointsPairTeam(tournamentId, groupNumber, pointsPair);
        } else {
            return getTournamentParticipantsByPointsPairUser(tournamentId, groupNumber, pointsPair);
        }
    }

    private List<Participant> getTournamentParticipantsByPointsPairUser(
            Long tournamentId, Integer groupNumber, PointsPair pointsPair) {

        String jpql = """
        SELECT p FROM Participant p
        JOIN FETCH p.user u
        WHERE p.tournament.id = :tournamentId
          AND p.points = :points
          AND p.score_difference = :scoreDifference
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
            Long tournamentId, Integer groupNumber, PointsPair pointsPair) {

        String jpql = """
        SELECT p FROM Participant p
        JOIN FETCH p.team t
        WHERE p.tournament.id = :tournamentId
          AND p.points = :points
          AND p.score_difference = :scoreDifference
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
    public PointsPair getTournamentMaxPointsPairGroup(Long tournamentId, Integer group) {
        String jpql = "SELECT p.points, p.score_difference " +
                "FROM Participant p " +
                "WHERE p.tournament.id = :tournamentId";

        if (group != null) {
            jpql += " AND p.groupNumber = :groupNumber";
        }

        jpql += " ORDER BY p.points DESC, p.score_difference DESC";

        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("tournamentId", tournamentId);
        if (group != null) {
            query.setParameter("groupNumber", group);
        }

        query.setMaxResults(1);
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
    public PointsPair getTournamentSecondMaxPointsPairGroup(Long tournamentId, Integer group) {
        String jpql = "SELECT p.points, p.score_difference " +
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
        Integer points = row[0] == null ? 0 : ((Number) row[0]).intValue();
        Integer scoreDiff = row[1] == null ? 0 : ((Number) row[1]).intValue();

        return new PointsPair(points, scoreDiff);
    }

    @Override
    public Integer getTournamentGroups(Long tournamentId) {
        return em.createQuery("SELECT COUNT(DISTINCT p.group_number) FROM Participant p WHERE p.tournament.id = :tournamentId", Long.class)
                .setParameter("tournamentId", tournamentId)
                .getSingleResult()
                .intValue();
    }

    @Override
    public List<Participant> getTournamentParticipantUsers(Long tournament_id) {
        TypedQuery<Participant> query = em.createQuery("SELECT p FROM Participant p JOIN FETCH p.user u WHERE p.tournament.id = :tournamentId", Participant.class);
        query.setParameter("tournamentId", tournament_id);
        List<Participant> toReturn = query.getResultList();

        fillParticipantTransientFields(toReturn);
        return toReturn;
    }

    @Override
    public Integer getGroupNumber(Long tournament_id, Long user_id, Integer teamSize) {
        String idField = (teamSize != null && teamSize > 1) ? "p.team.id" : "p.user.id";

        String jpql = "SELECT p.group_number FROM Participant p WHERE " + idField + " = :id AND p.tournament.id = :tournamentId";
        TypedQuery<Integer> query = em.createQuery(jpql, Integer.class);
        query.setParameter("tournamentId", tournament_id);
        query.setParameter("id", user_id);
        return query.getSingleResult();
    }

    @Override
    public void sumPoints(Long tournamentId, Long userId, Integer points, Integer scoreDifference, Integer teamSize) {
        String jpql;

        if (teamSize != null && teamSize > 1) {
            jpql = """
            UPDATE Participant p
            SET p.points = p.points + :points , p.score_difference = p.score_difference + :scoreDifference
            WHERE p.id = :id
              AND p.tournament.id = :tournamentId
              AND p.user IS NULL
        """;
        } else {
            jpql = """
            UPDATE Participant p
            SET p.points = p.points + :points , p.score_difference = p.score_difference + :scoreDifference
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
    public List<Participant> getTournamentParticipantTeams(Long tournament_id) {
        TypedQuery<Participant> query = em.createQuery("SELECT p FROM Participant p JOIN FETCH p.team t WHERE p.tournament.id = :tournamentId AND p.user IS NULL", Participant.class);
        query.setParameter("tournamentId", tournament_id);
        List<Participant> toReturn = query.getResultList();

        fillParticipantTransientFields(toReturn);
        return toReturn;
    }

    @Override
    public void updateHasRated(Long userId, Long tournamentId) {
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
                p.setPfp_id(p.getTeam().getPfp_id());
            } else {
                p.setName(p.getUser().getUsername());
                p.setPfp_id(p.getUser().getPfp_id());
            }
        });
    }
}
