package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.GameDao;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.User;
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
        TypedQuery<Game> query = em.createQuery("SELECT g FROM Game g WHERE g.name LIKE CONCAT('%', LOWER(:name), '%')", Game.class);
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
    public Game create(String name, Genre genre, Integer image_id) {
        Game game = new Game(name, genre, image_id);
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
    public void addFavourite(Long user_id, Long game_id) {
        User user = em.find(User.class, user_id);
        Game game = em.find(Game.class, game_id);

        user.getFavoriteGames().add(game);
        em.persist(user);
    }

    @Override
    public List<Game> getFavourites(Long user_id) {
        return em.find(User.class, user_id).getFavoriteGames();
    }

    @Override
    public List<Game> findAllPaged(Long page) {
        TypedQuery<Game> query = em.createQuery("SELECT g FROM Game g", Game.class);
        query.setFirstResult((int) (page * GRID_PAGE_SIZE));
        query.setMaxResults(GRID_PAGE_SIZE);

        return query.getResultList();
    }

    @Override
    public Long getPageAmount() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(g) FROM Game g", Long.class);
        Long totalGames = query.getSingleResult();

        return (long) Math.ceil((double) (totalGames) / GRID_PAGE_SIZE);
    }

}
