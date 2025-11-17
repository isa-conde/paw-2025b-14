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
    public Optional<byte[]> findById(long id) {
        Image img = em.find(Image.class,id);
        if(img == null){
            return Optional.empty();
        }
        return Optional.of(img.getImage());
    }

    @Override
    public Long insertImage(byte[] img) {
        Image i = new Image(img);
        em.persist(i);
        return i.getId();
    }

    @Override
    public void updateImage(long id, byte[] image) {
        Image i = em.find(Image.class, id);
        i.setImage(image);
        em.persist(i);
    }
}
