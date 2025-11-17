package ar.edu.itba.paw.interfaces.persistence;

import java.util.Optional;

public interface ImageDao {

    Optional<byte[]> findById(long id);

    Long insertImage(byte[] img);

    void updateImage(long id, byte[] image);
}
