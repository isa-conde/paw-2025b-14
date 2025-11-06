package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Rules;

import java.util.Optional;

public interface RulesService {

    Optional<Rules> findById(Long id);

    Rules insertRules(byte[] file);

    void updateRules(Long tournamentId, byte[] file);

}
