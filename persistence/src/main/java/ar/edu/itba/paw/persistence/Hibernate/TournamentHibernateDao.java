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
            jpql.append(" AND t.format = :formatId");
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
    public Tournament create(Long creatorId, String name, Long gameId, Region region, Elo elo, LocalDate startDate, LocalDate endDate, String format,
                             Structure structure, Integer maxParticipants, Long imageId, Boolean openInscriptions, Boolean isFinished, Long formatId,
                             Long rulesId, String serverName, String serverPassword, String discordChannel) {
        Tournament t = new Tournament(em.getReference(User.class, creatorId), name, em.getReference(Game.class,  gameId), region, startDate, endDate, format,
                structure, maxParticipants, imageId, openInscriptions, isFinished, em.getReference(GameFormat.class, formatId),
                serverName, serverPassword, discordChannel);
        t.setTournamentStarted(false);
        t.setElo(elo);
        if (rulesId != null){
            t.setRules(em.getReference(Rules.class, rulesId));
        }
        em.persist(t);
        return t;
    }

    @Override
    public void setFinished(Long tournamentId) {
        Tournament t = em.find(Tournament.class, tournamentId);
        t.setIsFinished(true);
        em.persist(t);
    }

    @Override
    public void closeInscriptions(Long tournamentId) {
        Tournament t = em.find(Tournament.class, tournamentId);
        t.setOpenInscriptions(false);
        em.persist(t);
    }

    @Override
    public List<Tournament> searchByName(String name, Long page) {

        Query idQuery = em.createNativeQuery(
                "SELECT t.id " +
                        "FROM tournament t " +
                        "WHERE LOWER(t.name) LIKE CONCAT('%', LOWER(?1), '%') " +
                        "  AND t.tournament_started = false " +
                        "ORDER BY t.start_date ASC"
        );

        idQuery.setParameter(1, name);
        idQuery.setFirstResult((int) (page * PAGE_SIZE));
        idQuery.setMaxResults(PAGE_SIZE);

        @SuppressWarnings("unchecked")
        List<Number> ids = idQuery.getResultList();
        if (ids.isEmpty()) return Collections.emptyList();

        TypedQuery<Tournament> fullQuery = em.createQuery(
                "SELECT DISTINCT t FROM Tournament t " +
                        "WHERE t.id IN :ids " +
                        "ORDER BY t.startDate ASC",
                Tournament.class
        );

        fullQuery.setParameter("ids",
                ids.stream().map(Number::longValue).toList()
        );

        return fullQuery.getResultList();
    }

    @Override
    public int countSearchByName(String name) {

        TypedQuery<Long> countQuery = em.createQuery(
                "SELECT COUNT(t) FROM Tournament t " +
                        "WHERE LOWER(t.name) LIKE CONCAT('%', LOWER(:name), '%') " +
                        "AND t.tournamentStarted = false",
                Long.class
        );

        countQuery.setParameter("name", name);
        long total = countQuery.getSingleResult();

        return (int) Math.ceil((double) total / PAGE_SIZE);
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
        Map<Long,List<Tournament>> ans = new LinkedHashMap<>();
        for(Number game : topGameIds){
            TypedQuery<Tournament> tournamentIdsQuery = em.createQuery(
                    "SELECT t " +
                            "FROM Tournament t " +
                            "WHERE t.game.id = :id " +
                            "AND t.openInscriptions = true " +
                            "ORDER BY t.startDate ASC ", Tournament.class);
            tournamentIdsQuery.setParameter("id", game.longValue());
            tournamentIdsQuery.setMaxResults(TOURNAMENTS_PER_GAME);
            List<Tournament> tournaments = tournamentIdsQuery.getResultList();
            if (!tournaments.isEmpty()){
                ans.put(game.longValue(),tournaments);

            }
        }
        return ans;
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
    public void updateTournamentInfo(Long tournamentId, String name, LocalDate startDate, LocalDate endDate, Integer maxParticipants, String serverName, String serverPassword, String discordChannel) {
        Tournament t = em.find(Tournament.class, tournamentId);
        if (name != null){
            t.setName(name);
        }if (startDate != null){
            t.setStartDate(startDate);
        }if (endDate != null){
            t.setEndDate(endDate);
        }if (maxParticipants != null){
            t.setMaxParticipants(maxParticipants);
        }if (serverName != null){
            t.setServerName(serverName);
        }if (serverPassword != null){
            t.setServerPassword(serverPassword);
        }if (discordChannel != null){
            t.setDiscordChannel(discordChannel);
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
    public void updateTournamentRating(Long tournamentId, Float userRating) {
        em.createQuery("UPDATE Tournament t SET t.rating = :userRating WHERE t.id = :tournamentId")
                .setParameter("userRating", userRating)
                .setParameter("tournamentId", tournamentId)
                .executeUpdate();
    }

    @Override
    public List<Tournament> findUserTournaments(Long userId, Boolean isFinished, Boolean isCreator, Boolean won, Long page) {
        Query nativeQuery;

        if (won) {
            nativeQuery = em.createNativeQuery(
                    "SELECT t.id FROM tournament t " +
                            "WHERE t.tournament_winner = (" +
                            "   SELECT p.id FROM participant p " +
                            "   WHERE p.user_id = ?1 AND p.tournament_id = t.id" +
                            ") " +
                            "ORDER BY t.start_date ASC"
            );
            nativeQuery.setParameter(1, userId);
        } else if (isCreator) {
            nativeQuery = em.createNativeQuery(
                    "SELECT t.id FROM tournament t " +
                            "WHERE t.creator_id = ?1 AND t.is_finished = ?2 " +
                            "ORDER BY t.start_date ASC"
            );
            nativeQuery.setParameter(1, userId);
            nativeQuery.setParameter(2, isFinished);
        } else {
            nativeQuery = em.createNativeQuery(
                    "SELECT t.id FROM tournament t " +
                            "WHERE t.is_finished = ?1 " +
                            "  AND t.id IN (" +
                            "       SELECT p.tournament_id FROM participant p " +
                            "       WHERE p.user_id = ?2" +
                            "  ) " +
                            "ORDER BY t.start_date ASC"
            );
            nativeQuery.setParameter(1, isFinished);
            nativeQuery.setParameter(2, userId);
        }

        nativeQuery.setFirstResult((int)(page * PAGE_SIZE));
        nativeQuery.setMaxResults(PAGE_SIZE);

        @SuppressWarnings("unchecked")
        List<Number> tournamentIds = nativeQuery.getResultList();
        if (tournamentIds.isEmpty()) return Collections.emptyList();

        TypedQuery<Tournament> fullQuery = em.createQuery(
                "SELECT DISTINCT t FROM Tournament t " +
                        "WHERE t.id IN :ids " +
                        "ORDER BY t.startDate ASC",
                Tournament.class
        );
        fullQuery.setParameter("ids",
                tournamentIds.stream().map(Number::longValue).toList()
        );

        return fullQuery.getResultList();
    }

    public int countUserTournaments(Long userId, Boolean isFinished, Boolean isCreator, Boolean won) {
        Long count;

        if (won) {
            count = em.createQuery(
                            "SELECT COUNT(t) FROM Tournament t " +
                                    "WHERE t.winner = (" +
                                    "   SELECT p FROM Participant p " +
                                    "   WHERE p.user.id = :userId AND p.tournament = t" +
                                    ")",
                            Long.class
                    )
                    .setParameter("userId", userId)
                    .getSingleResult();
        } else if (isCreator) {
            count = em.createQuery(
                            "SELECT COUNT(t) FROM Tournament t " +
                                    "WHERE t.creator.id = :userId AND t.isFinished = :isFinished",
                            Long.class
                    )
                    .setParameter("userId", userId)
                    .setParameter("isFinished", isFinished)
                    .getSingleResult();
        } else {
            count = em.createQuery(
                            "SELECT COUNT(p) FROM Participant p " +
                                    "WHERE p.user.id = :userId " +
                                    "AND p.tournament.isFinished = :isFinished",
                            Long.class
                    )
                    .setParameter("userId", userId)
                    .setParameter("isFinished", isFinished)
                    .getSingleResult();
        }
        int totalElements = count.intValue();
        return (int) Math.ceil((double) totalElements / PAGE_SIZE);
    }


}
