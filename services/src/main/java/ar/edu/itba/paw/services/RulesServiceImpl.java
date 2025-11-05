package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.RulesDao;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.RulesService;
import ar.edu.itba.paw.model.Rules;
import ar.edu.itba.paw.model.Tournament;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RulesServiceImpl implements RulesService {

    RulesDao rulesDao;
    TournamentDao  tournamentDao;

    RulesServiceImpl(RulesDao rulesDao, TournamentDao tournamentDao){
        this.rulesDao = rulesDao;
        this.tournamentDao = tournamentDao;
    }

    @Override
    public Optional<Rules> findById(Long id) {
        return rulesDao.findById(id);
    }

    @Override
    @Transactional
    public Rules insertRules(byte[] file) {
        return rulesDao.insertRules(file);
    }

    @Transactional
    @Override
    public void updateRules(Long tournamentId, byte[] file) {
        Tournament t = tournamentDao.findById(tournamentId).get();
        Rules r = t.getRules();
        if (r != null){
            rulesDao.updateRules(r.getId(), file);
        }else {
            r = insertRules(file);
            t.setRules(r);
        }

    }
}
