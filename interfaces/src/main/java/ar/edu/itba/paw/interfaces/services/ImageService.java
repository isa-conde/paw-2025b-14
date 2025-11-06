package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

public interface ImageService {

    Optional<byte[]> findById(Long id);

    Long insertImage(byte[] img);

}
