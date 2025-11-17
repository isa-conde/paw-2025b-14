package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageDao imageDao;

    public ImageServiceImpl(ImageDao imageDao){
        this.imageDao = imageDao;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<byte[]> findById(long id) {
        return imageDao.findById(id);
    }
}
