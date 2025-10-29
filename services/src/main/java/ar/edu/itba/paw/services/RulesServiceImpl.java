package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.RulesDao;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.RulesService;
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
    public Optional<byte[]> findById(Long id) {
        return rulesDao.findById(id);
    }

    @Override
    @Transactional
    public Long insertRules(byte[] file) {
        return rulesDao.insertRules(file);
    }

    @Transactional
    @Override
    public void updateRules(Long tournament_id, byte[] file) {
        Long id = tournamentDao.findById(tournament_id).get().getRules().getId();
        rulesDao.updateRules(id, file);
    }
}
