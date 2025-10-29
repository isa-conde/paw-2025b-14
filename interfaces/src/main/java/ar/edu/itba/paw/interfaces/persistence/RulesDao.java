package ar.edu.itba.paw.interfaces.persistence;

import java.util.Optional;

public interface RulesDao {

    Optional<byte[]> findById(Long id);

    Long insertRules(byte[] file);

    void updateRules(Long id, byte[] file);

}
