package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Rules;

import java.util.Optional;

public interface RulesDao {

    Optional<Rules> findById(long id);

    Rules insertRules(byte[] file);

    void updateRules(long id, byte[] file);

}
