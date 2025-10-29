package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.RulesDao;
import ar.edu.itba.paw.model.Rules;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
public class RulesHibernateDao implements RulesDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Rules> findById(Long id) {
        return Optional.ofNullable(em.find(Rules.class, id));
    }

    @Override
    public Rules insertRules(byte[] file) {
        Rules r = new Rules(file);
        em.persist(r);
        return r;
    }

    @Override
    public void updateRules(Long id, byte[] file) {
        Rules r = em.find(Rules.class, id);
        r.setFile(file);
        em.persist(r);
    }
}
