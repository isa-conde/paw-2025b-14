package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.GameDao;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.enums.Genre;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class GameHibernateDao implements GameDao {

    @PersistenceContext
    private EntityManager em;

    private static final int PAGE_SIZE = 9;

    @Override
    public Optional<Game> findById(long id) {
        return Optional.ofNullable(em.find(Game.class, id));
    }

    @Override
    public List<Game> searchByName(String name, Long page) {

        Query idQuery = em.createNativeQuery(
                "SELECT g.id " +
                        "FROM game g " +
                        "WHERE LOWER(g.name) LIKE CONCAT('%', LOWER(?1), '%') " +
                        "ORDER BY g.name ASC"
        );

        idQuery.setParameter(1, name);
        idQuery.setFirstResult((int) (page * PAGE_SIZE));
        idQuery.setMaxResults(PAGE_SIZE);

        @SuppressWarnings("unchecked")
        List<Number> ids = idQuery.getResultList();
        if (ids.isEmpty()) return Collections.emptyList();

        TypedQuery<Game> fullQuery = em.createQuery(
                "SELECT DISTINCT g FROM Game g " +
                        "WHERE g.id IN :ids " +
                        "ORDER BY g.name ASC",
                Game.class
        );

        fullQuery.setParameter("ids",
                ids.stream().map(Number::longValue).toList()
        );

        return fullQuery.getResultList();
    }

    @Override
    public int countSearchByNameGame(String name) {

        TypedQuery<Long> countQuery = em.createQuery(
                "SELECT COUNT(g) FROM Game g " +
                        "WHERE LOWER(g.name) LIKE CONCAT('%', LOWER(:name), '%')",
                Long.class
        );

        countQuery.setParameter("name", name);
        long total = countQuery.getSingleResult();

        return (int) Math.ceil((double) total / PAGE_SIZE);
    }


    @Override
    public List<Game> searchByGenre(Genre genre) {
        TypedQuery<Game> query = em.createQuery("SELECT g FROM Game g WHERE g.genre = :genre", Game.class);
        query.setParameter("genre", genre);
        return query.getResultList();
    }

    @Override
    public List<Game> findAll() {
        TypedQuery<Game> query = em.createQuery("SELECT g FROM Game g", Game.class);
        return query.getResultList();
    }

    @Override
    public Game create(String name, Genre genre, Integer imageId) {
        Game game = new Game(name, genre, imageId);
        em.persist(game);
        return game;
    }

    @Override
    public boolean checkNameExists(String name) {
        TypedQuery<Game> query = em.createQuery("SELECT g FROM Game g WHERE g.name = :name", Game.class);
        query.setParameter("name", name);
        return !query.getResultList().isEmpty();
    }


    @Override
    public List<Game> getFavourites(Long userId) {
        TypedQuery<Game> q = em.createQuery("""
            SELECT g
            FROM Tournament t
            JOIN t.game g
            WHERE EXISTS (
                SELECT 1 FROM Participant p
                WHERE p.tournament = t AND p.user.id = :uid
            )
            GROUP BY g
            ORDER BY COUNT(DISTINCT t) DESC, g.name ASC
        """, Game.class);
        q.setParameter("uid", userId);
        q.setMaxResults(6);
        return q.getResultList();
    }

    @Override
    public List<Game> findAllPaged(Long page) {
        int offset = (int) (page * PAGE_SIZE);

        Query idQuery = em.createNativeQuery("SELECT DISTINCT (id) FROM game ORDER BY id ASC ");
        idQuery.setFirstResult(offset);
        idQuery.setMaxResults(PAGE_SIZE);

        @SuppressWarnings("unchecked")
        List<Long> ids = idQuery.getResultList().stream()
                .map(it -> ((Number) it).longValue()).toList();


        return em.createQuery(
                        "SELECT g FROM Game g WHERE g.id IN (:ids) ORDER BY g.id", Game.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    public Long getPageAmount() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(g) FROM Game g", Long.class);
        Long totalGames = query.getSingleResult();

        return (long) Math.ceil((double) (totalGames) / PAGE_SIZE);
    }

}
