package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

public interface RulesService {

    Optional<byte[]> findById(Long id);

    Long insertRules(byte[] file);

    void updateRules(Long tournament_id, byte[] file);

}
