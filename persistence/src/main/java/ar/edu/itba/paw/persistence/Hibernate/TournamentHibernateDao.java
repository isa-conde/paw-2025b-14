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

@Repository
public class TournamentHibernateDao implements TournamentDao {

    private static final int PAGE_SIZE = 9;

    private static final int TOP_GAMES_LIMIT = 3;
    private static final int TOURNAMENTS_PER_GAME = 9;

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Tournament> findById(long id) {
        return Optional.ofNullable(em.find(Tournament.class, id));
    }

    @Override
    public List<Tournament> findTournaments(TournamentFilter filter, long page) {
        StringBuilder sql = new StringBuilder("SELECT DISTINCT t.* FROM tournament t");
        Map<String, Object> params = new LinkedHashMap<>();

        // Decidir joins condicionales según filtros (mantenemos modularidad)
        boolean joinGame = filter != null && filter.getGenre() != null;
        boolean joinFormat = filter != null && filter.getPlayersPerTeam() != null;

        if (joinGame) {
            sql.append(" JOIN game g ON t.game_id = g.id");
        }
        if (joinFormat) {
            sql.append(" JOIN game_format gf ON t.format_id = gf.id");
        }

        // Construyo WHERE usando la función modular
        String where = buildTournamentFilterSql(filter, params);
        sql.append(where);

        sql.append(" ORDER BY t.start_date ASC");

        Query nativeQuery = em.createNativeQuery(sql.toString(), Tournament.class);

        // Seteamos parámetros: convertir LocalDate a java.sql.Date por compatibilidad
        for (Map.Entry<String, Object> e : params.entrySet()) {
            Object value = e.getValue();
            if (value instanceof LocalDate) {
                nativeQuery.setParameter(e.getKey(), java.sql.Date.valueOf((LocalDate) value));
            } else {
                nativeQuery.setParameter(e.getKey(), value);
            }
        }

        nativeQuery.setFirstResult((int) (page * PAGE_SIZE));
        nativeQuery.setMaxResults(PAGE_SIZE);

        @SuppressWarnings("unchecked")
        List<Tournament> result = nativeQuery.getResultList();
        return result == null ? Collections.emptyList() : result;
    }

    private String buildTournamentFilterSql(TournamentFilter filter, Map<String, Object> params) {
        StringBuilder where = new StringBuilder(" WHERE t.open_inscriptions = true");

        if (filter == null) return where.toString();

        if (filter.getName() != null && !filter.getName().isEmpty()) {
            where.append(" AND LOWER(t.name) LIKE LOWER(:name)");
            params.put("name", "%" + filter.getName() + "%");
        }
        if (filter.getGameId() != null) {
            where.append(" AND t.game_id = :gameId");
            params.put("gameId", filter.getGameId());
        }
        if (filter.getRegion() != null) {
            where.append(" AND t.region = CAST(:region AS region_enum)");
            params.put("region", filter.getRegion().name());
        }
        if (filter.getElo() != null) {
            where.append(" AND t.elo = CAST(:elo AS elo_enum)");
            params.put("elo", filter.getElo().name());
        }
        if (filter.getFormat() != null && !filter.getFormat().isEmpty()) {
            where.append(" AND t.format = :format");
            params.put("format", filter.getFormat());
        }
        if (filter.getStructure() != null) {
            where.append(" AND t.structure = CAST(:structure AS structure_enum)");
            params.put("structure", filter.getStructure().name());
        }
        if (filter.getStartDate() != null) {
            where.append(" AND t.start_date >= :startDate");
            params.put("startDate", filter.getStartDate());
        }
        if (filter.getEndDate() != null) {
            where.append(" AND t.end_date <= :endDate");
            params.put("endDate", filter.getEndDate());
        }
        if (filter.getPlayersPerTeam() != null) {
            where.append(" AND gf.players_per_team = :playersPerTeam");
            params.put("playersPerTeam", filter.getPlayersPerTeam());
        }
        if (filter.getGenre() != null) {
            where.append(" AND g.genre = CAST(:genre AS genre_enum)");
            params.put("genre", filter.getGenre().name());
        }

        return where.toString();
    }

    @Override
    public Tournament create(long creatorId, String name, long gameId, Region region, Elo elo, LocalDate startDate, LocalDate endDate, String format,
                             Structure structure, int maxParticipants, long imageId, boolean openInscriptions, boolean isFinished, long formatId,
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
    public void setFinished(long tournamentId) {
        Tournament t = em.find(Tournament.class, tournamentId);
        t.setIsFinished(true);
        em.persist(t);
    }

    @Override
    public void closeInscriptions(long tournamentId) {
        Tournament t = em.find(Tournament.class, tournamentId);
        t.setOpenInscriptions(false);
        em.persist(t);
    }

    @Override
    public List<Tournament> searchByName(String name, long page) {

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
    public void startTournament(long tournamentId) {
        Tournament t = em.find(Tournament.class, tournamentId);
        t.setTournamentStarted(true);
        em.persist(t);
    }

    @Override
    public Map<Long, List<Tournament>> getUnfilteredTournamentPages(long page) {
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
    public int getPageAmount(int pageSize, TournamentFilter tf) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be > 0");
        }

        Map<String, Object> params = new LinkedHashMap<>();
        StringBuilder sql = new StringBuilder();

        boolean joinGame = tf != null && tf.getGenre() != null;
        boolean joinFormat = tf != null && tf.getPlayersPerTeam() != null;

        if (tf == null || tf.isEmpty()) {
            sql.append("SELECT COUNT(DISTINCT t.game_id) FROM tournament t");
        } else {
            sql.append("SELECT COUNT(DISTINCT t.id) FROM tournament t");
        }
        if (joinGame) {
            sql.append(" JOIN game g ON t.game_id = g.id");
        }
        if (joinFormat) {
            sql.append(" JOIN game_format gf ON t.format_id = gf.id");
        }
        String where = buildTournamentFilterSql(tf, params);
        sql.append(where);

        Query nativeQuery = em.createNativeQuery(sql.toString());

        for (Map.Entry<String, Object> e : params.entrySet()) {
            Object value = e.getValue();
            if (value instanceof LocalDate) {
                nativeQuery.setParameter(e.getKey(), java.sql.Date.valueOf((LocalDate) value));
            } else {
                nativeQuery.setParameter(e.getKey(), value);
            }
        }

        Number countNumber = (Number) nativeQuery.getSingleResult();
        long count = countNumber == null ? 0L : countNumber.longValue();

        if (count == 0) return 0;
        return (int) Math.ceil((double) count / pageSize);
    }

    @Override
    public void updateTournamentInfo(long tournamentId, String name, LocalDate startDate, LocalDate endDate, Integer maxParticipants, String serverName, String serverPassword, String discordChannel) {
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
    public int getTournamentParticipantsCount(long tournamentId) {
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
    public void setIsGroupStage(long tournamentId, boolean bool) {
        Tournament t = em.find(Tournament.class, tournamentId);
        t.setIsGroupStage(bool);
        em.persist(t);
    }

    @Override
    public Boolean getIsGroupStage(long tournamentId) {
        Tournament t = em.find(Tournament.class, tournamentId);
        return t.getIsGroupStage();
    }

    @Override
    public void setTournamentWinner(long tournamentId, long winnerId) {
        Tournament t = em.find(Tournament.class, tournamentId);
        t.setWinner(em.getReference(Participant.class, winnerId));
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
    public void updateTournamentRating(long tournamentId, float userRating) {
        em.createQuery("UPDATE Tournament t SET t.rating = :userRating WHERE t.id = :tournamentId")
                .setParameter("userRating", userRating)
                .setParameter("tournamentId", tournamentId)
                .executeUpdate();
    }

    @Override
    public List<Tournament> findUserTournaments(long userId, boolean isFinished, boolean isCreator, boolean won, long page) {
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

    @Override
    public int countUserTournaments(long userId, boolean isFinished, boolean isCreator, boolean won) {
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
