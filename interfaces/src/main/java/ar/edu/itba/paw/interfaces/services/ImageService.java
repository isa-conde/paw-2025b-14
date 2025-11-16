package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

public interface ImageService {

    Optional<byte[]> findById(long id);

    Long insertImage(byte[] img);

}
