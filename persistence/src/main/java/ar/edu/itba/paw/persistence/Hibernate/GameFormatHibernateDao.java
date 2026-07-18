package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.GameFormatDao;
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
        return em.createQuery(
                        "SELECT gf FROM GameFormat gf WHERE gf.game.id = :gameId ORDER BY gf.id",
                        GameFormat.class)
                .setParameter("gameId", gameId)
                .getResultList();
    }

    @Override
    public GameFormat findById(long id) {
        return em.find(GameFormat.class, id);
    }
}
