package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.GameFormatDao;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class GameFormatHibernateDao implements GameFormatDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<GameFormat> getFormats(long gameId) {
        Game g = em.find(Game.class, gameId);
        return g.getFormats();
    }

    @Override
    public Optional<GameFormat> findById(long id) {
        return Optional.of(em.find(GameFormat.class, id));
    }
}
