package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.GameDao;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.enums.Genre;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class GameHibernateDao implements GameDao {

    @PersistenceContext
    private EntityManager em;

    private static final int GRID_PAGE_SIZE = 9;

    @Override
    public Optional<Game> findById(long id) {
        return Optional.ofNullable(em.find(Game.class, id));
    }

    @Override
    public List<Game> searchByName(String name) {
        TypedQuery<Game> query = em.createQuery("SELECT g FROM Game g WHERE LOWER(g.name) LIKE CONCAT('%', LOWER(:name), '%')", Game.class);
        query.setParameter("name", name);
        return query.getResultList();
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
    public Game create(String name, Genre genre, int imageId) {
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
    public List<Game> getFavourites(long userId) {
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
    public List<Game> findAllPaged(long page) {
        int pageSize = GRID_PAGE_SIZE;
        int offset = (int) (page * pageSize);

        Query idQuery = em.createNativeQuery("SELECT DISTINCT (id) FROM game ORDER BY id ASC ");
        idQuery.setFirstResult(offset);
        idQuery.setMaxResults(pageSize);

        @SuppressWarnings("unchecked")
        List<Long> ids = idQuery.getResultList().stream()
                .map(it -> ((Number) it).longValue()).toList();


        return em.createQuery(
                        "SELECT g FROM Game g WHERE g.id IN (:ids) ORDER BY g.id", Game.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    public long getPageAmount() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(g) FROM Game g", Long.class);
        long totalGames = query.getSingleResult();

        return (long) Math.ceil((double) (totalGames) / GRID_PAGE_SIZE);
    }

}
