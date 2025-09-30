package ar.edu.itba.paw.interfaces.persistence;

import java.util.Optional;

public interface ImageDao {

    public Optional<byte[]> findById(Long id);

}
