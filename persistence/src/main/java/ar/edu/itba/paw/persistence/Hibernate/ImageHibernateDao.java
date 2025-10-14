package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.model.Image;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class ImageHibernateDao implements ImageDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<byte[]> findById(Long id) {
        return Optional.ofNullable(em.find(Image.class, id).getImage());
    }

    @Override
    public Long insertImage(byte[] img) {
        Image i = new Image(img);
        em.persist(i);
        return i.getId();
    }

    @Override
    public void updateImage(Long id, byte[] image) {
        Image i = em.find(Image.class, id);
        i.setImage(image);
        em.persist(i);
    }
}
