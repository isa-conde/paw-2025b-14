package ar.edu.itba.paw.interfaces.persistence;

import java.util.Optional;

public interface ImageDao {

    Optional<byte[]> findById(Long id);

    Integer insertImage(byte[] img);

    void updateImage(Long id, byte[] image);
}
