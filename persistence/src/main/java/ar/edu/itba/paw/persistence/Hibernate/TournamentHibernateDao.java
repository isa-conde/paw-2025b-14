package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.Rules;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TournamentHibernateDao implements TournamentDao {

    private static final int PAGE_SIZE = 9;

    private static final int TOP_GAMES_LIMIT = 3;
    private static final int TOURNAMENTS_PER_GAME = 9;

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Tournament> findById(Long id) {
        return Optional.ofNullable(em.find(Tournament.class, id));
    }

    @Override
    public List<Tournament> findTournaments(TournamentFilter tournamentFilter, Long page) {
        StringBuilder idJpql = new StringBuilder("SELECT DISTINCT t.id FROM Tournament t");
        Map<String, Object> params = new HashMap<>();

        idJpql.append(buildTournamentFilterJpql(tournamentFilter, params));

        TypedQuery<Long> idQuery = em.createQuery(idJpql.toString(), Long.class);
        params.forEach(idQuery::setParameter);

        idQuery.setFirstResult((int) (page * PAGE_SIZE));
        idQuery.setMaxResults(PAGE_SIZE);

        List<Long> tournamentIds = idQuery.getResultList();

        if (tournamentIds.isEmpty()) return Collections.emptyList();

        TypedQuery<Tournament> query = em.createQuery(
                "SELECT DISTINCT t FROM Tournament t " +
                        "WHERE t.id IN :ids " +
                        "ORDER BY t.startDate ASC", Tournament.class
        );
        query.setParameter("ids", tournamentIds);

        return query.getResultList();
    }

    private String buildTournamentFilterJpql(TournamentFilter filter, Map<String, Object> params) {
        StringBuilder jpql = new StringBuilder(" WHERE t.openInscriptions = true");

        if (filter.getName() != null) {
            jpql.append(" AND LOWER(t.name) LIKE LOWER(:name)");
            params.put("name", "%" + filter.getName() + "%");
        }
        if (filter.getGameId() != null) {
            jpql.append(" AND t.game.id = :gameId");
            params.put("gameId", filter.getGameId());
        }
        if (filter.getElo() != null) {
            jpql.append(" AND t.elo = :elo");
            params.put("elo", filter.getElo());
        }
        if (filter.getRegion() != null) {
            jpql.append(" AND t.region = :region");
            params.put("region", filter.getRegion());
        }
        if (filter.getFormat() != null) {
            jpql.append(" AND t.format.id = :formatId");
            params.put("formatId", filter.getFormat());
        }
        if (filter.getStructure() != null) {
            jpql.append(" AND t.structure = :structure");
            params.put("structure", filter.getStructure());
        }
        if (filter.getStartDate() != null) {
            jpql.append(" AND t.startDate <= :startDate");
            params.put("startDate", filter.getStartDate());
        }
        if (filter.getEndDate() != null) {
            jpql.append(" AND t.endDate <= :endDate");
            params.put("endDate", filter.getEndDate());
        }
        if (filter.getPlayersPerTeam() != null) {
            jpql.append(" AND t.formatEntity.playersPerTeam = :playersPerTeam");
            params.put("playersPerTeam", filter.getPlayersPerTeam());
        }
        if (filter.getGenre() != null) {
            jpql.append(" AND t.game.genre = :genre");
            params.put("genre", filter.getGenre());
        }

        return jpql.toString();
    }

    @Override
    public Tournament create(Long creatorId, String name, Long gameId, Region region, Elo elo, LocalDate startDate, LocalDate endDate, String format, Structure structure, Integer maxParticipants, Long imageId, Boolean openInscriptions, Boolean isFinished, Long formatId, Long rulesId) {
        Tournament t = new Tournament(em.getReference(User.class, creatorId), name, em.getReference(Game.class,  gameId), region, startDate, endDate, format, structure, maxParticipants, imageId, openInscriptions, isFinished, em.getReference(GameFormat.class, formatId));
        t.setTournamentStarted(false);
        t.setElo(elo);
        if (rulesId != null){
            t.setRules(em.getReference(Rules.class, rulesId));
        }
        em.persist(t);
        return t;
    }

    @Override
    public Structure getTournamentStructure(Long tournamentId) {
        Tournament t = em.find(Tournament.class, tournamentId);
        return t.getStructure();
    }

    @Override
    public List<Tournament> findByCreator(Long creatorId, Long page, Boolean isFinished) {
        Query idQuery = em.createNativeQuery(
                "SELECT t.id FROM Tournament t WHERE t.creator_id = ?1 AND t.is_finished = ?2 ORDER BY t.start_date ASC"
        );
        idQuery.setParameter(1, creatorId);
        idQuery.setParameter(2, isFinished);
        idQuery.setFirstResult((int) (page * PAGE_SIZE));
        idQuery.setMaxResults(PAGE_SIZE);

        @SuppressWarnings("unchecked")
        List<Number> tournamentIds = idQuery.getResultList();

        if (tournamentIds.isEmpty()) return Collections.emptyList();

        TypedQuery<Tournament> fullQuery = em.createQuery(
                "SELECT DISTINCT t FROM Tournament t " +
                        "WHERE t.id IN :ids " +
                        "ORDER BY t.startDate ASC",
                Tournament.class
        );
        fullQuery.setParameter("ids", tournamentIds.stream().map(Number::longValue).toList());

        return fullQuery.getResultList();
    }

    @Override
    public void setFinished(Long tournamentId) {
        Tournament t = em.find(Tournament.class, tournamentId);
        t.setIsFinished(true);
        em.persist(t);
    }

    @Override
    public List<Tournament> findUserActiveTournaments(Long userId, Long page) {
        return findUserTournaments(userId, false, page);
    }

    @Override
    public List<Tournament> findUserPastTournaments(Long userId, Long page) {
        return findUserTournaments(userId, true, page);
    }

    private List<Tournament> findUserTournaments(Long userId, Boolean isFinished, Long page) {
        Query idQuery = em.createNativeQuery(
                "SELECT t.id " +
                    "FROM Tournament t " +
                    "WHERE t.is_finished = ?1 " +
                    "  AND t.id IN ( " +
                        "    SELECT p.tournament_id " +
                        "    FROM Participant p " +
                        "    WHERE p.user_id = ?2 " +
                    ") " +
                    "ORDER BY t.start_date ASC"

        );
        idQuery.setParameter(1, isFinished);
        idQuery.setParameter(2, userId);
        idQuery.setFirstResult((int)(page * PAGE_SIZE));
        idQuery.setMaxResults(PAGE_SIZE);

        @SuppressWarnings("unchecked")
        List<Number> tournamentIds = idQuery.getResultList();
        if (tournamentIds.isEmpty()) return Collections.emptyList();

        TypedQuery<Tournament> fullQuery = em.createQuery(
                "SELECT DISTINCT t FROM Tournament t " +
                        "WHERE t.id IN :ids " +
                        "ORDER BY t.startDate ASC",
                Tournament.class
        );
        fullQuery.setParameter("ids", tournamentIds.stream().map(Number::longValue).toList());

        return fullQuery.getResultList();
    }

    @Override
    public void closeInscriptions(Long tournamentId) {
        Tournament t = em.find(Tournament.class, tournamentId);
        t.setOpenInscriptions(false);
        em.persist(t);
    }

    @Override
    public List<Tournament> searchByName(String name) {
        TypedQuery<Tournament> query = em.createQuery("SELECT t FROM Tournament t WHERE LOWER(t.name) LIKE CONCAT('%', LOWER(:name), '%')", Tournament.class);
        query.setParameter("name", name);
        return query.getResultList();
    }

    @Override
    public void startTournament(Long tournamentId) {
        Tournament t = em.find(Tournament.class, tournamentId);
        t.setTournamentStarted(true);
        em.persist(t);
    }

    @Override
    public Map<Long, List<Tournament>> getUnfilteredTournamentPages(Long page) {
        Query topGamesQuery = em.createNativeQuery(
                "SELECT game_id " +
                        "FROM tournament " +
                        "GROUP BY game_id " +
                        "ORDER BY COUNT(*) DESC"
        );
        topGamesQuery.setFirstResult((int)(page * TOP_GAMES_LIMIT));
        topGamesQuery.setMaxResults(TOP_GAMES_LIMIT);

        @SuppressWarnings("unchecked")
        List<Number> topGameIds = topGamesQuery.getResultList();

        if (topGameIds.isEmpty()) return Collections.emptyMap();

        Query tournamentIdsQuery = em.createNativeQuery(
                "SELECT t.id " +
                        "FROM Tournament t " +
                        "WHERE t.game_id IN ?1 " +
                        "AND t.open_inscriptions = true " +
                        "ORDER BY t.game_id, t.start_date ASC ");
        tournamentIdsQuery.setParameter(1, topGameIds.stream().map(Number::longValue).toList());
        tournamentIdsQuery.setMaxResults(TOURNAMENTS_PER_GAME);

        @SuppressWarnings("unchecked")
        List<Number> tournamentIds = tournamentIdsQuery.getResultList();
        if (tournamentIds.isEmpty()) return Collections.emptyMap();

        TypedQuery<Tournament> fullQuery = em.createQuery(
                "SELECT DISTINCT t FROM Tournament t " +
                        "LEFT JOIN FETCH t.game g " +
                        "WHERE t.id IN :ids " +
                        "ORDER BY t.game.id, t.startDate ASC",
                Tournament.class
        );
        fullQuery.setParameter("ids", tournamentIds.stream().map(Number::longValue).toList());

        List<Tournament> tournaments = fullQuery.getResultList();

        Map<Long, List<Tournament>> grouped = tournaments.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getGame().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        Map<Long, List<Tournament>> orderedMap = new LinkedHashMap<>();
        for (Number gameId : topGameIds) {
            Long gid = gameId.longValue();
            if (grouped.containsKey(gid)) {
                orderedMap.put(gid, grouped.get(gid));
            }
        }

        return orderedMap;
    }

    @Override
    public Integer getPageAmount(Integer pageSize, TournamentFilter tf) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder jpql;
        if (tf.isEmpty()){
            jpql = new StringBuilder("SELECT COUNT(DISTINCT t.game.id) FROM Tournament t");
        }else {
            jpql = new StringBuilder("SELECT COUNT(DISTINCT t.id) FROM Tournament t");
        }
        jpql.append(buildTournamentFilterJpql(tf, params));

        TypedQuery<Long> countQuery = em.createQuery(jpql.toString(), Long.class);
        params.forEach(countQuery::setParameter);

        Long count = countQuery.getSingleResult();

        return (int) Math.ceil((double) count / pageSize);
    }

    @Override
    public void updateTournamentInfo(Long tournamentId, String name, LocalDate startDate, LocalDate endDate, Integer maxParticipants) {
        Tournament t = em.find(Tournament.class, tournamentId);
        if (name != null){
            t.setName(name);
        }if (startDate != null){
            t.setStartDate(startDate);
        }if (endDate != null){
            t.setEndDate(endDate);
        }if (maxParticipants != null){
            t.setMaxParticipants(maxParticipants);
        }
        em.persist(t);
    }

    @Override
    public int getTournamentParticipantsCount(Long tournamentId) {
        String jpql = """
        SELECT COUNT(p)
        FROM Participant p
        WHERE p.tournament = :tournament
          AND (p.user IS NULL OR p.team IS NULL)
    """;

        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("tournament", em.getReference(Tournament.class, tournamentId));

        return query.getSingleResult().intValue();
    }

    @Override
    public void setIsGroupStage(Long tournamentId, Boolean bool) {
        Tournament t = em.find(Tournament.class, tournamentId);
        t.setIsGroupStage(bool);
        em.persist(t);
    }

    @Override
    public Boolean getIsGroupStage(Long tournamentId) {
        Tournament t = em.find(Tournament.class, tournamentId);
        return t.getIsGroupStage();
    }

    @Override
    public void setTournamentWinner(Long tournamentId, Long winner_id) {
        Tournament t = em.find(Tournament.class, tournamentId);
        t.setWinner(em.getReference(Participant.class, winner_id));
        em.persist(t);
    }

    @Override
    public Boolean isTournamentStarted(Long tournamentId) {
        Tournament t = em.find(Tournament.class, tournamentId);
        return t.getTournamentStarted();
    }

    @Override
    public void updateAllStartDates() {
        em.createQuery("""
        UPDATE Tournament t
        SET t.startDate = CURRENT_DATE
        WHERE t.startDate < CURRENT_DATE
          AND COALESCE(t.tournamentStarted, false) = false
    """).executeUpdate();
    }

    @Override
    public void updateAllEndDates() {
        em.createQuery("""
        UPDATE Tournament t
        SET t.endDate = CURRENT_DATE
        WHERE t.endDate < CURRENT_DATE
          AND COALESCE(t.isFinished, false) = false
    """).executeUpdate();
    }

    @Override
    public boolean isClosed(Long tournamentId) {
        Tournament t = em.find(Tournament.class, tournamentId);
        return !t.getOpenInscriptions();
    }

    private int countUserTournaments(Long userId, Boolean isFinished) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(p) " +
                        "FROM Participant p " +
                        "WHERE p.user = :user " +
                        "AND p.tournament IN (" +
                        "SELECT t " +
                        "FROM Tournament t " +
                        "WHERE t.isFinished = :isFinished" +
                        ")",
                Long.class
        );
        query.setParameter("user", em.getReference(User.class, userId));
        query.setParameter("isFinished", isFinished);

        Long count = query.getSingleResult();
        return count.intValue();
    }

    @Override
    public Integer getUserActiveTournamentsPages(Long userId) {
        int count = countUserTournaments(userId, false);
        return (int) Math.ceil(count / 9.0);
    }

    @Override
    public Integer getUserPastTournamentsPages(Long userId) {
        int count = countUserTournaments(userId, true);
        return (int) Math.ceil(count / 9.0);
    }

    @Override
    public Integer getCreatedAndFinishedTournamentsPages(Long userId) {
        int count = countCreatedTournaments(userId, true);
        return (int) Math.ceil(count / 9.0);
    }

    @Override
    public List<Tournament> getUserWonTournament(Long userId, Long page) {
        Query nativeQuery = em.createNativeQuery("SELECT t.id FROM tournament t WHERE tournament_winner = (SELECT p.id FROM participant p WHERE p.user_id = ?1 AND p.tournament_id = t.id)");
        nativeQuery.setParameter(1, userId);
        nativeQuery.setMaxResults(PAGE_SIZE);
        nativeQuery.setFirstResult((int) (page * PAGE_SIZE));

        @SuppressWarnings("unchecked")
        List<Number> rawIds = nativeQuery.getResultList();
        List<Long> ids = rawIds.stream().map(Number::longValue).toList();

        return em.createQuery("SELECT t FROM Tournament t WHERE id in :ids", Tournament.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    public Integer getUserWonTournamentPages(Long userId) {
        Query nativeQuery = em.createNativeQuery("SELECT COUNT(t.id) FROM tournament t WHERE tournament_winner = (SELECT p.id FROM participant p WHERE p.user_id = ?1 AND p.tournament_id = t.id)", Long.class);
        nativeQuery.setParameter(1, userId);
        return nativeQuery.getFirstResult();
    }

    @Override
    public Integer getCreatedAndOngoingTournamentsPages(Long userId) {
        int count = countCreatedTournaments(userId, false);
        return (int) Math.ceil(count / 9.0);
    }

    private int countCreatedTournaments(Long userId, boolean isFinished) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(t) FROM Tournament t WHERE t.creator = :creator AND t.isFinished = :isFinished",
                Long.class
        );
        query.setParameter("creator", em.getReference(User.class, userId));
        query.setParameter("isFinished", isFinished);

        return query.getSingleResult().intValue();
    }

    @Transactional
    @Override
    public void updateTournamentRating(Long tournamentId, Float userRating) {
        em.createQuery("UPDATE Tournament t SET t.rating = :userRating WHERE t.id = :tournamentId")
                .setParameter("userRating", userRating)
                .setParameter("tournamentId", tournamentId)
                .executeUpdate();
    }
}
