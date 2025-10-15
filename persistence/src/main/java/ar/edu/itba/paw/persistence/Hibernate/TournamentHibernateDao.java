package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
        StringBuilder idJpql = new StringBuilder("SELECT t.id FROM Tournament t");
        Map<String, Object> params = new HashMap<>();

        idJpql.append(buildTournamentFilterJpql(tournamentFilter, params));

        TypedQuery<Long> idQuery = em.createQuery(idJpql.toString(), Long.class);
        params.forEach(idQuery::setParameter);

        idQuery.setFirstResult((int) (page * 9));
        idQuery.setMaxResults(9);

        List<Long> tournamentIds = idQuery.getResultList();

        if (tournamentIds.isEmpty()) return Collections.emptyList();

        TypedQuery<Tournament> query = em.createQuery(
                "SELECT DISTINCT t FROM Tournament t " +
                        "WHERE t.id IN :ids " +
                        "ORDER BY t.start_date ASC", Tournament.class
        );
        query.setParameter("ids", tournamentIds);

        return query.getResultList();
    }

    private String buildTournamentFilterJpql(TournamentFilter filter, Map<String, Object> params) {
        StringBuilder jpql = new StringBuilder(" WHERE t.open_inscriptions = true");

        if (filter.getName() != null) {
            jpql.append(" AND LOWER(t.name) LIKE LOWER(:name)");
            params.put("name", "%" + filter.getName() + "%");
        }
        if (filter.getGame_id() != null) {
            jpql.append(" AND t.game.id = :gameId");
            params.put("gameId", filter.getGame_id());
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
        if (filter.getStart_date() != null) {
            jpql.append(" AND t.startDate <= :startDate");
            params.put("startDate", filter.getStart_date());
        }
        if (filter.getEnd_date() != null) {
            jpql.append(" AND t.endDate <= :endDate");
            params.put("endDate", filter.getEnd_date());
        }
        if (filter.getPlayersPerTeam() != null) {
            jpql.append(" AND t.formatEntity.players_per_team = :playersPerTeam");
            params.put("playersPerTeam", filter.getPlayersPerTeam());
        }
        if (filter.getGenre() != null) {
            jpql.append(" AND t.game.genre = :genre");
            params.put("genre", filter.getGenre());
        }

        return jpql.toString();
    }

    @Override
    public Tournament create(Long creator_id, String name, Long game_id, Region region, Elo elo, LocalDate start_date, LocalDate end_date, String format, Structure structure, Integer max_participants, Long image_id, Boolean openInscriptions, Boolean isFinished, Long format_id) {
        Tournament t = new Tournament(em.getReference(User.class, creator_id), name, em.getReference(Game.class,  game_id), region, start_date, end_date, format, structure, max_participants, image_id, openInscriptions, isFinished, em.getReference(GameFormat.class, format_id));
        t.setTournament_started(false);
        t.setElo(elo);
        em.persist(t);
        return t;
    }

    @Override
    public Structure getTournamentStructure(Long tournament_id) {
        Tournament t = em.find(Tournament.class, tournament_id);
        return t.getStructure();
    }

    @Override
    public List<Tournament> findByCreator(Long creator_id, Long page) {
        TypedQuery<Long> idQuery = em.createQuery(
                "SELECT t.id FROM Tournament t WHERE t.creator = :creator ORDER BY t.start_date ASC",
                Long.class
        );
        idQuery.setParameter("creator", em.getReference(User.class, creator_id));
        idQuery.setFirstResult((int) (page * 9));
        idQuery.setMaxResults(9);

        List<Long> tournamentIds = idQuery.getResultList();

        if (tournamentIds.isEmpty()) return Collections.emptyList();

        TypedQuery<Tournament> fullQuery = em.createQuery(
                "SELECT DISTINCT t FROM Tournament t " +
                        "WHERE t.id IN :ids " +
                        "ORDER BY t.start_date ASC",
                Tournament.class
        );
        fullQuery.setParameter("ids", tournamentIds);

        return fullQuery.getResultList();
    }

    @Override
    public void setFinished(Long tournament_id) {
        Tournament t = em.find(Tournament.class, tournament_id);
        t.setIs_finished(true);
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
        TypedQuery<Long> idQuery = em.createQuery(
                "SELECT t.id " +
                    "FROM Tournament t " +
                    "WHERE t.is_finished = :isFinished " +
                    "  AND t IN ( " +
                        "    SELECT p.tournament " +
                        "    FROM Participant p " +
                        "    WHERE p.user = :user " +
                    ") " +
                    "ORDER BY t.start_date ASC",
                Long.class
        );
        idQuery.setParameter("user", em.getReference(User.class, userId));
        idQuery.setParameter("isFinished", isFinished);
        idQuery.setFirstResult((int)(page * PAGE_SIZE));
        idQuery.setMaxResults(PAGE_SIZE);

        List<Long> tournamentIds = idQuery.getResultList();
        if (tournamentIds.isEmpty()) return Collections.emptyList();

        TypedQuery<Tournament> fullQuery = em.createQuery(
                "SELECT DISTINCT t FROM Tournament t " +
                        "WHERE t.id IN :ids " +
                        "ORDER BY t.start_date ASC",
                Tournament.class
        );
        fullQuery.setParameter("ids", tournamentIds);

        return fullQuery.getResultList();
    }

    @Override
    public void closeInscriptions(Long tournament_id) {
        Tournament t = em.find(Tournament.class, tournament_id);
        t.setOpen_inscriptions(false);
        em.persist(t);
    }

    @Override
    public List<Tournament> searchByName(String name) {
        TypedQuery<Tournament> query = em.createQuery("SELECT t FROM Tournament t WHERE t.name LIKE CONCAT('%', LOWER(:name), '%')", Tournament.class);
        query.setParameter("name", name);
        return query.getResultList();
    }

    @Override
    public void startTournament(Long tournament_id) {
        Tournament t = em.find(Tournament.class, tournament_id);
        t.setTournament_started(true);
        em.persist(t);
    }

    @Override
    public Map<Long, List<Tournament>> getUnfilteredTournamentPages(Long page) {
        TypedQuery<Long> topGamesQuery = em.createQuery(
                "SELECT g.id " +
                        "FROM Tournament t " +
                        "JOIN t.game g " +
                        "GROUP BY g.id " +
                        "ORDER BY COUNT(t) DESC",
                Long.class
        );
        topGamesQuery.setFirstResult((int)(page * TOP_GAMES_LIMIT));
        topGamesQuery.setMaxResults(TOP_GAMES_LIMIT);

        List<Long> topGameIds = topGamesQuery.getResultList();
        if (topGameIds.isEmpty()) return Collections.emptyMap();

        TypedQuery<Long> tournamentIdsQuery = em.createQuery(
                "SELECT t.id " +
                        "FROM Tournament t " +
                        "WHERE t.game.id IN :gameIds " +
                        "AND t.open_inscriptions = true " +
                        "ORDER BY t.game.id, t.start_date ASC",
                Long.class
        );
        tournamentIdsQuery.setParameter("gameIds", topGameIds);

        List<Long> tournamentIds = tournamentIdsQuery.getResultList();
        if (tournamentIds.isEmpty()) return Collections.emptyMap();

        TypedQuery<Tournament> fullQuery = em.createQuery(
                "SELECT DISTINCT t FROM Tournament t " +
                        "LEFT JOIN FETCH t.game g " +
                        "WHERE t.id IN :ids " +
                        "ORDER BY t.game.id, t.start_date ASC",
                Tournament.class
        );
        fullQuery.setParameter("ids", tournamentIds);

        List<Tournament> tournaments = fullQuery.getResultList();
        return tournaments.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getGame().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    @Override
    public Integer getPageAmount(Integer pageSize, TournamentFilter tf) {
        Map<String, Object> params = new HashMap<>();

        StringBuilder jpql = new StringBuilder("SELECT ");
        if (tf.getGame_id() != null) {
            jpql.append("COUNT(t) ");
        } else {
            jpql.append("COUNT(DISTINCT t.game.id) ");
        }

        jpql.append("FROM Tournament t");

        jpql.append(buildTournamentFilterJpql(tf, params));

        TypedQuery<Long> countQuery = em.createQuery(jpql.toString(), Long.class);
        params.forEach(countQuery::setParameter);

        Long count = countQuery.getSingleResult();

        return (int) Math.ceil((double) count / pageSize);
    }

    @Override
    public void updateTournamentInfo(Long tournament_id, String name, LocalDate start_date, LocalDate end_date, Integer max_participants) {
        Tournament t = em.find(Tournament.class, tournament_id);
        if (name != null){
            t.setName(name);
        }if (start_date != null){
            t.setStart_date(start_date);
        }if (end_date != null){
            t.setEnd_date(end_date);
        }if (max_participants != null){
            t.setMax_participants(max_participants);
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
        t.setIs_group_stage(bool);
        em.persist(t);
    }

    @Override
    public Boolean getIsGroupStage(Long tournamentId) {
        Tournament t = em.find(Tournament.class, tournamentId);
        return t.getIs_group_stage();
    }

    @Override
    public void setTournamentWinner(Long tournament_id, Long user_id) {
        Tournament t = em.find(Tournament.class, tournament_id);
        t.setWinner(em.getReference(User.class, user_id));
        em.persist(t);
    }

    @Override
    public Boolean isTournamentStarted(Long tournament_id) {
        Tournament t = em.find(Tournament.class, tournament_id);
        return t.getTournamentStarted();
    }

    @Override
    public void updateAllStartDates() {
        em.createQuery("""
        UPDATE Tournament t
        SET t.start_date = CURRENT_DATE
        WHERE t.start_date < CURRENT_DATE
          AND COALESCE(t.tournament_started, false) = false
    """).executeUpdate();
    }

    @Override
    public void updateAllEndDates() {
        em.createQuery("""
        UPDATE Tournament t
        SET t.end_date = CURRENT_DATE
        WHERE t.end_date < CURRENT_DATE
          AND COALESCE(t.is_finished, false) = false
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
                        "WHERE t.is_finished = :isFinished" +
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
    public Integer getCreatedAndOngoingTournamentsPages(Long userId) {
        int count = countCreatedTournaments(userId, false);
        return (int) Math.ceil(count / 9.0);
    }

    private int countCreatedTournaments(Long userId, boolean isFinished) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(t) FROM Tournament t WHERE t.creator = :creator AND t.is_finished = :isFinished",
                Long.class
        );
        query.setParameter("creator", em.getReference(User.class, userId));
        query.setParameter("isFinished", isFinished);

        return query.getSingleResult().intValue();
    }

}
