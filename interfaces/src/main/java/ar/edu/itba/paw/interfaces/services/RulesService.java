package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Rules;

import java.util.Optional;

public interface RulesService {

    Optional<Rules> findById(long id);

    Rules insertRules(byte[] file);

    void updateRules(long tournamentId, byte[] file);

}
