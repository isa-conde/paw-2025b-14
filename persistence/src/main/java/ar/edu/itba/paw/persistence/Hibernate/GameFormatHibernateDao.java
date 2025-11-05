package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.GameFormatDao;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class GameFormatHibernateDao implements GameFormatDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void insertFormat(GameFormat gameFormat) {
        em.persist(gameFormat);
    }

    @Override
    public List<GameFormat> getFormats(Long gameId) {
        Game g = em.find(Game.class, gameId);
        return g.getFormats();
    }

    @Override
    public Optional<GameFormat> getFormatById(Long id) {
        return Optional.of(em.find(GameFormat.class, id));
    }

    @Override
    public Integer getPlayersPerTeam(Long id) {
        GameFormat gameFormat = em.find(GameFormat.class, id);
        return gameFormat.getPlayersPerTeam();
    }
}
