package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Rules;

import java.util.Optional;

public interface RulesDao {

    Optional<Rules> findById(Long id);

    Rules insertRules(byte[] file);

    void updateRules(Long id, byte[] file);

}
