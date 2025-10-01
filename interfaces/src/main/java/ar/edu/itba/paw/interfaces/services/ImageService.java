package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

public interface ImageService {

    public Optional<byte[]> findById(Long id);

    public Long insertImage(byte[] img);

}
