package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageDao imageDao;

    public ImageServiceImpl(ImageDao imageDao){
        this.imageDao = imageDao;
    }

    @Override
    public Optional<byte[]> findById(Long id) {
        return imageDao.findById(id);
    }

    @Override
    public Long insertImage(byte[] img) {
        return imageDao.insertImage(img);
    }
}
